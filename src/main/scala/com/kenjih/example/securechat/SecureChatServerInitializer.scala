package com.kenjih.example.securechat

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}
import io.netty.handler.ssl.SslContext

class SecureChatServerInitializer(private val sslCtx: SslContext)
    extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline
    pipeline.addLast(sslCtx.newHandler(ch.alloc()))
    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter(): _*))
    pipeline.addLast(new StringDecoder())
    pipeline.addLast(new StringEncoder())
    pipeline.addLast(new SecureChatServerHandler())
  }
}
