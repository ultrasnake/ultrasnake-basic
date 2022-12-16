package cn.creedon.ns4j.traffic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficStore {

    /**
     * IP黑名单列表
     */
    private Set<String> blacklistRules;

    /**
     * IP白名单列表
     */
    private Set<String> whitelistRules;

    /**
     * 流控列表
     */
    private Set<TrafficBucket> trafficBuckets;

}
