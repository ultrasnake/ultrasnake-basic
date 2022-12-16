package cn.creedon.ns4j.http.filter;

import cn.creedon.ns4j.http.bean.Ns4jRequest;
import cn.creedon.ns4j.http.bean.Ns4jResponse;
import cn.creedon.ns4j.model.Ns4jRR;

import static cn.creedon.ns4j.boot.Ns4jConstants.*;

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
public class Ns4jRequestFilter implements Ns4jFilter {

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean doFilter(Ns4jRequest request, Ns4jResponse response) {
        String uri = request.getHttpRequest().uri();
        String httpRequestMethodName = request.getHttpRequest().method().name();
        if (REQUEST_HTTP_PING.equals(uri) || REQUEST_METHOD_OPTIONS.equals(httpRequestMethodName)) {
            response.setWrite(Ns4jRR.ok());
            return false;
        } else if (REQUEST_STATIC_ICO_FAVICON.equals(uri)) {
            response.setWrite("Bad Request [uri is favicon.ico]");
            return false;
        } else if (REQUEST_STATIC_ROOT.equals(uri)) {
            response.setWrite("Bad Request [uri is /]");
            return false;
        }
        return true;
    }
}
