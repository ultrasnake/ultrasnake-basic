package cn.creedon.common.subject;

import cn.creedon.common.observer.PropertyListener;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：抽象主题
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public interface PropertySubject<T> {

    /**
     * 移除监听
     *
     * @param listener
     */
    void addListener(PropertyListener<T> listener);

    /**
     * 移除监听
     *
     * @param listener
     */
    void removeListener(PropertyListener<T> listener);

    /**
     * 配置变更
     *
     * @param newValue
     * @return
     */
    boolean configChange(T newValue);

    /**
     * 配置删除
     *
     * @param newValue
     * @return
     */
    boolean configRemove(T newValue);

}