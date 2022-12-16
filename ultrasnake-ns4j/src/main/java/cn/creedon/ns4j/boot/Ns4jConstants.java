package cn.creedon.ns4j.boot;

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
public class Ns4jConstants {

    public static final Integer MAX_CONTENT_LENGTH = 64 * 1024 * 1024;

    public static final String CONTENT_TYPE_JSON = "text/json; charset=UTF-8";
    public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    public static final String CHARACTER_SLASH = "/";
    public static final String CHARACTER_BLANK = "";

    public static final String REQUEST_HTTP_PING = "/ping";
    public static final String REQUEST_METHOD_OPTIONS = "OPTIONS";
    public static final String REQUEST_STATIC_ICO_FAVICON = "/favicon.ico";
    public static final String REQUEST_STATIC_ROOT = "/";

    private Ns4jConstants() {
        throw new IllegalStateException("不支持实例化");
    }

}
