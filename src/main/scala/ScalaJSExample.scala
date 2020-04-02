import rhein._
import rhein.ui._
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.{Element}
import dom.document
import scalatags.JsDom.all._
import org.scalajs.dom.{Event => DomEvent}
import java.{util => ju}

@JSExportTopLevel("ScalaJSExample")
object ScalaJSExample {
  import Bindings._

  def main(args: Array[String]) {

    var list: BehaviourLoop[List[String]] = new BehaviourLoop()

    val buttonAdd: Button = new Button("Add", "")

    var allDeleteEvents: Event[(String, Int)] = new Event()

    val reactive =
      new Reactive(
        list,
        (x: String, index: Int) => {
          val buttonDelete = new Button("Delete", "")
          val delete = buttonDelete.eventClicked.map(u => ("delete", index))

          allDeleteEvents = Event.merge(allDeleteEvents, delete)
          li(x, buttonDelete.domElement)
        }
      )

    val add = buttonAdd.eventClicked.map(u => ("add", 0))

    val allEvents = Event.merge(allDeleteEvents, add)

    list.loop(
      allEvents
        .snapshot(list, (clicks, lst: List[String]) => {
          clicks._1 match {
            case "add"    => "New element" :: lst
            case "delete" => lst.take(clicks._2) ++ lst.drop(clicks._2 + 1)
          }
        })
        .hold(List(""))
    )

    val l: List[Behaviour[String]] = List()
    dom.document.body.innerHTML = ""
    dom.document.body.appendChild(
      div(cls := "mt-3")(
        h1("Clear text field example"),
        br,
        buttonAdd.domElement,
        //list,
        ul(reactive.domElement)
      ).render
    )

    // // Label that always shows the current text
    // val textField2: TextField = new TextField("Hello!")
    // val label: Label = new Label(textField2.text)

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Label and Textfield"),
    //     div(cls := "d-flex align-items-center")(
    //       textField2.domElement,
    //       span((cls := "ml-2"))(label.domElement)
    //     )
    //   ).render
    // )

    // // Using map to reverse string
    // val textField3: TextField = new TextField("Hello!")
    // val label3: Label = new Label(
    //   textField3.text.map((text => text.toString.reverse))
    // )

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Using map to Reverse"),
    //     div(cls := "d-flex align-items-center")(
    //       textField3.domElement,
    //       span((cls := "ml-2"))(label3.domElement)
    //     )
    //   ).render
    // )

    // // Merge example
    // val buttonA: Button = new Button("A", "btnA")
    // val buttonB: Button = new Button("B", "btnB")
    // val merged: Event[String] = Event.merge(
    //   buttonA.eventClicked.map(u => "A"),
    //   buttonB.eventClicked.map(u => "B")
    // )
    // val textField4: TextField = new TextField(merged, "")

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Merge example"),
    //     div(cls := "d-flex align-items-center")(
    //       buttonA.domElement,
    //       buttonB.domElement,
    //       span((cls := "ml-2"))(textField4.domElement)
    //     )
    //   ).render
    // )

    // // Merge and Hold examples
    // val buttonRed: Button = new Button("Red", "btn-danger")
    // val buttonGreen: Button = new Button("Green", "btn-success")
    // val buttonsMerged: Event[String] = Event.merge(
    //   buttonRed.eventClicked.map(_ => "Red"),
    //   buttonGreen.eventClicked.map(_ => "Green")
    // )
    // val labelRedOrGreen: Label = new Label(buttonsMerged.hold(""))

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Merge and hold example"),
    //     div(cls := "d-flex align-items-center")(
    //       buttonRed.domElement,
    //       buttonGreen.domElement,
    //       span((cls := "ml-2"))(labelRedOrGreen.domElement)
    //     )
    //   ).render
    // )

    // // Snapshot
    // val translateButton: Button = new Button("Translate", "btnTranslate")
    // val english: TextField = new TextField("Translate")
    // val snapshotVal: Event[String] = translateButton.eventClicked.snapshot(
    //   english.text,
    //   (u, txt: String) => txt.trim.replaceAll(" |$", "us ").trim
    // )
    // val latin: Label = new Label(snapshotVal.hold(""))

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Shanpshot"),
    //     div(cls := "d-flex align-items-center")(
    //       translateButton.domElement,
    //       english.domElement,
    //       span((cls := "ml-2"))(latin.domElement)
    //     )
    //   ).render
    // )

    // // Accuulator - Spinner example
    // val valueLoop: BehaviourLoop[Int] = new BehaviourLoop()

    // val buttonPlus: Button = new Button("+", "btnPlus")
    // val buttonMinus: Button = new Button("-", "btnMinus")
    // val sDelta: Event[Int] = Event.merge(
    //   buttonPlus.eventClicked.map(_ => 1),
    //   buttonMinus.eventClicked.map(_ => -1)
    // )
    // val sUpdate: Event[Int] =
    //   sDelta
    //     .snapshot(valueLoop, (delta, value_ : Int) => {
    //       val res = value_ + delta
    //       res
    //     })
    //     .filter(n => n >= 0)
    // valueLoop.loop(sUpdate.hold(0))
    // val resultLabel: Label = new Label(valueLoop.map(x => x.toString()))
    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Accumulator"),
    //     div(cls := "d-flex align-items-center")(
    //       buttonPlus.domElement,
    //       buttonMinus.domElement,
    //       span((cls := "ml-2"))(resultLabel.domElement)
    //     )
    //   ).render
    // )

    // // Lifting example
    // val textFieldA: TextField = new TextField("0")
    // val textFieldB: TextField = new TextField("0")
    // val a: Behaviour[Int] = textFieldA.text.map(t => t.toInt)
    // val b: Behaviour[Int] = textFieldB.text.map(t => t.toInt)
    // // def add(a: Int, b: Int): Int = a + b
    // val lifted = a.lift(b, (p, q: Int) => p + q)
    // val res: Label = new Label(lifted.map(x => x.toString))

    // dom.document.body.appendChild(
    //   div(cls := "mt-3")(
    //     h1("Lift"),
    //     div(cls := "d-flex align-items-center")(
    //       textFieldA.domElement,
    //       textFieldB.domElement,
    //       span((cls := "ml-2"))(res.domElement)
    //     )
    //   ).render
    // )

  }
}
