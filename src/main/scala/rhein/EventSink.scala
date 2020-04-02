package rhein
import scala.collection.mutable.ListBuffer

class EventSink[T] extends Event[T] {
  // do this in a new transaction
  def send(a: T) {
    Transaction.evaluate((trans: Transaction) => { send(trans, a) })
  }
  // get all listener actions and send
  // this payload down the pipe
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
