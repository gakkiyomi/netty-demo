package ink.fangcong.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author: fangcong
 * @description:
 * @create: Created by work on 2020-11-18 09:39
 **/
public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {

        new Server(7777).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();//创建EventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap(); //创建 ServerBootStrap
            b.group(group)
                    .channel(NioServerSocketChannel.class) // 指定所使用的NIO传输Channel
                    .localAddress(new InetSocketAddress(port)) //使用制定端口的套接字地址
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler); //将我们的处理逻辑Handler放置到pipeline里面 由此可见可以使用多个handler来一起实现逻辑处理
                        }
                    });
            System.out.println("echo 服务器启动 监听端口: 7777");
            ChannelFuture f = b.bind().sync(); // 异步绑定服务器 调用sync() 方法阻塞等待到直到绑定完成
            f.channel().closeFuture().sync(); //获取Channel的CloseFuture,并且阻塞当前线程直到它完成
        } finally {
            group.shutdownGracefully().sync(); //释放资源
        }
    }
}
