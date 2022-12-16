package cn.creedon.ns4j.container;

import cn.creedon.ns4j.context.container.Ns4jContainer;
import cn.creedon.ns4j.context.container.Ns4jContainerAware;
import cn.creedon.ns4j.exceptions.ResourceLoadException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
public enum ContainerManager {

    INSTANCE;

    /**
     * 用于存储SPI加载的所有{@link Ns4jContainerAware} 集合
     */
    private static Map<Class<?>, Ns4jContainerAware> CONTAINER_AWARE_SERVICES = new ConcurrentHashMap<>();

    public void boot(Ns4jContainer ns4jContainer) {
        CONTAINER_AWARE_SERVICES = loadAllExtensionLoader();
        for (Ns4jContainerAware aware : CONTAINER_AWARE_SERVICES.values()) {
            try {
                aware.setNs4jContainer(ns4jContainer);
            } catch (Throwable e) {
                throw new ResourceLoadException("系统容器通知异常", e);
            }
        }
    }

    private Map<Class<?>, Ns4jContainerAware> loadAllExtensionLoader() {
        Map<Class<?>, Ns4jContainerAware> bootServices = new LinkedHashMap<>();
        final List<Ns4jContainerAware> allServices = loadSpiService();
        for (Ns4jContainerAware bootService : allServices) {
            Class<? extends Ns4jContainerAware> containerAwareClass = bootService.getClass();
            if (bootServices.containsKey(containerAwareClass)) {
                continue;
            }
            bootServices.put(containerAwareClass, bootService);
        }
        return bootServices;
    }

    private List<Ns4jContainerAware> loadSpiService() {
        List<Ns4jContainerAware> allServices = new LinkedList<>();
        for (Ns4jContainerAware containerAware : ServiceLoader.load(Ns4jContainerAware.class)) {
            allServices.add(containerAware);
        }
        return allServices;
    }

    public Map<Class<?>, Ns4jContainerAware> get() {
        return CONTAINER_AWARE_SERVICES;
    }

}
