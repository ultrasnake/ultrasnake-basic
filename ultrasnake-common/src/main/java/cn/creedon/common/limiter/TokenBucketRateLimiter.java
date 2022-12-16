package cn.creedon.common.limiter;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：令牌桶限流
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class TokenBucketRateLimiter implements IRateLimiter {

    private static final long NANO_SECOND = 1000000000;

    private final double permitsPerSecond;
    private double tokenBucket;
    private final double nanoSecondsPreToken;

    private long timeBeginToCreateToken;

    public TokenBucketRateLimiter(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.tokenBucket = permitsPerSecond;
        this.nanoSecondsPreToken = NANO_SECOND / permitsPerSecond;
        this.timeBeginToCreateToken = System.nanoTime();
    }

    public static TokenBucketRateLimiter create(double permitsPerSecond) {
        return new TokenBucketRateLimiter(permitsPerSecond);
    }

    @Override
    public boolean tryAcquire() {
        synchronized (this) {
            long now = System.nanoTime();
            double newToken = (now - timeBeginToCreateToken) / nanoSecondsPreToken;
            tokenBucket = Math.min(permitsPerSecond, newToken + tokenBucket);
            timeBeginToCreateToken = now;
            if (tokenBucket > 0) {
                tokenBucket -= 1;
                return true;
            }
        }
        return false;
    }

}