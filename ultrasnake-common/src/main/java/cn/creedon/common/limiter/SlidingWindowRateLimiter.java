package cn.creedon.common.limiter;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：滑动窗口限流
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class SlidingWindowRateLimiter implements IRateLimiter {

    private final long permitsPerSecond;

    private final TreeMap<Long, Integer> counters;

    public SlidingWindowRateLimiter(long permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.counters = new TreeMap<Long, Integer>();
    }

    public static SlidingWindowRateLimiter create(long permitsPerSecond) {
        return new SlidingWindowRateLimiter(permitsPerSecond);
    }

    @Override
    public boolean tryAcquire() {
        synchronized (this) {
            long currentWindowTime = System.currentTimeMillis() / 1000 * 1000;
            int currentWindowCount = getCurrentWindowCount(currentWindowTime);
            if (currentWindowCount >= permitsPerSecond) {
                return false;
            }
            counters.merge(currentWindowTime, 1, Integer::sum);
            return true;
        }
    }

    private int getCurrentWindowCount(long currentWindowTime) {
        long startTime = currentWindowTime - 900;
        int result = 0;
        Iterator<Map.Entry<Long, Integer>> iterator = counters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            if (entry.getKey() < startTime) {
                iterator.remove();
            } else {
                result += entry.getValue();
            }
        }
        return result;
    }

}
