package org.hammerlab.pageant.fm.index

import org.apache.spark.SparkContext
import org.hammerlab.pageant.fm.utils.Utils
import org.scalatest.Ignore

abstract class DirectBuilderFMBamTest(blocksPerPartition: Int) extends FMBamTest {
  def name = "direct"
  override def generateFM(sc: SparkContext) = {
    val bases =
      sc.parallelize(
        sc
          .textFile(s"src/test/resources/1000.reads")
          .take(num)
          .map(_.map(Utils.toI).toVector :+ 0.toByte),
        numPartitions
      )

    DirectFMBuilder(
      bases,
      blockSize,
      blocksPerPartition
    )
  }
}

// Ignore these for now because they take too long to run.

@Ignore
class DirectBuilderFMBamTestThousand extends DirectBuilderFMBamTest(102) with ThousandReadTest

@Ignore
class DirectBuilderFMBamTestHundred extends DirectBuilderFMBamTest(11) with HundredReadTest

@Ignore
class DirectBuilderFMBamTestTen extends DirectBuilderFMBamTest(3) with TenReadTest
