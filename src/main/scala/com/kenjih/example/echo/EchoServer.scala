package com.kenjih.example.echo

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}

object EchoServer {

  private [echo] final val PORT = 8007

  def main(args: Array[String]): Unit = {
    val bossGroup = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.SO_BACKLOG, Int.box(100))
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
            val p = ch.pipeline()
            p.addLast(new EchoServerHandler())
          }
        })
      val f = b.bind(PORT).sync()
      f.channel().closeFuture().sync()
    } finally  {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
}
