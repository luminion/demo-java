package dataStructure.algorithm;

/**
 * 简答的令牌桶算法
 * @author luminion
 * @since 1.0.0
 */
public class TokenBucketRateLimiter {
    // 桶的最大容量（防止积累过多的令牌，应对突发流量）
    private final long capacity;
    // 每毫秒生成的令牌数 (比如 10/s，就是 0.01/ms)
    private final double refillRatePerMs;

    // 当前桶里还剩下多少令牌 (用 double 是为了保留小数精度)
    private double currentTokens;
    // 上一次计算令牌的时间戳
    private long lastUpdateTime;

    /**
     * @param capacity 桶的容量（突发并发上限）
     * @param ratePerSec 匀速每秒允许通过的请求数
     */
    public TokenBucketRateLimiter(long capacity, double ratePerSec) {
        this.capacity = capacity;
        this.refillRatePerMs = ratePerSec / 1000.0;
        this.currentTokens = capacity; // 初始状态桶是满的
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * 获取令牌
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();

        // 1. 计算这段时间 [应该] 生成多少令牌
        double generatedTokens = (now - lastUpdateTime) * refillRatePerMs;

        // 2. 更新当前令牌数（不能超过桶的容量）
        currentTokens = Math.min(capacity, currentTokens + generatedTokens);

        // 3. 更新上次时间戳
        lastUpdateTime = now;

        // 4. 判断是否能放行
        if (currentTokens >= 1.0) {
            currentTokens -= 1.0; // 消耗一个令牌
            return true;          // 放行请求
        }

        return false; // 令牌不够了，拒绝请求
    }

    // --- 测试代码 ---
    public static void main(String[] args) throws InterruptedException {
        // 容量为 5，每秒生成 2 个令牌
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 2);

        // 瞬间发起 7 次请求
        for (int i = 1; i <= 7; i++) {
            System.out.println("第 " + i + " 次请求：" + (limiter.tryAcquire() ? "✅通过" : "❌限流"));
        }

        System.out.println("睡 2 秒等令牌恢复...");
        Thread.sleep(2000);
        System.out.println("第 8 次请求：" + (limiter.tryAcquire() ? "✅通过" : "❌限流"));
    }
}