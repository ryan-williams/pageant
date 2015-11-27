package org.hammerlab.pageant.serialization

import java.io.ByteArrayInputStream

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import org.apache.spark.serializer.{KryoSerializer, KryoRegistrator}
import org.bdgenomics.utils.misc.SparkFunSuite
import org.hammerlab.pageant.utils.Utils._
import org.scalatest.Matchers

import JavaSerialization._
import KryoSerialization._

import scala.collection.immutable.StringOps

class SerializationTest extends SparkFunSuite with Matchers with KryoFooRegistrarTest {
  val l = List("aaaaaaaa", "bbbbbbbb", "cccccccc")

  var ks: KryoSerializer = null
  implicit var kryo: Kryo = null

  sparkBefore("init-kryo") {
    ks = new KryoSerializer(sc.getConf)
    kryo = ks.newKryo()
  }

  sparkTest("java list") {
    val bytes = javaBytes(l)
    bytes.length should be(263)
    javaRead[List[String]](bytes) should be(l)
  }

  sparkTest("kryo list") {
    val bytes = kryoBytes(l)
    bytes.length should be(32)
    kryoRead[List[String]](bytes) should be(l)
  }

  sparkTest("java foo") {
    val foo = Foo(187, "dddddddd")
    val bytes = javaBytes(foo)
    bytes.length should be(104)
    javaRead[Foo](bytes) should be(foo)
  }

  sparkTest("kryo foo") {
    val foo = Foo(187, "dddddddd")
    val bytes = kryoBytes(foo)
    bytes.length should be(12)
    kryoRead[Foo](bytes) should be(foo)
  }

  sparkTest("kryo foo class") {
    kryoBytes(Foo(127, "dddddddd"), includeClass = true).length should be(13)
    kryoBytes(Foo(128, "dddddddd"), includeClass = true).length should be(13)
    kryoBytes(Foo(129, "dddddddddddddddd"), includeClass = true).length should be(21)
    kryoBytes(Foo(130, "dddddddddddddddd"), includeClass = true).length should be(21)

    val foo = Foo(187, "dddddddd")
    val bytes = kryoBytes(foo, includeClass = true)
    bytes.length should be(13)
    kryoRead[Foo](bytes, includeClass = true) should be(foo)
  }

  def bs(bytes: Array[Byte]): Unit = {
    println(bytesToHex(bytes))
  }

  sparkTest("kryo 1 string") {
    val bytes = kryoBytes("aaaaaaaa")
    bytes.length should be(9)
    val bais = new ByteArrayInputStream(bytes)
    kryoRead[String](bais) should be("aaaaaaaa")
  }

  sparkTest("kryo class and 1 string") {
    val bytes = kryoBytes("aaaaaaaa", includeClass = true)
    bytes.length should be(10)
    val bais = new ByteArrayInputStream(bytes)
    kryoRead[String](bais, includeClass = true) should be("aaaaaaaa")
  }

  sparkTest("kryo strings") {
    val bytes = kryoBytes("aaaaaaaa") ++ kryoBytes("bbbbbbbb")
    bytes.length should be(18)
    val ip = new Input(bytes)
    kryoRead[String](ip, includeClass = false) should be("aaaaaaaa")
    kryoRead[String](ip, includeClass = false) should be("bbbbbbbb")
    ip.close()
  }

  sparkTest("kryo class and strings") {
    val bytes = kryoBytes("aaaaaaaa", includeClass = true) ++ kryoBytes("bbbbbbbb", includeClass = true)
    bytes.length should be(20)
    val ip = new Input(bytes)
    kryoRead[String](ip, includeClass = true) should be("aaaaaaaa")
    kryoRead[String](ip, includeClass = true) should be("bbbbbbbb")
    ip.close()
  }
}

