package org.hammerlab.pageant.suffixes.pdc3

import org.hammerlab.magic.test.spark.HasKryoSuite

class JoinedSerializerTest
  extends HasKryoSuite {

  def opt(i: Int) = if (i == n) None else Some(i.toLong)

  def test(name: String)(size: Int, t0: Int, t1: Int, n0: Int, n1: Int): Unit = {
    super.test(name) {
      checkKryoRoundTrip(Joined(opt(t0), opt(t1), opt(n0), opt(n1)), size)
    }
  }

  // Sentinel value for empty optional numbers.
  private val n: Int = -1

  test("t0-t1-n0-n1")(17,  1,  2,  3,  4)

  test("t0-t1-n0")(15,  1,  2,  3,  n)
  test("t0-t1-n1")(15,  1,  2,  n,  4)
  test("t0-n0-n1")(15,  1,  n,  3,  4)
  test("t1-n0-n1")(15,  n,  2,  3,  4)

  test("t0-t1")(13,  1,  2,  n,  n)
  test("t0-n0")(13,  1,  n,  3,  n)
  test("t0-n1")(13,  1,  n,  n,  4)
  test("t1-n0")(13,  n,  2,  3,  n)
  test("t1-n1")(13,  n,  2,  n,  4)
  test("n0-n1")(13,  n,  n,  3,  4)

  test("t0")(11,  1,  n,  n,  n)
  test("t1")(11,  n,  2,  n,  n)
  test("n0")(11,  n,  n,  3,  n)
  test("n1")(11,  n,  n,  n,  4)

  test("none")(9,  n,  n,  n,  n)
}
