package cn.creedon.ns4j.http;

import cn.creedon.ns4j.boot.Ns4jConstants;
import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.container.Ns4jDefaultContainer;
import cn.creedon.ns4j.http.filter.FilterChain;
import cn.creedon.ns4j.http.bean.Ns4jRedirect;
import cn.creedon.ns4j.http.bean.Ns4jRequest;
import cn.creedon.ns4j.http.bean.Ns4jResponse;
import cn.creedon.ns4j.http.multipart.MultipartFile;
import cn.creedon.ns4j.http.multipart.MultipartHttpRequest;
import cn.creedon.ns4j.loader.ResourceProcessor;
import cn.creedon.ns4j.model.HandlerHttpMeta;
import cn.creedon.ns4j.model.Ns4jRR;
import cn.creedon.ns4j.traffic.TrafficManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

import static cn.creedon.ns4j.boot.Ns4jConstants.CONTENT_TYPE_HTML;
import static cn.creedon.ns4j.boot.Ns4jConstants.CONTENT_TYPE_JSON;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

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
@Slf4j
public class Ns4jHttpHandler {

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    public static Object process(HttpRequest request, LastHttpContent lastHttpContent, ChannelHandlerContext ctx, StandardContext standardContext) {
        long startTime = System.currentTimeMillis();
        try {
            // 判断流控
            if (executeTraffic(request, lastHttpContent, ctx, standardContext)) {
                return null;
            }
            final String reqId = UUID.randomUUID().toString().replace("-", "");
            final Ns4jRequest ns4jRequest = new Ns4jRequest();
            final Ns4jResponse ns4jResponse = new Ns4jResponse();
            ns4jRequest.setContext(standardContext);
            ns4jRequest.setHttpRequest(request);
            ns4jRequest.setTraceId(reqId);
            // 执行过滤器
            if (executeFilter(request, lastHttpContent, ctx, ns4jRequest, ns4jResponse)) {
                return null;
            }
            HandlerHttpMeta handlerHttpMeta = ResourceProcessor.INSTANCE.getHandlerHttpMeta(getHandlerName(request));
            if (handlerHttpMeta == null) {
                String resultJson = JSON.toJSONString(Ns4jRR.error400("Bad Request"));
                writeResponse(request, lastHttpContent, resultJson, ctx, CONTENT_TYPE_JSON);
                return null;
            }
            Ns4jDefaultContainer container = ResourceProcessor.INSTANCE.defaultContainer();
            Object handlerObj = container.getBean(handlerHttpMeta.getClazz());
            String[] params = handlerHttpMeta.getParams();
            Method method = handlerHttpMeta.getMethod();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            List<String> contentTypes = request.headers().getAll(HttpHeaderNames.CONTENT_TYPE);
            String contentType = null;
            if (contentTypes != null && contentTypes.size() > 0) {
                contentType = contentTypes.get(0);
            }
            if (contentType != null && contentType.contains("application/json")) {
                ns4jRequest.setBody(getHttpContent(lastHttpContent));
                Object fromObj = null;
                if (args.length > 0) {
                    try {
                        fromObj = JSON.parseObject(ns4jRequest.getBody(), parameterTypes[0]);
                    } catch (Exception e) {
                        throw new RuntimeException("json to java bean error." + ns4jRequest.getBody(), e);
                    }
                    if (fromObj != null) {
                        args[0] = fromObj;
                    }
                }
            } else if (contentType != null && contentType.contains("multipart/form-data") && request.method().equals(HttpMethod.POST)) {
                ns4jRequest.setBody(null);
                MultipartHttpRequest multipart = null;
                multipart = new MultipartHttpRequest();
                multipart.setRootPath(standardContext.getServerPath());
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
                while (decoder.hasNext()) {
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        writeHttpData(multipart, data);
                    }
                }
                if (args.length == 0) {
                    throw new RuntimeException("upload file error parameter.");
                }
                if (multipart.getMultipartFiles() != null) {
                    Class<?> parameterType = parameterTypes[0];
                    if (parameterType == MultipartHttpRequest.class) {
                        args[0] = multipart;
                    }
                }
            } else {
                String body = JSON.toJSONString(getParamsMaps(request, lastHttpContent));
                ns4jRequest.setBody(body);
                if (args.length > 0) {
                    Map<?, ?> queryMap = null;
                    try {
                        String content = ns4jRequest.getBody();
                        if (content != null && !"".equals(content)) {
                            queryMap = JSON.parseObject(
                                    content, new TypeReference<Map<Object, Object>>() {
                                    }, Feature.OrderedField
                            );
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("json to java map error." + ns4jRequest.getBody(), e);
                    }
                    if (queryMap != null && queryMap.size() > 0) {
                        for (int i = 0; i < params.length; i++) {
                            String param = (String) queryMap.get(params[i]);
                            args[i] = getRealVal(parameterTypes[i], param);
                        }
                    }
                }
            }
            ns4jRequest.setMethodParams(args);
            if (standardContext.isPrintLog()) {
                // 请求日志
                log.info("MODS: " + reqId + "; uri: " + request.uri() + "; params: " + ns4jRequest.getBody());
            }
            Object toObject;
            if (ns4jResponse.getWrite() != null) {
                toObject = ns4jResponse.getWrite();
            } else {
                if (args.length > 0) {
                    toObject = method.invoke(handlerObj, args);
                } else {
                    toObject = method.invoke(handlerObj);
                }
            }
            String resultJson = null;
            if (toObject instanceof Ns4jRedirect) {
                Ns4jRedirect ns4jRedirectObj = (Ns4jRedirect) toObject;
                String redirect = ns4jRedirectObj.getRedirect();
                if (redirect != null
                        && !"".equals(redirect)
                        && redirect.startsWith("http")) {
                    sendRedirect(ctx, redirect);
                    return null;
                }
            } else if (toObject instanceof String) {
                contentType = CONTENT_TYPE_HTML;
                resultJson = (String) toObject;
            } else {
                contentType = CONTENT_TYPE_JSON;
                resultJson = JSON.toJSONString(toObject);
            }
            if (standardContext.isPrintLog()) {
                long time = System.currentTimeMillis() - startTime;
                // 响应日志
                log.info("MODS: " + reqId + "; uri: " + request.uri() + "; result: " + resultJson + "; cost: " + time + " ms.");
                // 慢日志
                if (time > 500) {
                    log.info("SLOW: " + reqId);
                }
            }
            writeResponse(request, lastHttpContent, resultJson, ctx, contentType);
        } catch (Exception e) {
            log.error("", e);
            return e;
        }
        return null;
    }

