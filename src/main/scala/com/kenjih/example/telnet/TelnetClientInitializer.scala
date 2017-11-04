package com.kenjih.example.telnet

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}

class TelnetClientInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline
    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter: _*))
    pipeline.addLast(TelnetClientInitializer.DECODER)
    pipeline.addLast(TelnetClientInitializer.ENCODER)
    pipeline.addLast(TelnetClientInitializer.CLIENT_HANDLER)
  }
}

object TelnetClientInitializer {
  private final val DECODER        = new StringDecoder()
  private final val ENCODER        = new StringEncoder()
  private final val CLIENT_HANDLER = new TelnetClientHandler()
}
