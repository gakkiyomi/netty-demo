package ink.fangcong.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author: fangcong
 * @description: Echo 客户端将会:
 *                  (1)连接到服务器;
 *                  (2)发送一个或者多个消息;
 *                  (3)对于每个消息，等待并接收从服务器发回的相同的消息;
 *                  (4)关闭连接。
 *               编写客户端所涉及的两个主要代码部分也是业务逻辑和引导，和服务器中的一样。
 * @create: Created by work on 2020-11-18 09:39
 **/
public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public void start() throws Exception{
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 7777;
        String host = "localhost";
        new Client(host,port).start();
    }


}
