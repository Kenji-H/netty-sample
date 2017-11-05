package com.kenjih.example.securechat

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate

object SecureChatServer {
  private final val PORT = 8992

  def main(args: Array[String]): Unit = {
    val ssc    = new SelfSignedCertificate()
    val sslCtx = SslContextBuilder.forServer(ssc.certificate, ssc.privateKey).build()

    val bossGroup   = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new SecureChatServerInitializer(sslCtx))
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
