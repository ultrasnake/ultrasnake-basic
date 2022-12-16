package cn.creedon.ns4j.context.container;

import java.util.List;
import java.util.Map;

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
public interface Ns4jContainer {

    Object getBean(String beanName);

    <T> T getBean(Class<T> clazz);

    String[] getBeanNames();

    int getBeanCount();

    void add(Class<?> clazz);

    List<Class<?>> getList();

    void put(Class<?> clazz, Object object);

    Map<Class<?>, Object> get();

}
