package ink.fangcong.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author: fangcong
 * @description: 如同服务器，客户端将拥有一个用来处理数据的ChannelInboundHandler。
 *               在这个场景下，你将扩展 SimpleChannelInboundHandler 类以处理所有必须的任务，这要求重写下面的方法:
 *                  channelActive()——在到服务器的连接已经建立之后将被调用;
 *                  channelRead0()1——当从服务器接收到一条消息时被调用;
 *                  exceptionCaught()——在处理过程中引发异常时被调用。
 * @create: Created by work on 2020-11-18 10:26
 **/


/*
SimpleChannelInboundHandler 与 ChannelInboundHandler 你可能会想:为什么我们在客户端使用的是 SimpleChannelInboundHandler，而不是在 Echo-
ServerHandler 中所使用的 ChannelInboundHandlerAdapter呢?
这和两个因素的相互作用有 关:业务逻辑如何处理消息以及 Netty 如何管理资源。
在客户端，当 channelRead0()方法完成时，你已经有了传入消息，并且已经处理完它了。当该方 法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的 ByteBuf 的内存引用。
在 EchoServerHandler 中，你仍然需要将传入消息回送给发送者，而write()操作是异步的，直 到 channelRead()方法返回后可能仍然没有完成。为此，EchoServerHandler 扩展了ChannelInboundHandlerAdapter，其在这个时间点上不会释放消息。
消息在 EchoServerHandler 的 channelReadComplete()方法中，当 writeAndFlush()方 法被调用时被释放。
 */

@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("Client received: "+ buf.toString(CharsetUtil.UTF_8));
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好,服务器!",
                CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
