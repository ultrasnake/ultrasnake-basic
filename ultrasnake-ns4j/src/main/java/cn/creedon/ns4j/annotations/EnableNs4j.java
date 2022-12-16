package cn.creedon.ns4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：EnableNs4j 核心启动注解
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableNs4j {

    /**
     * 端口
     *
     * @return
     */
    int port() default 8080;

    /**
     * 服务名称
     *
     * @return
     */
    String serverName();

    /**
     * 根路径
     *
     * @return
     */
    String serverPath();

    /**
     * 包扫描列表
     *
     * @return
     */
    String[] includes() default {};

    /**
     * 日志相关参数
     *
     * @return
     */
    boolean printLog() default true;

    /**
     * 是否开启Gzip请求压缩
     *
     * @return
     */
    boolean openGzip() default true;

    /**
     * 是否开启单机流控
     *
     * @return
     */
    boolean openTraffic() default false;
}
