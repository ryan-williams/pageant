package org.hammerlab.pageant.fm.utils

import Utils.{N, T}
import org.hammerlab.pageant.fm.blocks.BWTRun

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

case class Counts(c: Array[Long]) {
  def +(other: Counts): Counts = {
    val newC = c.clone()
    (0 until N).foreach(i ⇒ newC(i) += other(i))
    Counts(newC)
  }

  def +(t: T): Counts = {
    val newC = c.clone()
    newC(t) += 1
    Counts(newC)
  }

  def +=(t: T): Unit = {
    c(t) += 1
  }

  def +=(run: BWTRun): Unit = {
    c(run.t) += run.n
  }

  def apply(t: T): Long = c(t)
  def apply(i: Int): Long = c(i)

  def +=(other: Counts): Unit = {
    (0 until N).foreach(i => c(i) += other(i))
  }

  def sum: Long = c.sum

  def sameElements(other: Counts): Boolean = c.sameElements(other.c)

  def mkString(sep: String): String = c.mkString(sep)

  def update(t: T, n: Long) = {
    c(t) = n
  }

  def update(i: Int, n: Long) = {
    c(i) = n
  }

  def partialSum(): Counts = {
    val sum = Counts()
    for {i <- 1 until N} {
      sum(i) = sum(i - 1) + c(i - 1)
    }
    sum
  }

  override def toString: String = c.mkString("(", ",", ")")

  override def equals(o: Any): Boolean =
    o match {
      case other: Counts ⇒ c.sameElements(other.c)
      case _ ⇒ false
    }

  def copy(): Counts = Counts(c.clone())
}

object Counts {
  def apply(): Counts = Counts(Array.fill(N)(0L))

  def one(i: Int): Counts = Counts(oneHot(1L, 0L, i, N))

  private def oneHot[T: ClassTag](t: T, zero: T, i: Int, n: Int): Array[T] = {
    var arr = ArrayBuffer[T]()
    (0 until i).foreach(_ ⇒ arr.append(zero))
    arr.append(t)
    ((i+1) until n).foreach(_ ⇒ arr.append(zero))
    arr.toArray
  }

  def apply(it: Iterator[T]): Counts = {
    val counts = Array.fill(N)(0L)
    it.foreach(t ⇒ counts(t) += 1)
    Counts(counts)
  }

  def partialSums(css: Seq[Counts]): (Seq[Counts], Counts) = {
    val sums: ArrayBuffer[Counts] = ArrayBuffer()
    var cur = Counts()
    css.foreach(counts ⇒ {
      sums += cur
      cur = cur + counts
    })
    (sums, cur)
  }
}

