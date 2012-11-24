package de.mm.solutiuons;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import de.mm.solutiuons.netty.NettyServer;

public class Solution {
    private static HttpHandler handler = new HttpHandler(){
        @Override
        public String indexHtml() {
            return null;
        }
        
    };
    
    public static void main(String... args) throws Exception {
        System.out.println("Hit enter to stop!");
        NettyServer server = new NettyServer(handler);
        server.startServer();
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        bufferRead.readLine();
        server.shutdownServer();
        System.exit(0);
    }

}
