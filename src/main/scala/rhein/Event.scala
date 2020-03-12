package rhein

import scala.collection.mutable.ListBuffer
trait Listener {
    def unlisten()
}

trait TransactionHandler[A] {
    def run(trans : Transaction, a: A)
}

// Also known as Stream
class Event[T]() {
    
    protected var listeners = new ListBuffer[TransactionHandler[T]]()
	protected var finalizers = new ListBuffer[Listener]()
    var node: Node = new Node(0L);

    final class ListenerImplementation[T](event: Event[T], action: TransactionHandler[T], target: Node) extends Listener {

        def unlisten() = {
			event.listeners -= action
			event.node.unlinkTo(target)
		}

        override protected def finalize() = {
			unlisten()
		}

    } 

    def listen(action: Handler[T] ): Listener = {
		listen(Node.NullNode, (trans: Transaction, a: T) => { action.run(a) })
	}

    def listen(target: Node, action: TransactionHandler[T]): Listener = {
		Transaction.evaluate((trans: Transaction) => listen(target, trans, action))
	}

    def listen(target: Node, trans: Transaction, action: TransactionHandler[T]): Listener =  {
		node.linkTo(target)
		listeners += action
		new ListenerImplementation[T](this, action, target)
	}

    def map[B](f : T => B): Event[B] = {
		val out: EventSink[B] = new EventSink[B]();
		val l: Listener = listen(out.node, (trans: Transaction, a: T) => {
			out.send(trans, f.apply(a));
		})
		out.addCleanup(l)
	}

    def addCleanup(l: Listener): Event[T] = {
		finalizers += l
		this
	}

    def hold(initValue: T): Behaviour[T] = {
        new Behaviour[T](this, Some(initValue))
    }

    def snapshot[B, C](b: Behaviour[B], f: (T, B) => C):Event[C] = {
        val out: EventSink[C] = new EventSink[C]();

        val l:Listener = listen(out.node, (trans: Transaction, a: T) => {
			out.send(trans, f.apply(a, b.sampleNoTrans))
		})

        out.addCleanup(l)
    }

    override def finalize() {
        finalizers.foreach(_.unlisten)
    }
}



