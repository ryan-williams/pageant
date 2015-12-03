package org.hammerlab.pageant.utils

import java.io.ByteArrayOutputStream

import com.esotericsoftware.kryo.io.{Input, Output}
import org.scalatest.{Matchers, FunSuite}

class VarLongTest extends FunSuite with Matchers {

  def testBytes(l: Long, expected: Array[Int]): Unit = {
    test(s"${l.toHexString}") {
      val baos = new ByteArrayOutputStream()

      val op = new Output(baos)
      VarLong.write(op, l)
      op.close()

      val bytes = baos.toByteArray
      bytes should be(expected.map(_.toByte))

      val ip = new Input(bytes)
      VarLong.read(ip) should be(l)
      ip.close()
    }
  }

  testBytes(0x7B, Array(0x7B))
  testBytes(0x7F, Array(0x7F))

  testBytes(0x80, Array(0x80, 0x01))
  testBytes(0x81, Array(0x81, 0x01))
  testBytes(0x82, Array(0x82, 0x01))
  testBytes(0x83, Array(0x83, 0x01))
  testBytes(0x84, Array(0x84, 0x01))
  testBytes(0x85, Array(0x85, 0x01))

  testBytes(0x3FFF, Array(0xFF, 0x7F))
  testBytes(0x4000, Array(0x80, 0x80, 0x01))
  testBytes(0x4001, Array(0x81, 0x80, 0x01))
  testBytes(0x4002, Array(0x82, 0x80, 0x01))
  testBytes(0x4003, Array(0x83, 0x80, 0x01))
  testBytes(0x4004, Array(0x84, 0x80, 0x01))

  testBytes(0x1FFFFF, Array(0xFF, 0xFF, 0x7F))
  testBytes(0x200000, Array(0x80, 0x80, 0x80, 0x01))
  testBytes(0x200001, Array(0x81, 0x80, 0x80, 0x01))
  testBytes(0x200002, Array(0x82, 0x80, 0x80, 0x01))
  testBytes(0x200003, Array(0x83, 0x80, 0x80, 0x01))
  testBytes(0x200004, Array(0x84, 0x80, 0x80, 0x01))

  testBytes(0xFFFFFFF, Array(0xFF, 0xFF, 0xFF, 0x7F))
  testBytes(0x10000000, Array(0x80, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x10000001, Array(0x81, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x10000002, Array(0x82, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x10000003, Array(0x83, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x10000004, Array(0x84, 0x80, 0x80, 0x80, 0x01))

  testBytes(0x7FFFFFFFFL, Array(0xFF, 0xFF, 0xFF, 0xFF, 0x7F))
  testBytes(0x800000000L, Array(0x80, 0x80, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x800000001L, Array(0x81, 0x80, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x800000002L, Array(0x82, 0x80, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x800000003L, Array(0x83, 0x80, 0x80, 0x80, 0x80, 0x01))
  testBytes(0x800000004L, Array(0x84, 0x80, 0x80, 0x80, 0x80, 0x01))

  testBytes(0x7FFFFFFFFFFFFFFFL, Array(0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x7F))

}