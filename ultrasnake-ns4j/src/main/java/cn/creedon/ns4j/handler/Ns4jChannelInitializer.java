package cn.creedon.ns4j.handler;

import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.exceptions.ServerStartupException;
import cn.creedon.ns4j.http.ssl.SSLContextStore;
import cn.creedon.ns4j.http.ssl.SecureSSLContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;

import static cn.creedon.ns4j.boot.Ns4jConstants.MAX_CONTENT_LENGTH;


/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：请在此处加入功能描述
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Slf4j
public class Ns4jChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final StandardContext standardContext;

    public Ns4jChannelInitializer(StandardContext standardContext) {
        this.standardContext = standardContext;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        SSLContextStore sslStore = standardContext.getSslStore();
        if (sslStore != null && sslStore.isOpenSSL()) {
            SSLEngine engine = SecureSSLContextFactory.getSingleInstance(sslStore).createSSLEngine();
            engine.setNeedClientAuth(sslStore.isNeedClientAuth());
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            String[] protocols = engine.getEnabledProtocols();
            if (protocols == null || protocols.length == 0) {
                throw new ServerStartupException("ssl protocol error.");
            }
            engine.setEnabledProtocols(protocols);
            socketChannel.pipeline().addLast("ssl", new SslHandler(engine));
        }
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast("httpAggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        socketChannel.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
        if (standardContext.isOpenGzip()) {
            socketChannel.pipeline().addLast("deflater", new HttpContentCompressor());
        }
        socketChannel.pipeline().addLast(new Ns4jChannelHandler(standardContext));
    }
}