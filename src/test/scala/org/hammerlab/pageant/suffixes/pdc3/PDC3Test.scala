package org.hammerlab.pageant.suffixes.pdc3

import org.apache.spark.serializer.DirectFileRDDSerializer._
import org.hammerlab.pageant.suffixes.base.SuffixArrayTestBase
import org.hammerlab.pageant.utils.Utils.{loadBam, resourcePath}
import org.hammerlab.pageant.utils.{KryoNoReferenceTracking, PageantSuite}
import org.scalatest.{FunSuite, Matchers}

class PDC3Test extends SuffixArrayTestBase with PageantSuite with KryoNoReferenceTracking {

  override def testFn(name: String)(testFun: => Unit): Unit = test(name)(testFun)

  override def arr(a: Array[Int], n: Int): Array[Int] = {
    PDC3.apply(sc.parallelize(a.map(_.toLong))).map(_.toInt).collect
  }

  test("bam") {
    val ots = loadBam(sc, "src/test/resources/normal.bam")
    val ts = ots.zipWithIndex().map(_.swap).sortByKey(numPartitions = 4).map(_._2)

    ots.getNumPartitions should be(1)
    ts.getNumPartitions should be(4)

    ots.take(10) should be(Array(1, 4, 4, 4, 4, 4, 1, 1, 3, 1))
    ts.take(10) should be(Array(1, 4, 4, 4, 4, 4, 1, 1, 3, 1))

    ts.count should be(102000)

    val sa = PDC3.apply(ts.map(_.toLong))
    sa.count should be(102000)

    sa.take(1000) should be(1 to 1000 map(_ * 102 - 1) toArray)

    sa.getNumPartitions should be(4)

    val expectedSA = sc.directFile[Long]("src/test/resources/normal.bam.sa", gzip = false).collect
    val actualSA = sa.collect
    actualSA.length should be(expectedSA.length)
    for {
      ((actual, expected), idx) <- actualSA.zip(expectedSA).zipWithIndex
    } {
      withClue(s"SA, idx $idx:") {
        actual should be(expected)
      }
    }

    val expectedTS = sc.directFile[Byte]("src/test/resources/normal.bam.ts", gzip = false).collect
    val actualTS = ts.collect
    actualTS.length should be(expectedTS.length)
    for {
      ((actual, expected), idx) <- actualTS.zip(expectedTS).zipWithIndex
    } {
      withClue(s"TS, idx $idx:") {
        actual should be(expected)
      }
    }

//    ts.saveAsDirectFile("src/test/resources/normal.bam.ts", gzip = false)
//    sa.saveAsDirectFile("src/test/resources/normal.bam.sa", gzip = false)
  }
}

class CmpFnTest extends FunSuite with Matchers {
  import PDC3.cmpFn

  test("basic 1-1 cmp") {
    cmpFn(
      (1, Joined(t0O = Some(2), n0O = Some(3), n1O = Some(4))),
      (4, Joined(t0O = Some(2), n0O = Some(2), n1O = Some(1)))
    ) should be > 0
  }

  test("basic 2-0 cmp") {
    val t1 = (5L, Joined(t0O = Some(2), n0O = Some(1)))
    val t2 = (3L, Joined(t0O = Some(1), t1O = Some(2), n0O = Some(2), n1O = Some(1)))

    cmpFn(t1, t2) should be > 0
    cmpFn(t2, t1) should be < 0
  }
}
