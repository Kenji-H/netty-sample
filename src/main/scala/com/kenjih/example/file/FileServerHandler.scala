package com.kenjih.example.file

import java.io.RandomAccessFile

import io.netty.channel.{
  ChannelFutureListener,
  ChannelHandlerContext,
  DefaultFileRegion,
  SimpleChannelInboundHandler
}

class FileServerHandler extends SimpleChannelInboundHandler[String] {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush("HELLO: Type the path of the file to retrieve.\n")
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    var raf: RandomAccessFile = null
    var length: Long          = -1
    try {
      raf = new RandomAccessFile(msg, "r")
      length = raf.length()
    } catch {
      case e: Exception => {
        ctx.writeAndFlush(s"ERR: ${e.getClass.getSimpleName}: ${e.getMessage}\n")
        return
      }
    } finally {
      if (length < 0 && raf != null) {
        raf.close()
      }
    }

    ctx.write(s"OK: ${raf.length}\n")
    ctx.write(new DefaultFileRegion(raf.getChannel, 0, length))
    ctx.writeAndFlush("\n")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    if (ctx.channel.isActive) {
      ctx
        .writeAndFlush(s"ERR: ${cause.getClass.getSimpleName}: ${cause.getMessage}\n")
        .addListener(ChannelFutureListener.CLOSE)
    }
  }
}
