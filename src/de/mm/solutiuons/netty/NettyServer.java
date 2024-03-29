package de.mm.solutiuons.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpServerCodec;

import de.mm.solutiuons.HttpHandler;

public class NettyServer {
    private final HttpHandler handler;
    private Channel channel;
    public NettyServer(HttpHandler handler) {
        this.handler = handler;
    }
    public void startServer() throws Exception {
        ChannelFactory factory =
            new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
//                return Channels.pipeline(new ServerHandler(handler));
                return Channels.pipeline(
                    new HttpServerCodec(), 
                    new ServerHandler(handler)
                );
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        channel = bootstrap.bind(new InetSocketAddress(8888));
        System.out.println("Server Started!");
    }
    
    public void shutdownServer() throws Exception {
        channel.close().await();
        System.out.println("Server Stopped!");
    }
}