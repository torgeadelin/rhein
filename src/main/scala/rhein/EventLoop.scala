package rhein

class EventLoop[T] extends EventWithSend[T] {

  private var ea_out: Option[Event[T]] = None

  // override def sampleNow() =
  //   if (ea_out.isEmpty)
  //     throw new RuntimeException("StreamLoop sampled before it was looped")
  //   else
  //     ea_out.get.sampleNow()

  def loop(initStream: Event[T]) {
    if (ea_out.isDefined)
      throw new RuntimeException("StreamLoop looped more than once")
    ea_out = Some(initStream)
    addCleanup(initStream.listen(this.node, new TransactionHandler[T]() {
      override def run(trans: Transaction, a: T) {
        EventLoop.this.send(trans, a)
      }
    }))
  }
}
