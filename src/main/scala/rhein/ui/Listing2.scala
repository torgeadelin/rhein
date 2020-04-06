package rhein.ui
import rhein._
import scala.scalajs.js
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag
import scala.collection.{Iterable => Iter}
import scala.collection.mutable.ListBuffer

/**import
  * A simple UI Label component that was injected with an Behaviour
  * To facilitate interoperability
  *
  * @param text
  */
class Listing2(
    text: Behaviour[ListBuffer[Boolean]],
    f: (Boolean) => scalatags.JsDom.Modifier
) {

  val initialValue: ListBuffer[Boolean] = ListBuffer()

  val element = span(for (elem <- initialValue.toSeq) yield f(elem))

  var domElement = element.render

  // Logic
  var listener = text
    .changes()
    .listen((newVal: ListBuffer[Boolean]) => {
      val newLast =
        span(for (elem <- newVal.toSeq) yield f(elem)).render
      domElement.parentElement.replaceChild(newLast, domElement)
      domElement = newLast
    })
}
