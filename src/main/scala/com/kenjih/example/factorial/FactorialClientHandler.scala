package com.kenjih.example.factorial

import java.math.BigInteger
import java.util.concurrent.LinkedBlockingQueue

import scala.util.control.Breaks
import io.netty.channel.{
  ChannelFuture,
  ChannelFutureListener,
  ChannelHandlerContext,
  SimpleChannelInboundHandler
}

class FactorialClientHandler extends SimpleChannelInboundHandler[BigInteger] {
  private var ctx: ChannelHandlerContext = _
  private var receivedMessages: Int      = 0
  private var next: Int                  = 1
  private final val answer               = new LinkedBlockingQueue[BigInteger]()

  def getFactorial(): BigInteger = {
    answer.take()
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    this.ctx = ctx
    sendNumbers()
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: BigInteger): Unit = {
    receivedMessages += 1
    if (receivedMessages == FactorialClient.COUNT) {
      ctx
        .channel()
        .close()
        .addListener(new ChannelFutureListener {
          override def operationComplete(future: ChannelFuture): Unit = {
            assert(answer.offer(msg))
          }
        })
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

  private def sendNumbers(): Unit = {
    val b                     = new Breaks
    var future: ChannelFuture = null
    b.breakable {
      for (i <- 0 until 4096) {
        if (next > FactorialClient.COUNT) b.break()
        future = ctx.write(next)
        next += 1
      }
    }
    if (next <= FactorialClient.COUNT) {
      assert(future != null)
      future.addListener(numberSender)
    }
    ctx.flush()
  }

  private final val numberSender = new ChannelFutureListener {
    override def operationComplete(future: ChannelFuture): Unit = future.isSuccess() match {
      case true => sendNumbers()
      case _ => {
        future.cause().printStackTrace()
        future.channel().close()
      }
    }
  }

}
