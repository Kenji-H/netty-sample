package com.kenjih.example.discard

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

object DiscardClient {
  private[discard] final val HOST = "127.0.0.1"
  private[discard] final val PORT = 8009
  private[discard] final val SIZE = 32

  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            val p = ch.pipeline
            p.addLast(new DiscardClientHandler())
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
