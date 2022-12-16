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
public class ServerStartupException extends RuntimeException {

    public ServerStartupException() {
        super();
    }

    public ServerStartupException(String message) {
        super(message);
    }

    public ServerStartupException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerStartupException(Throwable cause) {
        super(cause);
    }

}
