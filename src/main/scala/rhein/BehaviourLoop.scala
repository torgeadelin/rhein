package rhein

final class BehaviourLoop[T] extends Behaviour[T](new EventLoop[T](), None) {

  def loop(a_out: Behaviour[T]) {
    event match {
      case s: EventLoop[T] => s.loop(a_out.changes())
      case _               =>
    }
    value = Some(a_out.sampleNoTrans)
  }

  override def sampleNoTrans(): T = {
    if (value.isEmpty)
      throw new RuntimeException("CellLoop sampled before it was looped")
    value.get
  }
}
