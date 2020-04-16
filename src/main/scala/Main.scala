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

import examples.todoApp.TodoApp
import examples.gameOfLife.GameOfLife

import scala.collection.mutable

// Exports and runs this main method in the javascript
// code that is generted by "fastOptJS"
@JSExportTopLevel("Main")
object Main {
  import Bindings._

  def main(args: Array[String]) {
    
    //Rhein
    document.body.appendChild(
      div(cls := "my-5")(
        img(src:= "https://i.imgur.com/2hb1EXu.png", style:= "width: 150px; margin-left: -5px"),
        h1( style :="font-size: 60pt")("Rhein"),
        p("Rhein is a data-propagation library based on Functional Reactive Programming abstractions such as Events and Behaviours that helps you to develop interactive applications using a conceptual-declarative approach that brings numerous benefits to the quality of the appli- cations and also solves several problems the mainstream methods of development of this type of software produce."),
      ).render
    )
  
    //========================================================================================
    /*                                                                                      *
    *                                     Examples                                          *
    *                                                                                       */
    //========================================================================================

    // Applications
    TodoApp.run()
    var game = new GameOfLife()
    game.run(new mutable.ListBuffer(), true)

    // Primitives in action

    // Label that always shows the current text
    val textField2: TextField = new TextField("Hello!")
    val label: Label = new Label(textField2.text)

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Label and Textfield"),

          div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("hold"),
            span(cls :="badge badge-secondary mr-1")("Label"),
            span(cls :="badge badge-secondary mr-1")("TextField")
          ),

          p(cls:= "w-75", "The hold primitive converts a event stream into a behaviour in such way that the behaviour’s value is that of the most recent event received. The Label component corresponds to a simple p element that wraps the value of a behaviour and re-renders whenever the value of behaviour changes. The TextField component corresponds to an input(type = \"text\") element."),

