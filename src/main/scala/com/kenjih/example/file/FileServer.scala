package com.kenjih.example.file

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.util.CharsetUtil

object FileServer {

  private final val PORT = 8023

  def main(args: Array[String]): Unit = {
    val bossGroup   = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .option[Integer](ChannelOption.SO_BACKLOG, 100)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            val p = ch.pipeline
            p.addLast(
              new StringEncoder(CharsetUtil.UTF_8),
              new LineBasedFrameDecoder(8192),
              new StringDecoder(CharsetUtil.UTF_8),
              new ChunkedWriteHandler(),
              new FileServerHandler()
            )
          }
        })
        .bind(PORT)
        .sync()
        .channel
        .closeFuture
        .sync()
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
}
