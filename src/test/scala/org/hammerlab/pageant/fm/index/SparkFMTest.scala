package org.hammerlab.pageant.fm.index

import org.hammerlab.pageant.fm.blocks.RunLengthBWTBlock
import org.hammerlab.pageant.fm.index.FMIndex.FMI
import org.hammerlab.pageant.fm.utils.{SmallFMSuite, Utils}
import org.hammerlab.pageant.utils.PageantSuite

import scala.collection.mutable.ArrayBuffer

class SparkFMTest extends PageantSuite {

  val sequence = "ACGTTGCA$"
  val (sa, bwt) = SmallFMSuite.initBWT(sequence)

  def testCase(saPartitions: Int, tsPartitions: Int, blockSize: Int): Unit = {
    test(s"spark-fm-$saPartitions-$tsPartitions-$blockSize") {

      sa should be(Array(8, 7, 0, 6, 1, 5, 2, 4, 3))
      bwt.map(Utils.toC).mkString("") should be("AC$GATCTG")

      val fm = SmallFMSuite.initFM(
        sc,
        saPartitions,
        sequence,
        tsPartitions,
        sa,
        bwt,
        blockSize,
        N = 6
      )

      val curCounts = Array.fill(6)(0)
      val counts = ArrayBuffer[Array[Int]]()
      for {i <- bwt} {
        counts.append(curCounts.clone())
        curCounts(i) += 1
      }

      val blocks =
        (for {
          start <- bwt.indices by blockSize
        } yield {
          val end = math.min(start + blockSize, bwt.length)
          RunLengthBWTBlock.fromTs(
            start,
            counts(start).map(_.toLong),
            bwt.slice(start, end)
          )
        }).toArray.zipWithIndex.map(_.swap)

      def testFM(fm: FMI) {
        fm.totalSums.c should be(Array(0, 1, 3, 5, 7, 9).map(_.toLong))
        fm.bwtBlocks.collect.sortBy(_._1) should be(blocks)
      }

      testFM(fm)
      val fn = tmpPath("small")
      fm.save(fn)

      val fm2 = FMIndex.load(sc, fn, gzip = false)
      testFM(fm2)
    }
  }

  for {
    saPartitions <- List(1, 3)
    tsPartitions <- List(1, 3)
    blockSize <- 1 to 5 by 2
  } {
    testCase(saPartitions, tsPartitions, blockSize)
  }
}

