package cn.creedon.common.observer;

import lombok.extern.slf4j.Slf4j;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：具体观察的简单实现
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Slf4j
public abstract class AbstractPropertyListener<T> implements PropertyListener<T> {

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void configChange(T value) {
        if (log.isDebugEnabled()) {
            log.debug("rules change: " + value);
        }
    }

    @Override
    public void configRemove(T value) {
        if (log.isDebugEnabled()) {
            log.debug("rules remove: " + value);
        }
    }

}
