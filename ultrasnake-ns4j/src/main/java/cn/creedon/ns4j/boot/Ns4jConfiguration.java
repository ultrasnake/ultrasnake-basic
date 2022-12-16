package cn.creedon.ns4j.boot;

import cn.creedon.ns4j.annotations.EnableNs4j;
import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.exceptions.ServerStartupException;

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
public enum Ns4jConfiguration {

    INSTANCE;

    Ns4jConfiguration() {
    }

    public void parse(Class<?> clazz, String[] args) {
        StandardContext context = StandardContext.getCurrentContext();
        final String environment = System.getProperty("ns4j.env");
        if (environment != null && !"".equals(environment)) {
            context.setEnvironment(environment);
        }
        if (clazz.isAnnotationPresent(EnableNs4j.class)) {
            EnableNs4j bean = clazz.getAnnotation(EnableNs4j.class);
            int port = bean.port();
            if (port < 1024 || port > 65535) {
                throw new ServerStartupException("服务端口 [ " + context.getPort() + " ] 出现错误");
            }
            context.setBootstrapClass(clazz);
            context.setPort(port);
            context.setServerName(bean.serverName());
            context.setServerPath(bean.serverPath());
            String[] includes = bean.includes();
            if (includes != null && includes.length > 0) {
                context.setHandlerPkg(bean.includes());
            } else {
                context.setHandlerPkg(new String[]{clazz.getPackage().getName()});
            }
            context.setPrintLog(bean.printLog());
            context.setOpenGzip(bean.openGzip());
            context.setOpenTraffic(bean.openTraffic());
        }
    }

}
