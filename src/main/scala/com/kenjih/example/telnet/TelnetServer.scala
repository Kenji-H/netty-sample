package com.kenjih.example.telnet

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}

object TelnetServer {
  private final val PORT = 8023

  def main(args: Array[String]): Unit = {
    val bossGroup   = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new TelnetServerInitializer())
      b.bind(PORT).sync().channel().closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
}
