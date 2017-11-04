package com.kenjih.example.telnet

import java.net.InetAddress
import java.util.Date

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}

@Sharable
class TelnetServerHandler extends SimpleChannelInboundHandler[String] {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.write(s"Welcome to ${InetAddress.getLocalHost.getHostName}!\r\n")
    ctx.write(s"It is ${new Date()} now.\r\n")
    ctx.flush()
  }

  override def channelRead0(ctx: ChannelHandlerContext, request: String): Unit = {
    var close = false
    val response =
      if (request.isEmpty) "Please type something.\r\n"
      else {
        if ("bye".equals(request.toLowerCase)) {
          close = true
          "Have a good day!\r\n"
        } else {
          s"Did you say '$request'?\r\n"
        }
      }
    val future = ctx.write(response)
    if (close) {
      future.addListener(ChannelFutureListener.CLOSE)
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}
