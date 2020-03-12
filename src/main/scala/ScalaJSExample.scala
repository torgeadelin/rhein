import rhein._
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.html
import dom.document
import scalatags.JsDom.all._
import org.scalajs.dom.{Event => DomEvent}
import rhein.{Event => RheinEvent}

import rhein.ui._

class RLabel(text: Behaviour[String]) {
  val initialValue = text.sampleNoTrans()
  val inner = p(initialValue)
  var materialized = inner.render

  var listener = text
    .changes()
    .listen(x => {
      val newLast = p(x).render
      materialized.parentElement.replaceChild(newLast, materialized)
      materialized = newLast
    })
}

@JSExportTopLevel("ScalaJSExample")
object ScalaJSExample {

  def main(args: Array[String]) {

    //Clear field example
    val clearButton = new Button("Clear", "clearButton")
    val sClearIt: Event[String] = clearButton.eventClicked.map(u => "")
    val textField = new TextField(sClearIt, "Default")
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(
      div(
        h1("Clear text field example"),
        clearButton.domElement,
        br,
        br,
        textField.domElement
      ).render
    )
  }
}
