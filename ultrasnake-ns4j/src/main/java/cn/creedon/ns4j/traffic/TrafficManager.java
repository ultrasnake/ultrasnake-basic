package cn.creedon.ns4j.traffic;

import cn.creedon.common.limiter.SlidingWindowRateLimiter;
import cn.creedon.ns4j.context.StandardContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
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
@Slf4j
public enum TrafficManager {

    INSTANCE;

    private static final Map<String, SlidingWindowRateLimiter> USER_RULE_MAPS = new ConcurrentHashMap<>();
    private Set<String> blacklistRules = null;
    private Set<String> whitelistRules = null;
    private volatile boolean openTraffic = false;

    /**
     * 初始化流控规则
     *
     * @param standardContext 应用上下文
     */
    public void initTraffic(StandardContext standardContext) {
        openTraffic = standardContext.isOpenTraffic();
        TrafficStore trafficStore = standardContext.getTrafficStore();
        if (trafficStore != null) {
            blacklistRules = trafficStore.getBlacklistRules();
            whitelistRules = trafficStore.getWhitelistRules();
            Set<TrafficBucket> trafficBuckets = trafficStore.getTrafficBuckets();
            if (trafficBuckets != null && trafficBuckets.size() > 0) {
                for (TrafficBucket trafficBucket : trafficBuckets) {
                    if (trafficBucket.isOpenFlow()) {
                        USER_RULE_MAPS.put(
                                trafficBucket.getUri(),
                                SlidingWindowRateLimiter.create(trafficBucket.getThreshold())
                        );
                    }
                }
            }
        }
    }

    /**
     * 验证流控规则
     *
     * @param ip  IP
     * @param uri URI
     * @return true: 表示放行请求 false: 表示拦截请求
     */
    public boolean verifyTraffic(String ip, String uri) {
        if (!openTraffic) {
            return true;
        }
        if (blacklistRules != null && ip != null && !"".equals(ip)) {
            if (blacklistRules.contains(ip)) {
                return false;
            }
        }
        if (whitelistRules != null && ip != null && !"".equals(ip)) {
            if (whitelistRules.contains(ip)) {
                return true;
            }
        }
        if (uri != null) {
            SlidingWindowRateLimiter rateLimiter = USER_RULE_MAPS.get(uri);
            if (rateLimiter == null) {
                return true;
            }
            return rateLimiter.tryAcquire();
        }
        return false;
    }

}
