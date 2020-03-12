package rhein
import scala.collection.mutable.ListBuffer

class EventSink[T] extends Event[T] {
  def send(a: T) {
    Transaction.run((trans: Transaction) => { send(trans, a) })
  }

  def send(trans: Transaction, a: T) {
    try {
      this.listeners
        .clone()
        .asInstanceOf[ListBuffer[TransactionHandler[T]]]
        .foreach(_.run(trans, a))
    } catch {
      case t: Throwable => t.printStackTrace()
    }
  }

}
