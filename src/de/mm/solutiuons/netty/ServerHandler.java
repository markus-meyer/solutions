package de.mm.solutiuons.netty;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import de.mm.solutiuons.HttpHandler;

public class ServerHandler extends SimpleChannelHandler { 
    private final HttpHandler handler;

    public ServerHandler(HttpHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception{
        HttpRequest request = (HttpRequest) e.getMessage();
        String uri = request.getUri();
        if(uri.equals("/hallo"))
            writeHttpResponse(e, HttpResponseStatus.OK, "Hello, world!");
        else {
            HttpStaticFileServerHandler fileServer = new HttpStaticFileServerHandler();
            fileServer.messageReceived(ctx, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) { 
        e.getCause().printStackTrace();
        StringWriter sw = new StringWriter();
        e.getCause().printStackTrace(new PrintWriter(sw));
        writeHttpResponse(e, HttpResponseStatus.INTERNAL_SERVER_ERROR, sw.toString());
        Channel ch = e.getChannel();
        ch.close();
    }
    
    private void writeHttpResponse(final ChannelEvent e, HttpResponseStatus status, String  content){
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setContent(ChannelBuffers.copiedBuffer(content, Charset.forName("UTF-8")));
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        e.getChannel().write(response).addListener(new ChannelFutureListener() {            
            public void operationComplete(ChannelFuture future) throws Exception {
                future.getChannel().close();                
            }});
    }
}