package cn.creedon.ns4j.http.bean;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：重定向实体
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public final class Ns4jRedirect {

    private final String redirect;

    private Ns4jRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getRedirect() {
        return redirect;
    }

    public static Ns4jRedirect to(String redirect) {
        return new Ns4jRedirect(redirect);
    }
}
