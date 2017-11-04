package com.kenjih.example.echo

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

object EchoClient {
  private[echo] final val HOST = "127.0.0.1"
  private[echo] final val PORT = 8007
  private[echo] final val SIZE = 256

  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .option(ChannelOption.TCP_NODELAY, Boolean.box(true))
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            val p = ch.pipeline
            p.addLast(new EchoClientHandler())
          }
        })
        .connect(HOST, PORT)
        .sync()
        .channel
        .closeFuture
        .sync()
    } finally {
      group.shutdownGracefully()
    }
  }
}
