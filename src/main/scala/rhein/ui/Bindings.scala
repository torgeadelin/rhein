package rhein.ui
import org.scalajs.dom.html
import org.scalajs.dom.{Element}
import scala.scalajs.js
import scalatags.JsDom.all._

import rhein._

object Bindings {

  implicit def SignalStr[T](r: Behaviour[T])(
      implicit f: T => Modifier
  ): Modifier = {
    var initialValue = r.sampleNoTrans()
    // if (r.isInstanceOf[Behaviour[scala.Iterable[Any]]]) {
    //   val initialValue = r.mapList(l => li(l))
    // }

    // UI - using Scalatags
    val element = p(initialValue)
    var domElement = element.render

    // Logic
    var listener = r
      .changes()
      .listen(x => {
        val newLast = p(x).render
        domElement.parentElement.replaceChild(newLast, domElement)
        domElement = newLast
      })

    domElement
  }

  implicit def RxAttrValue[T: AttrValue] = new AttrValue[Behaviour[T]] {
    def apply(t: Element, a: Attr, r: Behaviour[T]): Unit = {
      r.changes()
        .listen((newVal) => {
          implicitly[AttrValue[T]].apply(t, a, newVal)
        })
    }
  }

  implicit def RxStyleValue[T: StyleValue] =
    new StyleValue[Behaviour[T]] {
      def apply(t: Element, s: Style, r: Behaviour[T]): Unit = {
        r.changes()
          .listen((newVal) => {
            implicitly[StyleValue[T]].apply(t, s, r.sampleNoTrans())
          })
      }
    }
}
