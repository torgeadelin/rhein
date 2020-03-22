package rhein

import java.lang.Comparable
import collection.mutable.PriorityQueue
import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.atomic.AtomicLong

trait Handler[T] {
  def run(a: T)
}

class Entry(var rank: Node, var action: Handler[Transaction])
    extends Comparable[Entry] {
  override def compareTo(other: Entry): Int = {
    rank.compareTo(other.rank)
  }
}

class Transaction() {

  // must be private
  val pq = new PriorityQueue[Entry]()
  var last: List[Runnable] = List()

  // add a new transaction that is prioritized and runs before everything
  def prioritized(rank: Node, action: Handler[Transaction]) {
    pq += new Entry(rank, action)
  }

  // add a new action that is NOT prioritized and runs last
  def last(action: Runnable) {
    last = last ++ List(action)
  }

  // Close the transaction
  // Run all actions in pq and last
  def close() {
    while (!pq.isEmpty)
      pq.dequeue().action.run(this);

    last.foreach(_.run())
    last = List()
  }
}

object Transaction {

  // Run the specified code inside a single transaction, with the contained
  // code returning a value of the parameter type A.
  def evaluate[A](code: Transaction => A): A = {
    val trans: Transaction = new Transaction()
    try {
      code(trans)
    } finally {
      trans.close()
    }
  }

  // not used (replaced it with evaluate == everything seems to still work)
  // def run(code: Handler[Transaction]) {
  //   val trans: Transaction = new Transaction()
  //   try {
  //     code.run(trans)
  //   } finally {
  //     trans.close()
  //   }
  // }
}
