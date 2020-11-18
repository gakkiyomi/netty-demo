package ink.fangcong.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author: fangcong
 * @description: 所有的 Netty 服务器都需要以下两部分。
 *                  至少一个ChannelHandler— 该组件实现了服务器对从客户端接收的数据的处理，即它的业务逻辑。
 *                  引导— 这是配置服务器的启动代码。至少，它会将服务器绑定到它要监听连接请求的端口上。
 *               因为Echo 服务器会响应传入的消息，所以它需要实现 ChannelInboundHandler 接口，用 来定义响应入站事件的方法。
 *               这个简单的应用程序只需要用到少量的这些方法，所以继承 ChannelInboundHandlerAdapter 类也就足够了，它提供了 ChannelInboundHandler 的默认实现。
 *               我们感兴趣的方法是:
 *                  channelRead()— 对于每个传入的消息都要调用;
 *                  channelReadComplete()— 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息;
 *                  exceptionCaught()— 在读取操作期间，有异常抛出时会调用。
 * @create: Created by work on 2020-11-18 09:44
 **/
@ChannelHandler.Sharable // 标记一个channelHandler 可以被多个Channel安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received:" + in.toString(CharsetUtil.UTF_8));
        ctx.writeAndFlush(Unpooled.copiedBuffer("我很好，你呢?",
                CharsetUtil.UTF_8)); //给客户端返回消息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE); // 将未决消息冲刷到远程节点，并且关闭该Channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();  //打印错误堆栈信息
        ctx.close();  //关闭该Channel
        /*
        如果不捕获异常，会发生什么呢?
        每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的 实例链。
        在默认的情况下，ChannelHandler 会把对它的方法的调用转发给链中的下一个 ChannelHandler。
        因此，如果 exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被 传递到 ChannelPipeline 的尾端并被记录。
         */
    }
}
