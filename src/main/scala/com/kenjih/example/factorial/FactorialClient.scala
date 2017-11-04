package com.kenjih.example.factorial

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

object FactorialClient {
  private[factorial] final val HOST  = "127.0.0.1"
  private[factorial] final val PORT  = 8322
  private[factorial] final val COUNT = 1000

  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new FactorialClientInitializer())
      val f       = b.connect(HOST, PORT).sync()
      val handler = f.channel.pipeline.last().asInstanceOf[FactorialClientHandler]
      println(s"Factorial of $COUNT is ${handler.getFactorial}")
    } finally {
      group.shutdownGracefully()
    }
  }
}
