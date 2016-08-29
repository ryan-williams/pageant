package org.hammerlab.pageant.fm.blocks

import org.hammerlab.magic.test.spark.HasKryoSuite
import org.hammerlab.pageant.utils.PageantSuite

class BWTRunSerializerTest
  extends PageantSuite
    with HasKryoSuite {

  test("simple") {
    val t = 0.toByte
    for {
      n <- 1 to 15
    } {
      withClue(s"$n:") {
        checkKryoRoundTrip(BWTRun(t, n), 1)
      }
    }

    for {
      n <- 16 to 100
    } {
      withClue(s"$n:") {
        checkKryoRoundTrip(BWTRun(t, n), 2)
      }
    }
  }
}
