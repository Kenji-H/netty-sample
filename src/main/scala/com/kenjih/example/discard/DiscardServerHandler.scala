package com.kenjih.example.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

class DiscardServerHandler extends SimpleChannelInboundHandler[Any] {

  override def channelRead0(ctx: ChannelHandlerContext, msg: Any): Unit = {
    // do nothing
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
