package cn.creedon.ns4j.exceptions;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：服务启动异常
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class ContainerBeanException extends RuntimeException {

    public ContainerBeanException() {
        super();
    }

    public ContainerBeanException(String message) {
        super(message);
    }

    public ContainerBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerBeanException(Throwable cause) {
        super(cause);
    }

}
