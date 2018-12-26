/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.bootstrap.*;
/**
 *
 * @author Nikita
 */
public class TestClient {
   
    /**
     * Метод создает новое клиентское подключение к серверу
     * @param args параметры запуска
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String host ="localhost";
        int port = Integer.parseInt("8080");
        EventLoopGroup workerGroup = new NioEventLoopGroup();        
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });            
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }        
    }
        
}
    

