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

    // Merge example
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

    // Merge and Hold examples
    val buttonRed = new Button("Red", "btn-danger")
    val buttonGreen = new Button("Green", "btn-success")
    val buttonsMerged: Event[String] = Event.merge(
      buttonRed.eventClicked.map(_ => "Red"),
      buttonGreen.eventClicked.map(_ => "Green")
    )
    val labelRedOrGreen = new Label(buttonsMerged.hold(""))
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Merge and hold example"),
        div(cls := "d-flex align-items-center")(
          buttonRed.domElement,
          buttonGreen.domElement,
          span((cls := "ml-2"))(labelRedOrGreen.domElement)
        )
      ).render
    )

    // Snapshot
    val translateButton = new Button("Translate", "btnTranslate")
    val english =
      new TextField("Translate")
    val snapshotVal: Event[String] = translateButton.eventClicked.snapshot(
      english.text,
      (u, txt: String) => txt.trim.replaceAll(" |$", "us ").trim
    )
    val latin = new Label(snapshotVal.hold(""))
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Shanpshot"),
        div(cls := "d-flex align-items-center")(
          translateButton.domElement,
          english.domElement,
          span((cls := "ml-2"))(latin.domElement)
        )
      ).render
    )

  }
}
