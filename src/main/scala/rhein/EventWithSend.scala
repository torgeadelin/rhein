package rhein

class EventWithSend[T] extends Event[T] {

  def send(trans: Transaction, a: T) {
    if (firings.isEmpty)
      trans.last(new Runnable() {
        def run() { firings.clear() }
      })
    firings += a

    try {
      listeners.clone.foreach(_.run(trans, a))
    } catch {
      case t: Throwable => t.printStackTrace()
    }
  }

}
