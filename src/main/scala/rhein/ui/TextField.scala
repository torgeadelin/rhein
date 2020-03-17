package rhein.ui
import rhein._
import scala.scalajs.js
import scalatags.JsDom.all._
import org.scalajs.dom.{Event => DomEvent}

class TextField(
    var sText: Event[String],
    var initialValue: String,
    var enabled: Behaviour[Boolean]
) {

  // Logic
  final val sUserChangesSink: EventSink[String] = new EventSink()

  var sUserChanges: Event[String] = new Event()
  sUserChanges = sUserChangesSink
  val merged = Event.merge(sUserChangesSink, sText)
  var text: Behaviour[String] = merged.hold(initialValue)

  sText.listen((newVal) => {
    domElement.value = newVal
  })

  // UI - using Scalatags
  val element = input(`type` := "text", value := text.sampleNoTrans)
  var domElement = element.render
  domElement.oninput = (event: DomEvent) => {
    val newVal = domElement.value.toString
    sUserChangesSink.send(newVal)
  }

  /**
    * Auxiliary constructor
    *
    * @param initialValue
    * @return
    */
  def this(initialValue: String) {
    this(new Event[String], initialValue, new Behaviour[Boolean](Some(true)))
  }

  def this(sText: Event[String], initialValue: String) {
    this(sText, initialValue, new Behaviour[Boolean](Some(true)))
  }

  /*
  TODO Implement enable logic for then enabled Behaviour
 */
}
