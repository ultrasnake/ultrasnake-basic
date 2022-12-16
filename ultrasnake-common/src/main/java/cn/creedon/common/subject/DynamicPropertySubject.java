package cn.creedon.common.subject;

import cn.creedon.common.observer.PropertyListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：具体主题
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
@Slf4j
public class DynamicPropertySubject<T> implements PropertySubject<T> {

    protected Set<PropertyListener<T>> listeners = Collections.synchronizedSet(new HashSet<>());

    private T value = null;

    public DynamicPropertySubject() {
    }

    public DynamicPropertySubject(T value) {
        super();
        this.value = value;
    }

    @Override
    public void addListener(PropertyListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(PropertyListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean configChange(T newValue) {
        if (isEqual(value, newValue)) {
            return false;
        }
        log.info("配置更新: " + newValue);
        value = newValue;
        for (PropertyListener<T> listener : listeners) {
            if (listener.isSync()) {
                listener.configChange(newValue);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listener.configChange(newValue);
                    }
                }).start();
            }
        }
        return true;
    }

    @Override
    public boolean configRemove(T newValue) {
        log.info("删除配置: " + newValue);
        for (PropertyListener<T> listener : listeners) {
            if (listener.isSync()) {
                listener.configRemove(newValue);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listener.configRemove(newValue);
                    }
                }).start();
            }
        }
        return true;
    }

    private boolean isEqual(T oldValue, T newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }

    public void close() {
        listeners.clear();
    }
}
