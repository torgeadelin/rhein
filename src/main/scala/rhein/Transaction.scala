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

  private val pq = new PriorityQueue[Entry]()
  private var last: List[Runnable] = List()

  def prioritized(rank: Node, action: Handler[Transaction]) {
    pq += new Entry(rank, action)
  }

  def last(action: Runnable) {
    last = last ++ List(action)
  }

  // Close the transaction
  def close() {
    while (!pq.isEmpty)
      pq.dequeue().action.run(this);

    last.foreach(_.run())
    last = List()
  }
}

object Transaction {
  def evaluate[A](code: Transaction => A): A = {
    val trans: Transaction = new Transaction()
    try {
      code(trans)
    } finally {
      trans.close()
    }
  }

  def run(code: Handler[Transaction]) {
    val trans: Transaction = new Transaction()
    try {
      code.run(trans)
    } finally {
      trans.close()
    }

  }
}
