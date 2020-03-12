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
@JSExportTopLevel("ScalaJSExample")
object ScalaJSExample {

  def main(args: Array[String]) {

    // Clear field example
    val clearButton = new Button("Clear", "clearButton")
    val sClearIt: Event[String] = clearButton.eventClicked.map(u => "")
    val textField = new TextField(sClearIt, "Default")
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Clear text field example"),
        clearButton.domElement,
        br,
        br,
        textField.domElement
      ).render
    )

    // Label that always shows the current text
    val textField2 = new TextField("Hello!")
    val label = new Label(textField2.text)
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Label and Textfield"),
        div(cls := "d-flex align-items-center")(
          textField2.domElement,
          span((cls := "ml-2"))(label.domElement)
        )
      ).render
    )

    // Using map to reverse string
    val textField3 = new TextField("Hello!")
    val label3 = new Label(textField3.text.map((text => text.toString.reverse)))
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Using map to Reverse"),
        div(cls := "d-flex align-items-center")(
          textField3.domElement,
          span((cls := "ml-2"))(label3.domElement)
        )
      ).render
    )

    //Merge example
    val buttonA = new Button("A", "btnA")
    val buttonB = new Button("B", "btnB")
    val merged: Event[String] = Event.merge(
      buttonA.eventClicked.map(u => "A"),
      buttonB.eventClicked.map(u => "B")
    )
    val textField4 = new TextField(merged, "")
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Merge example"),
        div(cls := "d-flex align-items-center")(
          buttonA.domElement,
          buttonB.domElement,
          span((cls := "ml-2"))(textField4.domElement)
        )
      ).render
    )

  }
}
