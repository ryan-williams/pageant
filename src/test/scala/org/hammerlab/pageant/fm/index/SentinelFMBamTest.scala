package org.hammerlab.pageant.fm.index

import org.apache.spark.SparkContext
import org.hammerlab.pageant.fm.index.FMIndex.FMI
import org.hammerlab.pageant.fm.utils.Utils

abstract class SentinelFMBamTest extends FMBamTest {
  override def name: String = "sentinel"
  override def generateFM(sc: SparkContext): FMI = {
    val bases =
      sc.parallelize(
        sc
          .textFile(s"src/test/resources/1000.reads")
          .take(num)
          .flatMap(_.map(Utils.toI).toVector :+ 0.toByte),
        numPartitions
      )

    SentinelFM(bases, blockSize)
  }
}

class SentinelFMBamTestThousand extends SentinelFMBamTest with ThousandReadTest
class SentinelFMBamTestHundred extends SentinelFMBamTest with HundredReadTest
class SentinelFMBamTestTen extends SentinelFMBamTest with TenReadTest
