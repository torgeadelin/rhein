package rhein

import scala.collection.mutable.ListBuffer
// Listener trait
// must implement a unlisten method
trait Listener {
  def unlisten()
}

// TransactionHandler[A]
// must implement a run method
trait TransactionHandler[A] {
  def run(trans: Transaction, a: A)
}

// Also known as Stream
// Stream of events that fire at discrete times
class Event[T]() {
  // list with listeners on this event
  protected var listeners = new ListBuffer[TransactionHandler[T]]()
  // collects all listeners that need to be removed when this event gets killed
  protected var finalizers = new ListBuffer[Listener]()
  // Each Event has a Node object
  var node: Node = new Node(0L);

  def sampleNow(): IndexedSeq[T] = IndexedSeq()

  // only used in the context of EventLoops
  protected val firings = ListBuffer[T]()

  final class ListenerImplementation[T](
      event: Event[T],
      action: TransactionHandler[T],
      target: Node
  ) extends Listener {

    def unlisten() = {
      event.listeners -= action
      event.node.unlinkTo(target)
    }

    override protected def finalize() = {
      unlisten()
    }
  }

  def listen(action: Handler[T]): Listener = {
    listen(Node.NullNode, (trans: Transaction, a: T) => { action.run(a) })
  }

  def listen(target: Node, action: TransactionHandler[T]): Listener = {
    Transaction.evaluate((trans: Transaction) => listen(target, trans, action))
  }

  def listen(
      target: Node,
      trans: Transaction,
      action: TransactionHandler[T]
  ): Listener = {
    node.linkTo(target)
    listeners += action
    new ListenerImplementation[T](this, action, target)
  }

  // Map Primitive
  def map[B](f: T => B): Event[B] = {
    val out: EventSink[B] = new EventSink[B]();
    val l: Listener = listen(out.node, (trans: Transaction, a: T) => {
      out.send(trans, f.apply(a));
    })
    out.addCleanup(l)
  }

  // Hold Primitive
  final def hold(initValue: T): Behaviour[T] = {
    Transaction.evaluate(trans => new Behaviour[T](this, Some(initValue)))
  }

  // Filter Primitive
  def filter(f: T => Boolean): Event[T] = {
    val ev = this
    val out = new EventSink[T]() {
      // override def sampleNow() = ev.sampleNow().filter(f)
    }
    val l = listen(out.node, new TransactionHandler[T]() {
      def run(trans: Transaction, a: T) {
        if (f(a)) out.send(trans, a)
      }
    })
    out.addCleanup(l)
  }

  // Snapshot Primitive
  def snapshot[B, C](b: Behaviour[B], f: (T, B) => C): Event[C] = {
    val ev = this
    val out = new EventSink[C]() {
      // override def sampleNow() =
      //   ev.sampleNow().map(a => f.apply(a, b.sampleNoTrans()))
    }

    val l: Listener = listen(out.node, new TransactionHandler[T]() {
      def run(trans: Transaction, a: T) {
        out.send(trans, f(a, b.sampleNoTrans()))
        //println(b.sampleNoTrans)
      }
    })

    out.addCleanup(l)
  }

  def addCleanup(l: Listener): Event[T] = {
    finalizers += l
    this
  }

  // Removes the listeners while this event is killed
  override def finalize() {
    finalizers.foreach(_.unlisten)
  }
}

object Event {
  // Merge primitive
  def merge[T](ea: Event[T], eb: Event[T]): Event[T] = {
    val out: EventSink[T] = new EventSink[T]() {

      // override def sampleNow(): IndexedSeq[T] =
      // ea.sampleNow() ++ eb.sampleNow()
    }
    val h = new TransactionHandler[T]() {
      def run(trans: Transaction, a: T) {
        out.send(trans, a)
      }
    }

    val l1 = ea.listen(out.node, h)
    val l2 = eb.listen(
      out.node,
      new TransactionHandler[T]() {
        def run(trans1: Transaction, a: T) {
          trans1.prioritized(out.node, new Handler[Transaction]() {
            def run(trans2: Transaction) {
              out.send(trans2, a)
            }
          })
        }
      }
    )
    out.addCleanup(l1).addCleanup(l2)
  }
}
