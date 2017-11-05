package com.kenjih.example.securechat

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

class SecureChatClientHandler extends SimpleChannelInboundHandler[String] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    println(msg)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
