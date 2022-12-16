package cn.creedon.ns4j.container;

import cn.creedon.ns4j.context.container.Ns4jContainer;
import cn.creedon.ns4j.exceptions.ContainerBeanException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public final class Ns4jDefaultContainer implements Ns4jContainer {

    private static final Map<Class<?>, Object> CONTAINER_MAP = new ConcurrentHashMap<>();
    private static final List<Class<?>> CONTAINER_LIST = new ArrayList<>();

    @Override
    public Object getBean(String beanName) throws ContainerBeanException {
        if (beanName == null || "".equals(beanName)) {
            return null;
        }
        try {
            Class<?> aClass = Class.forName(beanName);
            Object o = this.get().get(aClass);
            if (o != null) {
                return o;
            }
        } catch (Exception e) {
            throw new ContainerBeanException(e);
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws ContainerBeanException {
        return (T) this.get().get(clazz);
    }

    @Override
    public String[] getBeanNames() {
        String[] beanNames = new String[getBeanCount()];
        int i = 0;
        for (Map.Entry<Class<?>, Object> entry : this.get().entrySet()) {
            beanNames[i++] = entry.getKey().getCanonicalName();
        }
        return beanNames;
    }

    @Override
    public int getBeanCount() {
        return CONTAINER_MAP.size();
    }

    @Override
    public void put(Class<?> clazz, Object object) {
        CONTAINER_MAP.put(clazz, object);
    }

    @Override
    public Map<Class<?>, Object> get() {
        return CONTAINER_MAP;
    }

    @Override
    public void add(Class<?> clazz) {
        CONTAINER_LIST.add(clazz);
    }

    @Override
    public List<Class<?>> getList() {
        return CONTAINER_LIST;
    }

}
