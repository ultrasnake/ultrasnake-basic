package cn.creedon.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
public class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;
    private final boolean daemon;

    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-thread-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        return t;
    }
}