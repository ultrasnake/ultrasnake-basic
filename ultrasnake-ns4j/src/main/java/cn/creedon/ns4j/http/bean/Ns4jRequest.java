package cn.creedon.ns4j.http.bean;

import cn.creedon.ns4j.context.StandardContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：请求实体
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Data
public final class Ns4jRequest {

    private String traceId;

    private StandardContext context;

    private HttpRequest httpRequest;

    private Object[] methodParams;

    private String body;

    private Map<String, Object> attributes = new LinkedHashMap<>(16);

    private Map<String, Object> parameters = new LinkedHashMap<>(16);

}
