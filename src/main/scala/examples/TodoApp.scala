package examples.todoApp
import rhein._
import rhein.ui._
import scalatags.JsDom.all._
import scala.scalajs.js._
import scala.scalajs.js.annotation._
import org.scalajs.dom
import org.scalajs.dom._

/**
  * Value that will be emitted
  * when emitting events in the todo application
  * @param action can either be add or remove
  * @param value will specify which value in the list will the action will be taken on
  */
class TodoMessage[T](var action: String, var value: T) extends Message

/**
  * Simmple Task class to model a task
  * @param description
  * @param isDone
  */
class Task(var description: String, var isDone: Boolean) {
  val id: String = java.util.UUID.randomUUID.toString
}

/**
  * Todo application
  */
object TodoApp {
  import Bindings._

  /**
    * UI component for a Task
    * CSS classes are part of Bootstrap v4.0
    * @param task
    */
  def todoItem(task: Task, buttonDelete: Button) = {
    div(cls := "d-flex align-items-center mb-1")(
      div(cls := "d-flex align-items-center border p-2")(
        span(cls := "mr-2", task.description),
        if (!task.isDone)
          span(cls := "badge badge-pill badge-warning", "Ongoing")
        else span(cls := "badge badge-pill badge-success", "Done")
      ),
      buttonDelete.domElement(cls := "btn btn-light ml-2")
    )
  }

  /**
    * Method to initialise and run
    * the todo application.
    */
  def run() {
    // UI components
    val buttonAdd = new Button("+")
    val taskTextField = new TextField("")

    /**
      * Stream of events
      * When the add button is clicked, a new event is
      */
    val todoEvents = buttonAdd.eventClicked
      .snapshot(taskTextField.text, (click, text: String) => {
        new TodoMessage[Task]("add", new Task(text, false))
      })

    // Loop State
    // Creating an accumulator
    val cState = new BehaviourLoop[List[Task]]
    val updates = todoEvents.snapshot(cState, (event, _state: List[Task]) => {
      event.action match {
        case "add" => {
          event.value :: _state
        }
        case "remove" => {
          _state.filter(p => p.id != event.value.id)
        }
      }
    })
    cState.loop(updates.hold(List()))

    // Displaying the Todos
    val list = new Listing[Task](cState, (task: Task, index: Int) => {
      val buttonDelete = new Button("Mark as done!")
      buttonDelete
        .attachEvent(todoEvents, new TodoMessage[Task]("remove", task))
      todoItem(task, buttonDelete)
    })

    //Rendering
    dom.document.body.appendChild(
      div(cls := "mt-2")(
        h1("Todo Application"),
        p("Please add a new task"),
        div(cls := "d-flex align-items-center")(
          buttonAdd.domElement,
          taskTextField.domElement
        ),
        br,
        br,
        list.domElement
      ).render
    )

  }
}
