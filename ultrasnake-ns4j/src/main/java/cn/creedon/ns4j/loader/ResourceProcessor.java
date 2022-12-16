package cn.creedon.ns4j.loader;

import cn.creedon.common.utils.PackageScanner;
import cn.creedon.ns4j.annotations.Ns4jInject;
import cn.creedon.ns4j.annotations.Ns4jModule;
import cn.creedon.ns4j.annotations.Ns4jURI;
import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.container.ContainerManager;
import cn.creedon.ns4j.container.Ns4jDefaultContainer;
import cn.creedon.ns4j.exceptions.ResourceLoadException;
import cn.creedon.ns4j.http.enums.RequestMethod;
import cn.creedon.ns4j.http.filter.FilterChain;
import cn.creedon.ns4j.http.filter.Ns4jFilter;
import cn.creedon.ns4j.context.notice.Ns4jNotice;
import cn.creedon.ns4j.model.HandlerHttpMeta;
import cn.creedon.ns4j.proxy.MethodProxyHandler;
import cn.creedon.ns4j.traffic.TrafficManager;
import cn.creedon.ns4j.utils.UltraAsmHelper;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.creedon.ns4j.boot.Ns4jConstants.CHARACTER_BLANK;
import static cn.creedon.ns4j.boot.Ns4jConstants.CHARACTER_SLASH;

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
public enum ResourceProcessor {

    INSTANCE;

    /**
     * 默认的容器对象
     */
    private static final Ns4jDefaultContainer CONTAINER = new Ns4jDefaultContainer();

    /**
     * 通知类列表 - 启动成功通知
     */
    private static final List<Class<?>> NOTICES = new ArrayList<>();

    /**
     * 过滤器列表 - 服务请求过滤
     */
    private static final FilterChain FILTER_CHAIN = new FilterChain();

    /**
     * 请求处理的元信息集合
     */
    private static final Map<String, HandlerHttpMeta> HANDLER_META_MAP = new ConcurrentHashMap<>();

    /**
     * 资源初始化加载
     * 说明：
     * 1、核心：ContextWrapper加入到容器列表之中
     * 2、核心：加载资源
     * 3、核心：加载SPI扩展
     * 4、核心：@Ns4jInject 容器对象注入属性（属性来自于容器）
     * 5、扩展：流控初始化
     */
    public final void load() {
        final StandardContext standardContext = StandardContext.getCurrentContext();
        CONTAINER.put(StandardContext.class, standardContext);
        Set<Class<?>> classes = scanPackageClasses(standardContext.getHandlerPkg());
        for (Class<?> clazz : classes) {
            loadResources(clazz);
        }
        ContainerManager.INSTANCE.boot(CONTAINER);
        ExtensionManager.INSTANCE.boot(classes);
        List<Class<?>> list = CONTAINER.getList();
        for (Class<?> aClass : list) {
            try {
                injectFields(aClass);
            } catch (Exception e) {
                throw new ResourceLoadException("注入属性出现异常", e);
            }
        }
        if (standardContext.isOpenTraffic()) {
            TrafficManager.INSTANCE.initTraffic(standardContext);
        }
    }

    /**
     * 扫描包下所有需要处理的类
     *
     * @param packs 包路径数组
     * @return
     */
    private Set<Class<?>> scanPackageClasses(String[] packs) {
        Set<Class<?>> classes = new HashSet<>();
        if (packs != null) {
            for (String packName : packs) {
                classes.addAll(PackageScanner.scan(packName));
            }
        }
        return classes;
    }

    /**
     * 加载资源
     *
     * @param clazz 需要处理的类
     */
    private void loadResources(Class<?> clazz) {
        if (Modifier.isInterface(clazz.getModifiers())) {
            return;
        }
        Ns4jModule moduleHandler = clazz.getAnnotation(Ns4jModule.class);
        if (moduleHandler != null) {
            CONTAINER.add(clazz);
        }
        if (Ns4jNotice.class.isAssignableFrom(clazz) && !Ns4jNotice.class.equals(clazz)) {
            NOTICES.add(clazz);
        }
        if (Ns4jFilter.class.isAssignableFrom(clazz) && !Ns4jFilter.class.equals(clazz)) {
            FILTER_CHAIN.addFilter(clazz);
        }
        Ns4jURI uriClassHandler = clazz.getAnnotation(Ns4jURI.class);
        if (uriClassHandler != null) {
            String classAlias = uriClassHandler.value().replace(CHARACTER_SLASH, CHARACTER_BLANK);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Ns4jURI apiMethodHandler = method.getAnnotation(Ns4jURI.class);
                if (apiMethodHandler == null) {
                    continue;
                }
                String methodAlias = apiMethodHandler.value().replace(CHARACTER_SLASH, CHARACTER_BLANK);
                RequestMethod[] requestMethod = apiMethodHandler.method();
                String[] params = UltraAsmHelper.getMethodParamNames(method);
                HandlerHttpMeta handlerHttpMeta = new HandlerHttpMeta();
                handlerHttpMeta.setClazz(clazz);
                handlerHttpMeta.setMethod(method);
                handlerHttpMeta.setParams(params);
                handlerHttpMeta.setRequestMethod(requestMethod);
                HANDLER_META_MAP.put(classAlias + methodAlias, handlerHttpMeta);
            }
        }
    }

    /**
     * 注入属性值
     * System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./code");
     */
    private void injectFields(Class<?> clazz) throws Exception {
        Object clazzObject = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            Ns4jInject fieldInject = field.getAnnotation(Ns4jInject.class);
            if (fieldInject != null) {
                Class<?> fieldType = field.getType();
                boolean enhancerFlag = fieldInject.enhancer();
                Object fieldBean = CONTAINER.get().get(fieldType);
                if (enhancerFlag) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(fieldType);
                    enhancer.setCallback(new MethodProxyHandler(fieldBean));
                    fieldBean = enhancer.create();
                }
                if (fieldBean != null) {
                    field.setAccessible(true);
                    field.set(clazzObject, fieldBean);
                }
            }
        }
        CONTAINER.put(clazz, clazzObject);
    }

    public final List<Class<?>> notices() {
        return NOTICES;
    }

    public final Ns4jDefaultContainer defaultContainer() {
        return CONTAINER;
    }

    public final FilterChain filterChain() {
        return FILTER_CHAIN;
    }

    public final HandlerHttpMeta getHandlerHttpMeta(String handlerName) {
        if (handlerName == null || "".equals(handlerName)) {
            return null;
        }
        return HANDLER_META_MAP.get(handlerName);
    }

}
