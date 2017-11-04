package com.kenjih.example.factorial

import java.math.BigInteger
import java.util.List

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.{ByteToMessageDecoder, CorruptedFrameException}

class BigIntegerDecoder extends ByteToMessageDecoder {
  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: List[AnyRef]): Unit = {
    if (in.readableBytes() < 5) {
      return
    }
    in.markReaderIndex()

    in.readUnsignedByte match {
      case 'F'         =>
      case magicNumber => throw new CorruptedFrameException(s"Invalid magic number: $magicNumber")
    }

    val dataLength = in.readInt()
    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex()
      return
    }

    val decoded = new Array[Byte](dataLength)
    in.readBytes(decoded)

    out.add(new BigInteger(decoded))
  }
}
