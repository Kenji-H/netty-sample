package com.kenjih.example.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.{
  ChannelFuture,
  ChannelFutureListener,
  ChannelHandlerContext,
  SimpleChannelInboundHandler
}

class DiscardClientHandler extends SimpleChannelInboundHandler[Any] {
  private var content: ByteBuf           = _
  private var ctx: ChannelHandlerContext = _
  private var counter: Long              = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    this.ctx = ctx
    content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE)
    generateTraffic()
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    content.release()
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: Any): Unit = {
    // server is supposed to send nothing.
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

  private def generateTraffic(): Unit = {
    ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficeGenerator)
  }

  private final val trafficeGenerator = new ChannelFutureListener {
    override def operationComplete(future: ChannelFuture): Unit = {
      if (future.isSuccess) {
        generateTraffic()
      } else {
        future.cause().printStackTrace()
        future.channel.close()
      }
    }
  }
}
