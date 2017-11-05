package com.kenjih.example.securechat

import java.io.{BufferedReader, InputStreamReader}

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

import scala.util.control.Breaks

object SecureChatClient {
  private[securechat] final val HOST = "127.0.0.1"
  private[securechat] final val PORT = 8992

  def main(args: Array[String]): Unit = {
    val sslCtx =
      SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new SecureChatClientInitializer(sslCtx))

      val ch = b.connect(HOST, PORT).sync.channel

      var lastWriteFuture: ChannelFuture = null
      val in                             = new BufferedReader(new InputStreamReader(System.in))
      val breaks                         = new Breaks()
      breaks.breakable {
        while (true) {
          val line = in.readLine()
          if (line == null)
            breaks.break()
          lastWriteFuture = ch.writeAndFlush(s"$line\r\n")
          if ("bye".equals(line.toLowerCase)) {
            ch.closeFuture.sync()
            breaks.break()
          }
        }
      }

      if (lastWriteFuture != null) {
        lastWriteFuture.sync()
      }
    } finally {
      group.shutdownGracefully()
    }
  }

}
