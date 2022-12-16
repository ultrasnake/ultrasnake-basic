package cn.creedon.ns4j.handler;

import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.http.Ns4jHttpHandler;
import cn.creedon.ns4j.model.Ns4jRR;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;

import static cn.creedon.ns4j.boot.Ns4jConstants.CONTENT_TYPE_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

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
@ChannelHandler.Sharable
public class Ns4jChannelHandler extends SimpleChannelInboundHandler<Object> {

    private FullHttpRequest request;

    private final StandardContext standardContext;

    public Ns4jChannelHandler(StandardContext standardContext) {
        this.standardContext = standardContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            request = (FullHttpRequest) msg;
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
        }
        if (msg instanceof LastHttpContent) {
            LastHttpContent content = (LastHttpContent) msg;
            Object res = Ns4jHttpHandler.process(request, content, ctx, standardContext);
            if (res != null) {
                log.error("", (Exception) res);
                Ns4jHttpHandler.writeResponse(
                        request,
                        content,
                        JSON.toJSONString(Ns4jRR.error500()),
                        ctx,
                        CONTENT_TYPE_JSON
                );
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause.getMessage().contains("Connection reset by peer") || cause instanceof SSLException)) {
            log.error("exceptionCaught", cause);
        }
        ctx.close();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

}