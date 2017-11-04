package com.kenjih.example.telnet

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}

class TelnetServerInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline()
    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter: _*))
    pipeline.addLast(TelnetServerInitializer.DECODER)
    pipeline.addLast(TelnetServerInitializer.ENCODER)
    pipeline.addLast(TelnetServerInitializer.SERVER_HANDLER)
  }
}

object TelnetServerInitializer {
  private final val DECODER        = new StringDecoder()
  private final val ENCODER        = new StringEncoder()
  private final val SERVER_HANDLER = new TelnetServerHandler()
}
