package com.lidachui.websocket.common.util;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * RedisUtils
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2023/4/10 23:52
 */
@Component
public class RedisUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 出异常，重复操作的次数
     */
    private static Integer times = 5;

    /**
     * 用户排序通过注册时间的 权重值
     *
     * @param date
     * @return
     */
    public double getCreateTimeScore(long date) {
        return date / 100000.0;
    }

    /**
     * 获取Redis中所有的键的key
     *
     * @return
     */
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    /**
     * 获取所有的普通key-value
     *
     * @return
     */
    public Map<String, Object> getAllString() {
        Set<String> stringSet = getAllKeys();
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.STRING) {
                map.put(k, get(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Set -key-value
     *
     * @return
     */
    public Map<String, Set<Object>> getAllSet() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.SET) {
                map.put(k, getSet(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的ZSet正序  -key-value 不获取权重值
     *
     * @return
     */
    public Map<String, Set<Object>> getAllZSetRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                logger.debug("k:" + k);
                map.put(k, getZSetRange(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的ZSet倒序  -key-value 不获取权重值
     *
     * @return
     */
    public Map<String, Set<Object>> getAllZSetReverseRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                map.put(k, getZSetReverseRange(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的List -key-value
     *
     * @return
     */
    public Map<String, List<Object>> getAllList() {
        Set<String> stringSet = getAllKeys();
        Map<String, List<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.LIST) {
                map.put(k, getList(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Map -key-value
     *
     * @return
     */
    public Map<String, Map<String, Object>> getAllMap() {
        Set<String> stringSet = getAllKeys();
        Map<String, Map<String, Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.HASH) {
                map.put(k, getMap(k));
            }
        }
        return map;
    }

    /**
     * 添加一个list
     *
     * @param key
     * @param objectList
     */
    public void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }

    /**
     * 向list中增加值
     *
     * @param key
     * @param obj
     * @return 返回在list中的下标
     */
    public long addList(String key, Object obj) {
        return redisTemplate.boundListOps(key).rightPush(obj);
    }

    /**
     * 向list中增加值
     *
     * @param key
     * @param obj
     * @return 返回在list中的下标
     */
    public long addList(String key, Object... obj) {
        return redisTemplate.boundListOps(key).rightPushAll(obj);
    }

    /**
     * 输出list
     *
     * @param key List的key
     * @param s   开始下标
     * @param e   结束的下标
     * @return
     */
    public List<Object> getList(String key, long s, long e) {
        return redisTemplate.boundListOps(key).range(s, e);
    }

    /**
     * 输出完整的list
     *
     * @param key
     */
    public List<Object> getList(String key) {
        return redisTemplate.boundListOps(key).range(0, getListSize(key));
    }

    /**
     * 获取list集合中元素的个数
     *
     * @param key
     * @return
     */
    public long getListSize(String key) {
        return redisTemplate.boundListOps(key).size();
    }

    /**
     * 移除list中某值
     * 移除list中 count个value为object的值,并且返回移除的数量,
     * 如果count为0,或者大于list中为value为object数量的总和,
     * 那么移除所有value为object的值,并且返回移除数量
     *
     * @param key
     * @param object
     * @return 返回移除数量
     */
    public long removeListValue(String key, Object object) {
        return redisTemplate.boundListOps(key).remove(0, object);
    }

    /**
     * 移除list中某值
     *
     * @param key
     * @param objects
     * @return 返回移除数量
     */
    public long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }

    /**
     * 批量删除key对应的value
     *
     * @param key
     */
    public void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                remove(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blears
     */
    public void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 修改key名 如果不存在该key或者没有修改成功返回false
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blear
     */
    public void removeBlear(String blear) {
        redisTemplate.delete(redisTemplate.keys(blear));
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blears
     */
    public void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blear
     */
    public void removeByRegular(String blear) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.delete(s);
            }
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param blears
     */
    public void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param blear
     */
    public void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }

    /**
     * 移除key 对应的value
     *
     * @param key
     * @param value
     * @return
     */
    public Long removeZSetValue(String key, Object... value) {
        return redisTemplate.boundZSetOps(key).remove(value);
    }

    /**
     * 移除key ZSet
     *
     * @param key
     * @return
     */
    public void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }

    /**
     * 删除，键为K的集合，索引start<=index<=end的元素子集
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public void removeZSetRange(String key, Long start, Long end) {
        redisTemplate.boundZSetOps(key).removeRange(start, end);
    }

    /**
     * 并集 将key对应的集合和key1对应的集合合并到key2中
     * 如果分数相同的值，都会保留
     * 原来key2的值会被覆盖
     *
     * @param key
     * @param key1
     * @param key2
     */
    public void setZSetUnionAndStore(String key, String key1, String key2) {
        redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }

    /**
     * 获取整个有序集合ZSET，正序
     *
     * @param key
     */
    public Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }

    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，正序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置
     */
    public Set<Object> getZSetRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).range(start, end);
    }

    /**
     * 获取整个有序集合ZSET，倒序
     *
     * @param key
     */
    public Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }

    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，倒序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置
     */
    public Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 正序 -从小到大
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序 -从大到小
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }

    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }

    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }

    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }

    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }

    /**
     * 键为K的集合，sMin<=score<=sMax的元素个数
     *
     * @param key
     * @param sMin
     * @param sMax
     * @return
     */
    public long getZSetCountSize(String key, double sMin, double sMax) {
        return redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }

    /**
     * 获取Zset 键为K的集合元素个数
     *
     * @param key
     * @return
     */
    public long getZSetSize(String key) {
        return redisTemplate.boundZSetOps(key).size();
    }

    /**
     * 获取键为K的集合，value为obj的元素分数
     *
     * @param key
     * @param value
     * @return
     */
    public double getZSetScore(String key, Object value) {
        return redisTemplate.boundZSetOps(key).score(value);
    }

    /**
     * 元素分数增加，delta是增量
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public double incrementZSetScore(String key, Object value, double delta) {
        return redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }

    /**
     * 添加有序集合ZSET
     * 默认按照score升序排列，存储格式K(1)==V(n)，V(1)=S(1)
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Boolean addZSet(String key, double score, Object value) {
        return redisTemplate.boundZSetOps(key).add(value, score);
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param value
     * @return
     */
    public Long addZSet(String key, TreeSet<Object> value) {
        return redisTemplate.boundZSetOps(key).add(value);
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Boolean addZSet(String key, double[] score, Object[] value) {
        if (score.length != value.length) {
            return false;
        }
        for (int i = 0; i < score.length; i++) {
            if (addZSet(key, score[i], value[i]) == false) {
                return false;
            }
        }
        return true;
    }


    public void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }


    public void removeZSetRangeByScore(String key, double s, double e) {
        redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }


    public Boolean setSetExpireTime(String key, Long time) {
        return redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public Boolean setZSetExpireTime(String key, Long time) {
        return redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public Object get(int key) {
        return this.get(String.valueOf(key));
    }

    public Object get(long key) {
        return this.get(String.valueOf(key));
    }

    public Object get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }


    public List<Object> get(String... keys) {
        List<Object> list = new ArrayList<>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }


    public List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }


    public void set(long key, Object value) {
        this.set(String.valueOf(key), value);
    }

    public void set(int key, Object value) {
        this.set(String.valueOf(key), value);
    }

    public void set(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
    }


    public void set(String key, Object value, Long expireTime) {
        redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }


    public boolean setExpireTime(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    public DataType getType(String key) {
        return redisTemplate.type(key);
    }


    public void removeMapField(String key, Object... field) {
        redisTemplate.boundHashOps(key).delete(field);
    }


    public Long getMapSize(String key) {
        return redisTemplate.boundHashOps(key).size();
    }


    public Map<String, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }


    public <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }


    public Boolean hasMapKey(String key, String field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }


    public List<Object> getMapFieldValue(String key) {
        return redisTemplate.boundHashOps(key).values();
    }


    public Set<Object> getMapFieldKey(String key) {
        return redisTemplate.boundHashOps(key).keys();
    }


    public void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }


    public void addMap(String key, String field, Object value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }


    public void addMap(String key, String field, Object value, long time) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }


    public void watch(String key) {
        redisTemplate.watch(key);
    }


    public void addSet(String key, Object... obj) {
        redisTemplate.boundSetOps(key).add(obj);
    }


    public long removeSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).remove(obj);
    }


    public long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }


    public long getSetSize(String key) {
        return redisTemplate.boundSetOps(key).size();
    }


    public Boolean hasSetValue(String key, Object obj) {
        Boolean boo = null;
        int t = 0;
        while (true) {
            try {
                boo = redisTemplate.boundSetOps(key).isMember(obj);
                break;
            } catch (Exception e) {
                logger.error("key[" + key + "],obj[" + obj + "]判断Set中的值是否存在失败,异常信息:" + e.getMessage());
                t++;
            }
            if (t > times) {
                break;
            }
        }
        logger.info("key[" + key + "],obj[" + obj + "]是否存在,boo:" + boo);
        return boo;
    }


    public Set<Object> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }


    public Set<Object> getSetUnion(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).union(otherKey);
    }


    public Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).union(set);
    }


    public Set<Object> getSetIntersect(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).intersect(otherKey);
    }


    public Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).intersect(set);
    }

    public Object getAndSet(String key, Object value) {
        return redisTemplate.boundValueOps(key).getAndSet(value);
    }

    public Boolean setNX(final String lockKey, final String lockValue, final Long milliSecondTime) {
        final String luaScript = "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', " + milliSecondTime + ") then return 1 else return 0 end";
        RedisScript<Long> script = new DefaultRedisScript(luaScript, Long.class);
        Long res = (Long) redisTemplate.execute(script, Arrays.asList(lockKey.split(",")), lockValue);
        return res == 1;
    }

    public boolean tryLock(final String lockKey, final String lockValue, final Long milliSecondTime) {
        return setNX(lockKey, lockValue, milliSecondTime);
    }

    public void releaseLock(final String lockKey, final String lockValue) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> script = new DefaultRedisScript(luaScript, Long.class);
        redisTemplate.execute(script, Arrays.asList(lockKey.split(",")), lockValue);
    }

    public boolean lockBlocking(final String lockKey, final String lockValue, final Long milliSecondTimeExpire, final long timeout, TimeUnit unit) {
        long nanoWaitForLock = unit.toNanos(timeout);
        long start = System.nanoTime();
        try {
            while ((System.nanoTime() - start) < nanoWaitForLock) {
                if (tryLock(lockKey, lockValue, milliSecondTimeExpire)) {
                    return true;
                }
            }
            // 随机时间,防止活锁
            TimeUnit.MILLISECONDS.sleep(1000 + new Random().nextInt(100));
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("[RedisLock] fail to get lock. key: %s, thread: %s", lockKey, Thread.currentThread()), ex);
            }
        }
        return false;
    }

    public Long setRange(final String key, final Object value) {
        final String luaScript = "redis.call('setrange', KEYS[1], 0, ARGV[1])";
        RedisScript<Long> script = new DefaultRedisScript(luaScript, Long.class);
        return (Long) redisTemplate.execute(script, Arrays.asList(key), value);
    }


    public String buildRedisKey(String prefix, String... params) {
        for (String param : params) {
            if (StringUtils.hasText(param)) {
                prefix = prefix.concat(":").concat(param);
            }
        }
        return prefix;
    }

    public Long eval(final String luaScript, final List keys, final List params) {
        RedisScript<Long> script = new DefaultRedisScript(luaScript, Long.class);
        return stringRedisTemplate.execute(script, keys, params);
    }

    public Long eval(final RedisScript<Long> script, final List keys, final Object[] params) {
        return stringRedisTemplate.execute(script, keys, params);
    }

    public String scriptLoad(final String script) {
        return (String) redisTemplate.execute((RedisCallback<String>) connection -> {
            try {
                return connection.scriptLoad(script.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        });
    }

    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

}

