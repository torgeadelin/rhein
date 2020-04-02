package rhein.ui
import rhein._
import scala.scalajs.js
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
  * A simple UI Label component that was injected with an Behaviour
  * To facilitate interoperability
  *
  * @param text
  */
class Reactive[T](
    text: BehaviourLoop[List[T]],
    f: (T, Int) => scalatags.JsDom.Modifier
) {

  val initialValue = List() //.asInstanceOf[Iterable[AnyRef]]
  // UI - using Scalatags

  val element = span(
    //span(for ((elem, i) <- initialValue.zipWithIndex) yield span(f(elem, i)))
  )

  var domElement = element.render

  // Logic
  var listener = text
    .changes()
    .listen(x => {
      val newLast =
        span(for ((elem, i) <- x.zipWithIndex) yield span(f(elem, i))).render
      domElement.parentElement.replaceChild(newLast, domElement)
      domElement = newLast
    })

}