          div(cls := "d-flex align-items-center")(
          textField2.domElement,
          span((cls := "ml-2"))(label.domElement),
        )
      ).render
    )

    // Using map to reverse string
    val textField3: TextField = new TextField("Hello!")
    val label3: Label = new Label(
      textField3.text.map((text => text.toString.reverse))
    )

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Using map to Reverse"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("hold"),
            span(cls :="badge badge-info mr-1")("map"),
            span(cls :="badge badge-secondary mr-1")("Label"),
            span(cls :="badge badge-secondary mr-1")("TextField")
          ),

          p(cls:= "w-75", "The map primitive is used to convert a stream of events of type A into a stream of events of type B by passing a function as argument that does the transformation"),

        div(cls := "d-flex align-items-center")(
          textField3.domElement,
          span((cls := "ml-2"))(label3.domElement)
        )
      ).render
    )

    // Merge example
    val buttonA: Button = new Button("A", "btnA")
    val buttonB: Button = new Button("B", "btnB")
    val merged: Event[String] = Event.merge(
      buttonA.eventClicked.map(u => "A"),
      buttonB.eventClicked.map(u => "B")
    )
    val textField4: TextField = new TextField(merged, "")

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Merge example"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("merge"),
            span(cls :="badge badge-info mr-1")("map"),
            span(cls :="badge badge-secondary mr-1")("TextField"),
            span(cls :="badge badge-secondary mr-1")("Button")
          ),

          p(cls:= "w-75", "The merge primitive puts the event firings from two event streams together into a single stream. This function is semantically equivalent to the one we apply to collections (i.e. lists). The Button component corresponds to a div element that is attached with an onClick DOM listener."),

        div(cls := "d-flex align-items-center")(
          buttonA.domElement,
          buttonB.domElement,
          span((cls := "ml-2"))(textField4.domElement)
        )
      ).render
    )

    // Merge and Hold examples
    val buttonRed: Button = new Button("Red", "btn-danger")
    val buttonGreen: Button = new Button("Green", "btn-success")
    val buttonsMerged: Event[String] = Event.merge(
      buttonRed.eventClicked.map(_ => "Red"),
      buttonGreen.eventClicked.map(_ => "Green")
    )
    val labelRedOrGreen: Label = new Label(buttonsMerged.hold(""))

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Merge and hold example"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("merge"),
            span(cls :="badge badge-info mr-1")("map"),
            span(cls :="badge badge-info mr-1")("hold"),
            span(cls :="badge badge-secondary mr-1")("Label"),
            span(cls :="badge badge-secondary mr-1")("Button")
          ),

          br,

        div(cls := "d-flex align-items-center")(
          buttonRed.domElement,
          buttonGreen.domElement,
          span((cls := "ml-2"))(labelRedOrGreen.domElement)
        )
      ).render
    )

    // Snapshot
    val translateButton: Button = new Button("Translate", "btnTranslate")
    val english: TextField = new TextField("Translate")
    val snapshotVal: Event[String] = translateButton.eventClicked.snapshot(
      english.text,
      (u, txt: String) => txt.trim.replaceAll(" |$", "us ").trim
    )
    val latin: Label = new Label(snapshotVal.hold(""))

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Shanpshot"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("snapshot"),
            span(cls :="badge badge-info mr-1")("hold"),
            span(cls :="badge badge-secondary mr-1")("TextField"),
            span(cls :="badge badge-secondary mr-1")("Button")
          ),

          p(cls:= "w-75", "The snapshot primitive captures the value of a behaviour at the time when an event stream fires, and then it can combine the payload from the event stream and the one from the behaviour together with a supplied function."),


        div(cls := "d-flex align-items-center")(
          translateButton.domElement,
          english.domElement,
          span((cls := "ml-2"))(latin.domElement)
        )
      ).render
    )

    // Accuulator - Spinner example
    val valueLoop: BehaviourLoop[Int] = new BehaviourLoop()

    val buttonPlus: Button = new Button("+", "btnPlus")
    val buttonMinus: Button = new Button("-", "btnMinus")
    val sDelta: Event[Int] = Event.merge(
      buttonPlus.eventClicked.map(_ => 1),
      buttonMinus.eventClicked.map(_ => -1)
    )
    val sUpdate: Event[Int] =
      sDelta
        .snapshot(valueLoop, (delta, value_ : Int) => {
          val res = value_ + delta
          res
        })
        .filter(n => n >= 0)
    valueLoop.loop(sUpdate.hold(0))
    val resultLabel: Label = new Label(valueLoop.map(x => x.toString()))
    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Accumulator"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("loop"),
            span(cls :="badge badge-info mr-1")("hold"),
            span(cls :="badge badge-info mr-1")("merge"),
            span(cls :="badge badge-info mr-1")("map"),
            span(cls :="badge badge-info mr-1")("filter"),
            span(cls :="badge badge-secondary mr-1")("Label"),
            span(cls :="badge badge-secondary mr-1")("Button")
          ),

          p(cls:= "w-75", "An Accumulator represents a state that is updated by combining new information with the existing state."),

        div(cls := "d-flex align-items-center")(
          buttonPlus.domElement,
          buttonMinus.domElement,
          span((cls := "ml-2"))(resultLabel.domElement)
        )
      ).render
    )

    // Lifting example
    val textFieldA: TextField = new TextField("0")
    val textFieldB: TextField = new TextField("0")
    val a: Behaviour[Int] = textFieldA.text.map(t => t.toInt)
    val b: Behaviour[Int] = textFieldB.text.map(t => t.toInt)
    // def add(a: Int, b: Int): Int = a + b
    val lifted = a.lift(b, (p, q: Int) => p + q)
    val res: Label = new Label(lifted.map(x => x.toString))

    dom.document.body.appendChild(
      div(cls := "my-5")(
        h1("Lift"),

        div(cls := "mb-1")(
            span("Using: "),
            span(cls :="badge badge-info mr-1")("lift"),
            span(cls :="badge badge-info mr-1")("map"),
            span(cls :="badge badge-secondary mr-1")("Label"),
            span(cls :="badge badge-secondary mr-1")("TextField")
          ),

          p(cls:= "w-75", "The lift primitive allows you to combine two or more behaviours into one using a specified combining function. The filter primitive is used to let event stream values through the pipe only sometimes. This is a general functional programming concept, and this name is used universally in FRP systems."),

        div(cls := "d-flex align-items-center")(
          textFieldA.domElement,
          textFieldB.domElement,
          span((cls := "ml-2"))(res.domElement),
          
        ),
        div(cls := "alert alert-light my-5", role := "alert", "All examples are implemented in Rhein")
      ).render
    )

  }
}
