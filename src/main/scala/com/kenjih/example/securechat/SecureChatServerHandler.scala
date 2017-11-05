package com.kenjih.example.securechat

import java.net.InetAddress

import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.ssl.SslHandler
import io.netty.util.concurrent.{Future, GenericFutureListener, GlobalEventExecutor}

class SecureChatServerHandler extends SimpleChannelInboundHandler[String] {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.pipeline
      .get(classOf[SslHandler])
      .handshakeFuture
      .addListener(
        new GenericFutureListener[Future[_ >: Channel]] {
          override def operationComplete(future: Future[_ >: Channel]): Unit = {
            ctx.writeAndFlush(
              s"Welcome to ${InetAddress.getLocalHost.getHostName} secure chat service!\n")
            ctx.writeAndFlush(
              s"Your session is protected by ${ctx.pipeline.get(classOf[SslHandler]).engine.getSession.getCipherSuite} cipher suite.\n")
            SecureChatServerHandler.channels.add(ctx.channel)
          }
        }
      )
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    SecureChatServerHandler.channels.forEach { c =>
      if (c != ctx.channel) {
        c.writeAndFlush(s"[${ctx.channel.remoteAddress}] $msg\n")
      } else {
        c.writeAndFlush(s"[you] $msg\n")
      }
    }

    if ("bye".equals(msg.toLowerCase)) {
      ctx.close()
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

object SecureChatServerHandler {
  private final val channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
}
