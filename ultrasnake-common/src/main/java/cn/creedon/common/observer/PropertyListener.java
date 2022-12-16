package cn.creedon.common.observer;

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
public interface PropertyListener<T> {

    /**
     * 是否同步处理，默认为true，，为false时，要考虑跨线程处理上下文数据
     *
     * @return
     */
    boolean isSync();

    /**
     * 配置变更
     *
     * @param value
     */
    void configChange(T value);

    /**
     * 配置删除
     *
     * @param value
     */
    void configRemove(T value);

}