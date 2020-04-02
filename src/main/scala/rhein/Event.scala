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

  // only used in the context of EventLoops
  protected val firings = ListBuffer[T]()

  // Listener implementation.
  // when creating a new listener on an event, it returns an instance of this class/
  // and the listener can be closed/killed
  // cannot be extended
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

  // listener without providing a node (action is the code to be executed)
  /**
    * Listen for firings of this event. The returned Listener has an unlisten()
    * method to cause the listener to be removed. This is the observer pattern.
    */
  def listen(action: Handler[T]): Listener = {
    listen(Node.NullNode, (trans: Transaction, a: T) => { action.run(a) })
  }

  // listener providing a node and a handler (action is the code to be executed)
  // this function wraps the listener in a transaction?
  def listen(target: Node, action: TransactionHandler[T]): Listener = {
    Transaction.evaluate((trans: Transaction) => listen(target, trans, action))
  }

  // attaching a listener to this event.
  // step 1 - linking this node to the new target node
  // step 2 - adding the action to the list  of transaction handlers called listeners (functions -- the code
  // that is given in the listener)
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
    val out = new EventSink[T]()
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
    val out = new EventSink[C]()

    val l: Listener = listen(out.node, new TransactionHandler[T]() {
      def run(trans: Transaction, a: T) {
        out.send(trans, f(a, b.sampleNoTrans()))
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
    val out: EventSink[T] = new EventSink[T]()
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