    /**
     * 执行流控
     *
     * @param request
     * @param lastHttpContent
     * @param ctx
     * @param standardContext
     * @return
     */
    private static boolean executeTraffic(HttpRequest request, LastHttpContent lastHttpContent, ChannelHandlerContext ctx, StandardContext standardContext) {
        String ip = getIp(request, ctx);
        String uri = request.uri();
        if (!TrafficManager.INSTANCE.verifyTraffic(ip, uri)) {
            String resultJson = JSON.toJSONString(Ns4jRR.ok("触发流控"));
            if (standardContext.isPrintLog()) {
                log.info("FLOW: >>> " + (ip + "_" + uri) + "; result: " + resultJson);
            }
            writeResponse(request, lastHttpContent, resultJson, ctx, CONTENT_TYPE_JSON);
            return true;
        }
        return false;
    }

    /**
     * 执行过滤器
     *
     * @param request
     * @param lastHttpContent
     * @param ctx
     * @param ns4jRequest
     * @param ns4jResponse
     * @return
     */
    private static boolean executeFilter(HttpRequest request, LastHttpContent lastHttpContent, ChannelHandlerContext ctx, Ns4jRequest ns4jRequest, Ns4jResponse ns4jResponse) {
        final FilterChain filterChain = ResourceProcessor.INSTANCE.filterChain();
        final boolean filterResult = filterChain.doFilter(ns4jRequest, ns4jResponse);
        if (!filterResult) {
            Object write = ns4jResponse.getWrite();
            if (write instanceof Ns4jRedirect) {
                sendRedirect(ctx, ((Ns4jRedirect) write).getRedirect());
                return true;
            }
            String resultJson;
            if (write instanceof String) {
                resultJson = JSON.toJSONString(Ns4jRR.error400(String.valueOf(ns4jResponse.getWrite())));
            } else if (write instanceof Ns4jRR) {
                resultJson = JSON.toJSONString(ns4jResponse.getWrite());
            } else {
                resultJson = JSON.toJSONString(Ns4jRR.error500());
            }
            writeResponse(request, lastHttpContent, resultJson, ctx, CONTENT_TYPE_JSON);
            return true;
        }
        return false;
    }

