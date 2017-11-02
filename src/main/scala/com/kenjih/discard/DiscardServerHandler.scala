package com.kenjih.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil

class DiscardServerHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    try {
        while (in.isReadable) {
          System.out.print(in.readByte.toChar)
          System.out.flush()
        }
    } finally {
      ReferenceCountUtil.release(msg)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close
  }
}