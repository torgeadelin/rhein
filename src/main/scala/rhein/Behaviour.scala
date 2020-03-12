package rhein

class Behaviour[T](var event: Event[T], var value: Option[T]) {
  var valueUpdate: Option[T] = None
  var cleanup: Option[Listener] = None

  def this(value: Option[T]) {
    this(new Event[T](), value)
  }

  Transaction.run((trans1: Transaction) => {
    this.cleanup = Some(
      event.listen(
        Node.NullNode,
        trans1,
        (trans2: Transaction, a: T) => {
          if (this.valueUpdate == null) {
            trans2.last(() => {
              this.value = this.valueUpdate
              this.valueUpdate = None
            })
          }
          this.valueUpdate = Some(a)
        }
      )
    )
  })

//   def listen(action: T => Unit): Listener =
//     Transaction(trans => value(trans).listen(action))

  def sampleNoTrans(): T = value.get

  //Should be different
  def newValue(): T = {
    valueUpdate.get
  }

  def changes(): Event[T] = {
    event
  }

  def map[B](f: T => B): Behaviour[B] = {
    changes().map(f).hold(f.apply(sampleNoTrans()))
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
