package com.kenjih.example.factorial

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.compression.{ZlibCodecFactory, ZlibWrapper}

class FactorialServerInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val pipeline = ch.pipeline
    pipeline
      .addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP))
      .addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP))
      .addLast(new BigIntegerDecoder())
      .addLast(new NumberEncoder)
      .addLast(new FactorialServerHandler())
  }
}
