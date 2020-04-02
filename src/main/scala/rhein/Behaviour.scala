package rhein

// Behaviour - time varying value
// Differentiates from an Event by always having a value => continuous
class Behaviour[T](var event: Event[T], var value: Option[T]) {
  // fires once with the behaviour current value in the transaction where it was invoked
  var valueUpdate: Option[T] = None
  var cleanup: Option[Listener] = None

  // Auxiliary constructor
  def this(value: Option[T]) {
    this(new Event[T](), value)
  }

  // Creates the behaviour dependency
  // Using a Transaction
  Transaction.evaluate((trans1: Transaction) => {
    this.cleanup = Some(
      event.listen(
        Node.NullNode,
        trans1,
        (trans2: Transaction, a: T) => {
          if (Behaviour.this.valueUpdate.isEmpty) {
            trans2.last(new Runnable() {
              def run() {
                Behaviour.this.value = valueUpdate
                Behaviour.this.valueUpdate = None
              }
            })
          }
          this.valueUpdate = Some(a)
        }
      )
    )
  })

  // Sample the value without a transaction
  def sampleNoTrans(): T = value.get

  // Get the new value update
  def newValue(): T = {
    valueUpdate.getOrElse(sampleNoTrans)
  }

  // Returns an event with all changes that have been made to this
  // Behaviour
  // gives you the discrete updates to a behaviour.
  // effectively the inverse of hold()
  // returns the firing only after you start listening not from the beginning
  // helps with operational code

  def changes(): Event[T] = {
    event
  }

  // Map Primitive
  final def map[B](f: T => B): Behaviour[B] = {
    changes().map(f).hold(f.apply(sampleNoTrans()))
  }

  final def mapList[B](f: T => B): List[B] = {
    sampleNoTrans().asInstanceOf[List[T]].map(f)
  }

  // Lift Primitive
  final def lift[B, C](b: Behaviour[B], f: (T, B) => C): Behaviour[C] = {
    def ffa(aa: T)(bb: B) = f(aa, bb)
    Behaviour.apply(map(ffa), b)
  }

  def values(): Event[T] = {
    new Event[T]() {
      override def listen(
          target: Node,
          trans: Transaction,
          action: TransactionHandler[T]
      ): Listener = {
        action.run(trans, value.get)
        changes().listen(target, trans, action)
      }
    }
  }

  override def finalize() = {
    cleanup.get.unlisten
  }

}

object Behaviour {
  def apply[T, B](bf: Behaviour[T => B], ba: Behaviour[T]): Behaviour[B] = {
    val out = new EventSink[B]()

    var fired = false
    def h(trans: Transaction) {
      if (!fired) {
        fired = true
        trans.prioritized(out.node, { trans2 =>
          out.send(trans2, bf.newValue().apply(ba.newValue()))
          fired = false
        })
      }
    }
    val l1 = bf
      .changes()
      .listen(out.node, new TransactionHandler[T => B]() {
        def run(trans: Transaction, f: T => B) {
          h(trans)
        }
      })
    val l2 = ba
      .changes()
      .listen(out.node, new TransactionHandler[T]() {
        def run(trans: Transaction, a: T) {
          h(trans)
        }
      })
    out
      .addCleanup(l1)
      .addCleanup(l2)
      .hold(bf.sampleNoTrans().apply(ba.sampleNoTrans()))
  }
}
