package rhein
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import scala.collection.mutable.ListBuffer

class RheinTester {

  @Test
  def testSendEvent(): Unit = {
    val event = new EventSink[Int]()
    val out = new ListBuffer[Int]()
    val listener = event.listen(x => out += x)
    event.send(1)
    listener.unlisten()
    assertEquals(List(1), out)
    event.send(2)
    assertEquals(List(1), out)
  }

  @Test
  def testMapPrimitive(): Unit = {
    val event = new EventSink[Int]()
    val map = event.map(x => x.toString)
    val out = new ListBuffer[String]()
    val listener = map.listen(x => out += x)
    event.send(5)
    listener.unlisten()
    assertEquals(List("5"), out)
  }

  @Test
  def testMergePrimitive(): Unit = {
    val e1 = new EventSink[Int]()
    val e2 = new EventSink[Int]()
    val out = new ListBuffer[Int]()
    val listener = Event.merge(e2, e1).listen(x => out += x)
    e1.send(1)
    e2.send(2)
    e1.send(3)
    listener.unlisten()
    assertEquals(List(1, 2, 3), out)
  }

  @Test
  def testFilterPrimitive(): Unit = {
    val event = new EventSink[Char]()
    val out = new ListBuffer[Char]()
    val listener =
      event.filter(c => Character.isUpperCase(c)).listen(x => out += x)
    List('H', 'o', 'I').foreach(event.send(_))
    listener.unlisten()
    assertEquals(List('H', 'I'), out)
  }

  @Test
  def testLiftPrimitive(): Unit = {
    val out = new ListBuffer[Int]()
    val behaviourSink = new BehaviourSink(Some(1))
    val cell = behaviourSink.map(v => 2 * v)
    val listener = behaviourSink
      .lift(cell, (x: Int, y: Int) => x + y)
      .changes()
      .listen(x => out += x)
    behaviourSink.send(2)
    behaviourSink.send(7)
    listener.unlisten()
    assertEquals(List(6, 21), out)
  }

  @Test
  def testHoldPrimitive(): Unit = {
    val event = new EventSink[Int]()
    val behaviour = event.hold(0)
    val out = new ListBuffer[Int]()
    val listener = event.listen(x => out += x)
    List(2, 9).foreach(event.send)
    listener.unlisten()
    assertEquals(List(2, 9), out)
  }

  @Test
  def testSnapshotPrimitive(): Unit = {
    val behaviourSink: BehaviourSink[Int] = new BehaviourSink(Some(0))
    val event = new EventSink[Long]()
    val out = new ListBuffer[String]()
    val listener = event
      .snapshot[Int, String](behaviourSink, (x: Long, y: Int) => x + " " + y)
      .listen(x => out += x)
    event.send(100L)
    behaviourSink.send(2)
    event.send(200L)
    behaviourSink.send(9)
    behaviourSink.send(1)
    event.send(300L)
    listener.unlisten()
    assertEquals(List("100 0", "200 2", "300 1"), out)
  }

}
