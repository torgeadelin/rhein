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
class Button(
    val text: String,
    val label: String,
    var eventClicked: Event[_],
    val enabled: Behaviour[Boolean]
) {

  // Debug
  println(s"$label Button was created")

  // Logic
  var eventClickedSink: EventSink[Unit] = new EventSink()
  eventClicked = eventClickedSink

  // UI - using Scalatags

  val domElement = div(id := label, cls := "btn btn-primary", onclick := { () =>
    {
      eventClickedSink.send(Unit)
    }
  })(text)

  /**
    * Auxiliary Constructor
    *
    * @param label
    * @return
    */
  def this(text: String, label: String) {
    this(text, label, new Event(), new Behaviour(Some(true)))
  }
  def this(text: String, stream: Event[_]) {
    this(text, "", stream, new Behaviour(Some(true)))
  }

  /*
  TODO Implement enable logic for then enabled Behaviour
 */
}
