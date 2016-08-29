package org.hammerlab.pageant.fm.utils

import org.apache.spark.SparkContext
import org.hammerlab.pageant.fm.index.FMIndex
import org.hammerlab.pageant.utils.PageantSuite

trait FMSuite extends PageantSuite {

  var fm: FMIndex = _
  def initFM(sc: SparkContext): FMIndex

  override def beforeAll(): Unit = {
    super.beforeAll()
    fm = initFM(sc)
  }
}
