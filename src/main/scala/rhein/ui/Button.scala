package rhein.ui
import rhein._
import scala.scalajs.js
import scalatags.JsDom.all._

/**
  * A simple UI Button component that was injected with an Event Stream
  * To facilitate interoperability
  *
  * @param label
  * @param enabled
  */
class EmptyMessage extends Message

class Button(
    val text: String,
    val label: String,
    val enabled: Behaviour[Boolean],
    var valueToEmit: Message = new EmptyMessage()
) {

  // Logic
  var eventClicked: Event[Message] = new Event()
  var eventClickedSink: EventSink[Message] = new EventSink()
  eventClicked = eventClickedSink

  // UI - using Scalatags

  val domElement = div(id := label, cls := "btn btn-primary", onclick := { () =>
    {
      eventClickedSink.send(valueToEmit)
    }
  })(text)

  def attachEvent(event: EventSink[_], newValueToEmit: Message) {
    valueToEmit = newValueToEmit
    eventClickedSink = event.asInstanceOf[EventSink[Message]]
  }

  def this(text: String, label: String) {
    this(text, label, new Behaviour(Some(true)), new EmptyMessage())
  }

  def this(text: String) {
    this(text, "", new Behaviour(Some(true)), new EmptyMessage())
  }
  /*
  TODO Implement enable logic for then enabled Behaviour
 */
}
