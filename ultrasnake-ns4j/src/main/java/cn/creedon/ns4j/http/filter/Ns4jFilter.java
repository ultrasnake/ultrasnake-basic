package cn.creedon.ns4j.http.filter;

import cn.creedon.ns4j.http.bean.Ns4jRequest;
import cn.creedon.ns4j.http.bean.Ns4jResponse;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：Ns4jFilter过滤器
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public interface Ns4jFilter {

    boolean doFilter(Ns4jRequest request, Ns4jResponse response);

    default int getOrder() {
        return Integer.MAX_VALUE;
    }

}
