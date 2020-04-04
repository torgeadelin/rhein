// package examples.todoApp
// import rhein.ui.{Button, TextField, Message, Listing, Bindings}
// import rhein._
// import scalatags.JsDom.all._
// import scala.scalajs.js._
// import scala.scalajs.js.annotation._
// import org.scalajs.dom
// import org.scalajs.dom._

// class TodoMessage[T](var action: String, var value: T) extends Message

// object TodoApp {
//   import Bindings._

//   /**
//     * UI component for a Task
//     * @param task
//     */
//   def todoItem(task: Task, buttonDelete: Button) = {
//     div(cls := "d-flex align-items-center")(
//       div(cls := "d-flex align-items-center border p-2")(
//         span(cls := "mr-2", task.description),
//         if (task.isDone)
//           span(cls := "badge badge-pill badge-warning", "Ongoing")
//         else span(cls := "badge badge-pill badge-success", "Done")
//       ),
//       buttonDelete.domElement(cls := "btn btn-light ml-2")
//     )
//   }

//   def run() {
//     val buttonAdd = new Button("+")
//     val taskTextField = new TextField("")

//     val eventAdd = buttonAdd.eventClicked
//       .snapshot(taskTextField.text, (click, text: String) => {
//         new TodoMessage[Task]("add", new Task(text, false))
//       })

//     var todoEvents: EventSink[TodoMessage[Task]] =
//       eventAdd.asInstanceOf[EventSink[TodoMessage[Task]]]

//     // Loop State
//     var loopState = new EventLoop[List[Task]]
//     var cState = loopState.hold(List());
//     loopState.loop(
//       todoEvents.snapshot(
//         cState,
//         (event, _state: List[Task]) => {
//           event.action match {
//             case "add" => {
//               event.value :: _state
//             }
//             case "remove" => {
//               _state.filter(p => p.id != event.value.id)
//             }
//           }
//         }
//       )
//     )

//     // Displaying the Todos
//     val list = new Listing[Task](cState, task => {
//       val buttonDelete = new Button("-")
//       buttonDelete
//         .attachEvent(todoEvents, new TodoMessage[Task]("remove", task))
//       todoItem(task, buttonDelete)
//     })

//     dom.document.body.innerHTML = ""
//     dom.document.body.appendChild(
//       div(cls := "mt-3")(
//         h1("Todo Application"),
//         br,
//         div(cls := "d-flex align-items-center")(
//           buttonAdd.domElement,
//           taskTextField.domElement
//         ),
//         br,
//         br,
//         list.domElement
//       ).render
//     )

//   }
// }
