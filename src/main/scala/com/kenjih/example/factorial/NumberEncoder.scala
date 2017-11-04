package com.kenjih.example.factorial

import java.math.BigInteger

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class NumberEncoder extends MessageToByteEncoder[Number] {
  override def encode(ctx: ChannelHandlerContext, msg: Number, out: ByteBuf): Unit = {
    val v = msg match {
      case i: BigInteger => msg.asInstanceOf[BigInteger]
      case _             => new BigInteger(String.valueOf(msg))
    }
    val data = v.toByteArray
    out.writeByte('F'.asInstanceOf[Byte])
    out.writeInt(data.length)
    out.writeBytes(data)
  }
}
