package com.kenjih.example.echo

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class EchoClientHandler extends ChannelInboundHandlerAdapter {

  private final val firstMessage = Unpooled.buffer(EchoClient.SIZE)
  for (i <- 0 until firstMessage.capacity()) {
    firstMessage.writeByte(i.asInstanceOf[Byte])
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(firstMessage)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    ctx.write(msg)
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}
