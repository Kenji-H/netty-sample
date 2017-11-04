package com.kenjih.example.telnet

import java.io.{BufferedReader, InputStreamReader}

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

import scala.util.control.Breaks

object TelnetClient {
  private final val HOST = "127.0.0.1"
  private final val PORT = 8023

  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new TelnetClientInitializer())

      val ch                             = b.connect(HOST, PORT).sync().channel
      var lastWriteFuture: ChannelFuture = null
      val in                             = new BufferedReader(new InputStreamReader(System.in))

      val breaks = new Breaks()
      breaks.breakable {
        while (true) {
          val line = in.readLine()
          if (line == null)
            breaks.break()
          lastWriteFuture = ch.writeAndFlush(line + "\r\n")
          if ("bye".equals(line.toLowerCase())) {
            ch.closeFuture.sync()
            breaks.break()
          }
        }
        if (lastWriteFuture != null) {
          lastWriteFuture.sync()
        }
      }
    } finally {
      group.shutdownGracefully()
    }
  }
}
