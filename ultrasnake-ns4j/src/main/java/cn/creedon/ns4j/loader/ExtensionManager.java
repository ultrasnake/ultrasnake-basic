package cn.creedon.ns4j.loader;

import cn.creedon.ns4j.context.StandardContext;
import cn.creedon.ns4j.context.loader.Ns4jExtension;
import cn.creedon.ns4j.exceptions.ResourceLoadException;

import java.util.*;

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
public enum ExtensionManager {

    INSTANCE;

    /**
     * 用于存储SPI加载的所有{@link Ns4jExtension} 集合
     */
    private static Map<Class<?>, Ns4jExtension> EXTENSION_LOADER_SERVICES = Collections.emptyMap();

    public void boot(Set<Class<?>> classes) {
        final StandardContext currentContext = StandardContext.getCurrentContext();
        EXTENSION_LOADER_SERVICES = loadAllExtensionLoader();
        for (Ns4jExtension loader : EXTENSION_LOADER_SERVICES.values()) {
            try {
                loader.load(currentContext.getEnvironment(), currentContext.getBootstrapClass(), classes);
            } catch (Throwable e) {
                throw new ResourceLoadException("外部资源加载异常", e);
            }
        }
    }

    private Map<Class<?>, Ns4jExtension> loadAllExtensionLoader() {
        Map<Class<?>, Ns4jExtension> bootServices = new LinkedHashMap<>();
        final List<Ns4jExtension> allServices = loadSpiService();
        for (Ns4jExtension bootService : allServices) {
            Class<? extends Ns4jExtension> extensionLoaderServiceClass = bootService.getClass();
            if (bootServices.containsKey(extensionLoaderServiceClass)) {
                continue;
            }
            bootServices.put(extensionLoaderServiceClass, bootService);
        }
        return bootServices;
    }

    private List<Ns4jExtension> loadSpiService() {
        List<Ns4jExtension> allServices = new LinkedList<>();
        for (Ns4jExtension ns4jExtension : ServiceLoader.load(Ns4jExtension.class)) {
            allServices.add(ns4jExtension);
        }
        return allServices;
    }

    public Map<Class<?>, Ns4jExtension> get() {
        return EXTENSION_LOADER_SERVICES;
    }

}
