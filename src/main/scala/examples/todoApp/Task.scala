package examples.todoApp

class Task(var description: String, var isDone: Boolean) {
  val id: String = java.util.UUID.randomUUID.toString
}
