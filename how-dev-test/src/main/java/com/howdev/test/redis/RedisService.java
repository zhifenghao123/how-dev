package com.howdev.test.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * RedisService class
 *
 * @author haozhifeng
 * @date 2024/02/28
 */
@Slf4j
@Service
public class RedisService {
    /**
     * Redis锁删除的lua脚本（使用lua脚本释放redis锁）
     * 如果key存在并且等于特殊的锁值 则删除key 即释放锁
     */
    private static final String REDIS_UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取分布式锁
     * （1）加锁原理
     *      1）加锁实际上就是给 Key 设置一个 Value
     *      2）key 不存在时，返回1；存在则返回 0
     *      3）为了避免死锁，会设置一个过期时间
     * （2）setnx 命令
     *      1）setnx是指对set命令加上nx参数
     *      nx表示 key 不存在时，才会创建键值对
     *      如果setnx命令执行成功，则表示获取到了锁
     *      2）Redis2.6 提供 LUA 脚本，它可以保证加锁和设置过期时间的操作是原子性的
     * （3）set参数。set(key, value, NX, PX)
     *      key 是锁的名字
     *      value 通常是客户端生成的唯一的字符串
     *      NX 表示键不存在时，才创建值
     *      PX 设置键的过期时间
     * @param key
     * @param token
     * @param expireInSeconds 锁超时时间
     * @return
     */
    public boolean lock(String key, String token, long expireInSeconds) {
        // 内部使用redis SET(NX, PX) 命令.
        Boolean res = redisTemplate.opsForValue().setIfAbsent(key, token, expireInSeconds, TimeUnit.SECONDS);
        return Objects.equals(res, true);
    }

    /**
     * 尝试获取锁，如果获取不到，则重试指定次数，直到获取成功或者到达制定的重试次数
     * @param key
     * @param token
     * @param expireInSeconds
     * @param retryTimes
     * @return
     */
    public boolean tryLockWithRetryTimes(String key, String token, long expireInSeconds, int retryTimes) {
        for (int i = 1; i <= retryTimes; i++) {
            Boolean res = lock(key, token, expireInSeconds);
            if (Boolean.TRUE.equals(res)) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("thread sleep error", e);
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    /**
     * 尝试获取锁，如果获取不到，则在指定的时间内重试多次，直到获取成功或者到达制定的重试时间
     *
     * @param key key
 * @param token token
 * @param expireInSeconds expireInSeconds
 * @param acquireTimeout acquireTimeout
     * @return:
     * @author: haozhifeng
     */
    public boolean tryLockWithAcquireTimeOut(String key, String token, long expireInSeconds, long acquireTimeout) {
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                Boolean res = lock(key, token, expireInSeconds);
                if (Boolean.TRUE.equals(res)) {
                    return true;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("thread sleep error", e);
                    Thread.currentThread().interrupt();
                }
            }
        return false;
    }

    /**
     * 分布式锁unlock，使用lua脚本保证事务
     * 解锁的过程就是将 Key 键删除，但也不能乱删，不能说客户端 1 的请求将客户端 2 的锁给删除掉。
     * 首先判断当前锁的 value 是否与传入的值相等，若相等，则删除 Key，解锁成功
     * 为了保证解锁操作的原子性，我们也用 LUA 脚本完成这一操作
     * """
     * -- 当前锁的字符串是否与传入的值相等
     * if redis.call('get', KEYS[1]) == ARGV[1]
     *     then
     *     -- 执行删除操作
     *         return redis.call('del', KEYS[1])
     *     else
     *     -- 不成功，返回0
     *         return 0
     * end
     * """
     *
     * @param key
     * @param token lock时的token值，只有token一致才能解锁
     * @return
     */
    public boolean unlock(String key, String token) {
        Long res = null;
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(REDIS_UNLOCK_SCRIPT, Long.class);
            res = redisTemplate.execute(redisScript, Collections.singletonList(key), token);
        } catch (Exception e) {
            log.error("exception occurred by redis unlock with eval lua script:key=[{}],token=[{}]", key, token, e);
        }

        // null means lua return nil (the lock is released or be hold by the other request)
        if (!Objects.equals(res, 1L)) {
            log.warn("redis unlock failed:key=[{}],token=[{}],res=[{}]", key, token, res);
            return false;
        }
        return true;
    }
}
