package rhein.ui
import rhein._
import scala.scalajs.js
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag
import scala.collection.{Iterable => Iter}

/**import
  * A simple UI Label component that was injected with an Behaviour
  * To facilitate interoperability
  *
  * @param text
  */
class Listing[T](
    text: Behaviour[List[T]],
    f: (T, Int) => scalatags.JsDom.Modifier
) {

  val initialValue: List[T] = List()

  val element = span(
    for ((elem, index) <- initialValue.toSeq.zipWithIndex) yield f(elem, index)
  )

  var domElement = element.render

  // Logic
  var listener = text
    .changes()
    .listen((newVal: List[T]) => {
      val newLast =
        span(
          for ((elem, index) <- newVal.toSeq.zipWithIndex) yield f(elem, index)
        ).render
      domElement.parentElement.replaceChild(newLast, domElement)
      domElement = newLast
    })
}
