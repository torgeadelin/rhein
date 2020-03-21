package rhein

class BehaviourSink[T](initValue: Option[T])
    extends Behaviour[T](new EventSink[T](), initValue) {

  def send(a: T) {
    event.asInstanceOf[EventSink[T]].send(a)
  }
}
