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

  var sUserChanges: Event[String] = new Event()
  var text: Behaviour[String] = new Behaviour(Some(initialValue))

  val sUserChangesSink: EventSink[String] = new EventSink()

  // UI - using Scalatags
  val element = input(`type` := "text", value := text.sampleNoTrans)
  var domElement = element.render
  domElement.oninput = (event: DomEvent) => {
    val newVal = domElement.value.toString
    sUserChangesSink.send(newVal)
  }

  // Logic
  sUserChanges = sUserChangesSink
  text = sUserChangesSink.hold(initialValue)

  sText.listen((newVal) => {
    domElement.value = newVal
  })

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