    /**
     * 获取真实的IP地址
     *
     * @param request
     * @param ctx
     * @return
     */
    private static String getIp(HttpRequest request, ChannelHandlerContext ctx) {
        String ip = request.headers().get("X-Forwarded-For");
        if (ip == null) {
            InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            ip = inSocket.getAddress().getHostAddress();
        }
        return ip;
    }

    public static void sendRedirect(final ChannelHandlerContext ctx, String url) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PERMANENT_REDIRECT);
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with,content-type");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        headers.set(HttpHeaderNames.LOCATION, url); //重定向URL设置
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void writeResponse(final HttpRequest request, final LastHttpContent lastHttpContent, final String resultData, final ChannelHandlerContext ctx, String contentType) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        String defaultStr = null;
        if (contentType == null) {
            contentType = CONTENT_TYPE_JSON;
        }
        if (contentType.startsWith("text/json")) {
            defaultStr = "{}";
        } else {
            defaultStr = "";
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                lastHttpContent.decoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(
                        (resultData == null || resultData.equals("null")) ? defaultStr : resultData, CharsetUtil.UTF_8
                )
        );
        response.headers().set(CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type,Content-Length, Authorization, Accept,X-Requested-With,X-File-Name");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "PUT,POST,GET,DELETE,OPTIONS");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ctx.channel().close();
                } else {
                    if (future.cause().getMessage() != null) {
                        log.error("write client error." + future.cause().getMessage());
                    }
                    ctx.channel().close();
                }
            }
        });
    }

    public static Object getRealVal(Class<?> clazz, String fromValue) {
        if (fromValue == null || "null".equals(fromValue)) {
            return null;
        } else if (clazz.equals(String.class)) {
            return fromValue;
        } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            return Integer.valueOf(fromValue);
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            return Long.valueOf(fromValue);
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            return Float.valueOf(fromValue);
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            return Double.valueOf(fromValue);
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            return Boolean.valueOf(fromValue);
        }
        return fromValue;
    }

    public static String getHandlerName(HttpRequest request) {
        final String uri = request.uri();
        String handlerName = uri;
        if (uri.contains("?")) {
            handlerName = uri.substring(0, uri.indexOf("?"));
        }
        return handlerName.replace(Ns4jConstants.CHARACTER_SLASH, Ns4jConstants.CHARACTER_BLANK);
    }

    public static Map<String, String> getParamsMaps(HttpRequest request, HttpContent lastHttpContent) {
        QueryStringDecoder queryString = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryString.parameters();
        Map<String, String> queryMap = new HashMap<String, String>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    queryMap.put(key, val);
                }
            }
        }
        String queryContent = getHttpContent(lastHttpContent);
        if (queryContent != null && !"".equals(queryContent)) {
            queryString = new QueryStringDecoder(queryContent, false);
            params = queryString.parameters();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, List<String>> p : params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        queryMap.put(key, val);
                    }
                }
            }
        }
        return queryMap;
    }

    public static String getHttpContent(HttpContent lastHttpContent) {
        String queryContent = null;
        ByteBuf bbf = lastHttpContent.content();
        if (bbf.isReadable()) {
            queryContent = bbf.toString(CharsetUtil.UTF_8);
            bbf.clear();
        }
        return queryContent;
    }

    private static void writeHttpData(MultipartHttpRequest multipart, InterfaceHttpData data) {
        //HttpDataType有三种类型 Attribute, FileUpload, InternalAttribute
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            try {
                Map<String, Object> parameters = multipart.getParameters();
                if (parameters == null) {
                    parameters = new HashMap<String, Object>();
                }
                parameters.put(attribute.getName(), attribute.getValue());
                multipart.setParameters(parameters);

            } catch (Exception e) {
                log.error("", e);
            }
        } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
            try {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        List<MultipartFile> multipartFiles = multipart.getMultipartFiles();
                        if (multipartFiles == null) {
                            multipartFiles = new ArrayList<>();
                        }
                        MultipartFile multipartFile = new MultipartFile();
                        multipartFile.setContentType(fileUpload.getContentType());
                        multipartFile.setFileName(fileUpload.getFilename());
                        multipartFile.setFileLength(fileUpload.length());
                        multipartFile.setFileByte(fileUpload.get());
                        multipartFiles.add(multipartFile);
                        multipart.setMultipartFiles(multipartFiles);
                    } else {
                        log.warn("File to be continued but should not!");
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        } else {
            log.error("HttpDataType InternalAttribute is not hander!");
        }
    }

}
