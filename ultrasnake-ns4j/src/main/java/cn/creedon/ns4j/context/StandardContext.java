package cn.creedon.ns4j.context;

import cn.creedon.ns4j.http.ssl.SSLContextStore;
import cn.creedon.ns4j.traffic.TrafficStore;
import lombok.Data;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：上下文包装器
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
public final class StandardContext {

    private static final ThreadLocal<? extends StandardContext> threadLocal = ThreadLocal.withInitial(() -> {
        try {
            return StandardContext.class.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * 启动类的Class
     */
    private Class<?> bootstrapClass;

    private String environment;

    private int port;
    private String serverName;
    private String serverPath;
    private String[] handlerPkg;
    private boolean printLog = true;
    private boolean openGzip = true;
    private boolean openTraffic = false;
    private TrafficStore trafficStore;
    private SSLContextStore sslStore;

    public static StandardContext getCurrentContext() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

}
