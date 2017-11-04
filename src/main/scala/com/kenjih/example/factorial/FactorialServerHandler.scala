package com.kenjih.example.factorial

import java.math.BigInteger

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}

class FactorialServerHandler extends SimpleChannelInboundHandler[BigInteger] {
  private var lastMultiplier = new BigInteger("1")
  private var factorial      = new BigInteger("1")

  override def channelRead0(ctx: ChannelHandlerContext, msg: BigInteger): Unit = {
    lastMultiplier = msg
    factorial = factorial.multiply(msg)
    ctx.writeAndFlush(factorial)
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    println(s"Factorial of $lastMultiplier is $factorial")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
