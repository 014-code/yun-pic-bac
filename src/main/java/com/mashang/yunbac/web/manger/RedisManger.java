package com.mashang.yunbac.web.manger;

import cn.hutool.core.util.BooleanUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis通用操作类
 */
@Service
public class RedisManger {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;


    /**
     * 设置字符串键值并携带过期时间
     */
    public void setWithTTL(String key, String value, long ttl, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    /**
     * 获取字符串键值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 尝试获取锁-简单坂
     *
     * @param key
     * @return
     */
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        //获取锁
        RLock lock = redissonClient.getLock(key);
//        //只有key不存在时才能设置成功，所以每次查询图片首页时只要调用这个方法判断返回值，false证明已被写入(即占据态)，此时得等待
//        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
//        return BooleanUtil.isTrue(flag);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param key
     */
    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取锁
     *
     * @param key
     */
    public boolean getLock(String key) {
        return tryLock(key, 10, 30, TimeUnit.SECONDS);
    }

}
