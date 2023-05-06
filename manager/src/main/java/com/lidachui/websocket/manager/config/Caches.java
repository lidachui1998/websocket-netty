package com.lidachui.websocket.manager.config;

import com.lidachui.websocket.common.util.RedisUtils;
import javax.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Caches
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 23:55
 */
@Component
public final class Caches implements InitializingBean {

    private static Caches caches = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        caches = this;
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisUtils redisUtils;

    public static <T> void set(String key, T value) {
        caches.redisUtils.set(key, value);
    }

    public static <T> void set(String key, T value, long expireSeconds) {
        caches.redisUtils.set(key, value, expireSeconds);
    }

    public static <T> void replaceValue(String key, T value) {
        caches.redisUtils.set(key, value, getExpire(key));
    }

    public static <T> T get(String key, Class<T> clazz) {
        return clazz.cast(caches.redisUtils.get(key));
    }

    public static void delete(String key) {
        caches.redisUtils.remove(key);
    }

    public static long getExpire(String key) {
        return caches.redisTemplate.opsForValue().getOperations().getExpire(key);
    }

    public static boolean exists(String key) {
        return caches.redisTemplate.opsForValue().getOperations().hasKey(key);
    }

    public static boolean tryLock(final String lockKey, final String lockValue, final Long milliSecondTime) {
        return caches.redisUtils.tryLock(lockKey, lockValue, milliSecondTime);
    }

    public static void releaseLock(final String lockKey, final String lockValue) {
        caches.redisUtils.releaseLock(lockKey, lockValue);
    }

    public static void removeBlear(String key) {
        caches.redisUtils.removeBlear(key);
    }

}
