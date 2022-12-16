package cn.creedon.ns4j;

import cn.creedon.ns4j.boot.Ns4jConfiguration;
import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.exceptions.ServerStartupException;
import cn.creedon.ns4j.handler.Ns4jChannelInitializer;
import cn.creedon.ns4j.context.notice.Ns4jNotice;
import cn.creedon.ns4j.http.ssl.SSLContextStore;
import cn.creedon.ns4j.layout.BannerLayout;
import cn.creedon.ns4j.layout.ContextLayout;
import cn.creedon.ns4j.loader.ResourceProcessor;
import cn.creedon.ns4j.traffic.TrafficStore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：服务入口
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Slf4j
public enum NettyServerStartup {

    INSTANCE;

    private static final OutputStream OUT = System.out;

    private final StandardContext standardContext = StandardContext.getCurrentContext();

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;

    private final AtomicBoolean isBindRef = new AtomicBoolean(false);

    private final Thread serverShutdownThread = new Thread("SERVER-SHUTDOWN-THREAD") {
        @Override
        public void run() {
            try {
                NettyServerStartup.INSTANCE.destroy();
            } catch (Exception ignored) {
            }
        }
    };

    /**
     * 核心启动入口
     *
     * @param clazz 启动类的Class
     * @param args  启动参数
     */
    public void boot(Class<?> clazz, String[] args) {
        final boolean isBind = NettyServerStartup.INSTANCE.isBind();
        if (isBind) {
            NettyServerStartup.INSTANCE.unbind();
            return;
        }
        write(new BannerLayout().format());
        Ns4jConfiguration.INSTANCE.parse(clazz, args);
        write(new Date() + " +++++ " + new ContextLayout().format(standardContext));
        ResourceProcessor.INSTANCE.load();
        NettyServerStartup.INSTANCE.bind();
    }

    public boolean isBind() {
        return isBindRef.get();
    }

    public void bind() {
        if (!isBindRef.compareAndSet(false, true)) {
            throw new ServerStartupException("服务端口 [ " + standardContext.getPort() + " ] 已经绑定");
        }
        try {
            initNettyServer();
            serverBootstrap.bind(new InetSocketAddress(standardContext.getPort())).addListener(future -> {
                if (future.isSuccess() && isBind()) {
                    write(new Date() + " +++++ 服务端口 [ " + standardContext.getPort() + " ] 绑定成功");
                    List<Class<?>> notices = ResourceProcessor.INSTANCE.notices();
                    if (notices != null && notices.size() > 0) {
                        for (Class<?> notice : notices) {
                            Ns4jNotice ns4jNotice = (Ns4jNotice) notice.newInstance();
                            ns4jNotice.run();
                        }
                    }
                }
            });
            Runtime.getRuntime().addShutdownHook(serverShutdownThread);
        } catch (Exception e) {
            unbind();
            throw new ServerStartupException("服务绑定异常" + e, e);
        }
    }

    public void unbind() {
        if (!isBindRef.compareAndSet(true, false)) {
            throw new ServerStartupException("服务端口 [ " + standardContext.getPort() + " ] 已经解绑");
        }
    }

    public void destroy() {
        Runtime.getRuntime().removeShutdownHook(serverShutdownThread);
        if (isBind()) {
            unbind();
        }
        if (standardContext != null) {
            StandardContext.remove();
        }
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        write(new Date() + " +++++ 服务销毁完成");
    }

    /**
     * 初始化Netty服务
     */
    private void initNettyServer() {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup(200);
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 没有查到 'SO_TIMEOUT' 选项
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new Ns4jChannelInitializer(standardContext));
    }

    public void setSSLContextStore(SSLContextStore sslStore) {
        this.standardContext.setSslStore(sslStore);
    }

    public void setTrafficStore(TrafficStore trafficStore) {
        this.standardContext.setTrafficStore(trafficStore);
    }

    private void write(String context) {
        try {
            OUT.write(String.format("%s\r\n", context).getBytes(StandardCharsets.UTF_8));
            OUT.flush();
        } catch (IOException e) {
            throw new ServerStartupException("启动控制台输出异常" + e, e);
        }
    }
}
