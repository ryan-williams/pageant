package org.hammerlab.pageant.suffixes

package object util {
  def longToCmpFnReturn(l: Long) =
    if (l < 0) -1
    else if (l > 0) 1
    else 0
}
