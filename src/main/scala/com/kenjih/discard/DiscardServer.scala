package com.kenjih.discard

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

object DiscardServer {
  @throws[Exception]
  def main(args: Array[String]): Unit = {
    var port = 0
    if (args.length > 0) port = args(0).toInt
    else port = 8080
    new DiscardServer(port).run()
  }
}

class DiscardServer(var port: Int) {
  @throws[Exception]
  def run(): Unit = {
    val bossGroup = new NioEventLoopGroup
    val workerGroup = new NioEventLoopGroup
    try {
      val b = new ServerBootstrap
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(
          new ChannelInitializer[SocketChannel]() {
            @throws[Exception]
            override def initChannel(ch: SocketChannel): Unit = {
              ch.pipeline.addLast(new DiscardServerHandler)
            }
          }
        ).option[Integer](ChannelOption.SO_BACKLOG, 128)
        .childOption[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)

      val f = b.bind(port).sync
      f.channel.closeFuture.sync

    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }
}

