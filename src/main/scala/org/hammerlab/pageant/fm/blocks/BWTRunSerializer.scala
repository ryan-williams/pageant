package org.hammerlab.pageant.fm.blocks

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.hammerlab.magic.math.VarNum

class BWTRunSerializer extends Serializer[BWTRun] {
  override def write(kryo: Kryo, os: Output, o: BWTRun): Unit = {
    val (low4, rest) = (o.n & 0xf, o.n >> 4)

    var byte = (o.t + (low4 << 4)).toByte
    if (rest > 0) byte = (byte | 0x8).toByte
    os.write(byte)
    if (rest > 0)
      VarNum.write(os, rest)
  }

  override def read(kryo: Kryo, is: Input, tpe: Class[BWTRun]): BWTRun = {
    val bytes = Array[Byte](0)
    val byte = is.readByte() //is.read(bytes).toByte
    val n = (byte & 0xf0) >> 4
    BWTRun(
      (byte & 0x7).toByte,
      if ((byte & 0x8) > 0)
        n + (VarNum.read(is).toInt << 4)
      else
        n
    )
  }
}

