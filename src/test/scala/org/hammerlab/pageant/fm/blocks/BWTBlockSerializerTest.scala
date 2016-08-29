package org.hammerlab.pageant.fm.blocks

import org.hammerlab.magic.test.spark.HasKryoSuite
import org.hammerlab.pageant.fm.index.RunLengthIterator
import org.hammerlab.pageant.fm.utils.Counts
import org.hammerlab.pageant.fm.utils.Utils.toI
import org.hammerlab.pageant.utils.PageantSuite

class BWTBlockSerializerTest
  extends PageantSuite
    with HasKryoSuite {

  def lines(str: String): Seq[String] =
    str
      .trim
      .stripMargin
      .split("\n")

  def testBlockSizes(name: String,
                     seq: String,
                     expectedPieces: String,
                     fullBlockSize: Int,
                     rlBlockSize: Int,
                     start: Int = 0,
                     counts: Counts = Counts()): Unit = {
    test(name) {
      val ts =
        lines(seq)
          .mkString("")
          .map(toI)

      val rl = RunLengthIterator(ts).toArray

      rl.mkString(" ") should be(lines(expectedPieces).mkString(" "))

      checkKryoRoundTrip(FullBWTBlock(start, counts, ts.toArray), fullBlockSize)
      checkKryoRoundTrip(RunLengthBWTBlock(start, counts, rl), rlBlockSize)
    }
  }

  testBlockSizes(
    "simple block",
    """
      |AAAAAAAAAA
      |CCCCCCCCCC
      |GGGGGGGGGG
      |TTTTTTTTTT
      |""",
    "10A 10C 10G 10T",
    48,
    12
  )

  testBlockSizes(
    "complex block",
    "ATTTTTAAGAGAAAAAACTGAAAGTTAATAGAGAGGTGACTCAGATCCAGAGGTGGAAGAGGAAGGAAGCTTGGAACCCTATAGAGTTGCTGAGTGCCAGG",
    """
      |1A 5T 2A 1G 1A 1G 6A 1C 1T 1G
      |3A 1G 2T 2A 1T 1A 1G 1A 1G 1A
      |2G 1T 1G 1A 1C 1T 1C 1A 1G 1A
      |1T 2C 1A 1G 1A 2G 1T 2G 2A 1G
      |1A 2G 2A 2G 2A 1G 1C 2T 2G 2A
      |3C 1T 1A 1T 1A 1G 1A 1G 2T 1G
      |1C 1T 1G 1A 1G 1T 1G 2C 1A 2G
      |""",
    110,
    79
  )

  testBlockSizes(
    "repeat block",
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGG",
    "32A 48C 15G",
    104,
    13
  )
}
