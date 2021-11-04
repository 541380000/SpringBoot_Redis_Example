package com.william.googlecaptcha.with.redis.cached.controller;

import com.william.googlecaptcha.with.redis.cached.util.CommonUtil;
import com.william.googlecaptcha.with.redis.cached.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1")
public class CouponController_bad {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String COUPON_LOCK_KEY = "goods-service:coupon:lock";

    /*
    using redisTemplate.opsForValue().setIfAbsent(COUPON_LOCK_KEY, 1); to lock
    using redisTemplate.delete(COUPON_LOCK_KEY); to unlock
    Drawbacks of getCoupon_1 are
    1. If program lock it, and break down when doing something, the lock won't be released.
    2. If we set an expire time but program runs slowly, when program try to unlock, the lock may not exist.
    3. Even worse, in situation of 2, if another program lock it, this slow program will unlock another program's lock.
     */


    @GetMapping("get_coupon1")
    public JsonData getCoupon_1(HttpServletRequest request){
        // set to 1 to lock
        if (redisTemplate.opsForValue().setIfAbsent(COUPON_LOCK_KEY, 1) == true) {
            // do_something here
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redisTemplate.delete(COUPON_LOCK_KEY);
        }
        return JsonData.buildSuccess("Success");
    }

    /*
    We will make it better in getCoupon_2, watch it
     */


    @GetMapping("get_coupon2")
    public JsonData getCoupon_2(HttpServletRequest request){
        String key = getUserKey(request);

        // set lock value to program's id, when deleting the lock, we check id first
        // we also set an expire time in case the program brean down
        while(true) {
            if (redisTemplate.opsForValue().setIfAbsent(COUPON_LOCK_KEY, key, 3, TimeUnit.SECONDS) == true) {
                // do_something here
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println();
                    System.out.println(redisTemplate.opsForValue().get(COUPON_LOCK_KEY));
                    System.out.println();
                    if (redisTemplate.opsForValue().get(COUPON_LOCK_KEY).equals(key)){
                        System.out.println("deleting key");
                        redisTemplate.delete(COUPON_LOCK_KEY);
                    }
                    break;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return JsonData.buildSuccess("Success");
    }

    /*
    It is better, ah?
    But there still exist problems.
    If after we check the key with if (redisTemplate.opsForValue().get(COUPON_LOCK_KEY) == key) successfully,
    we are ready to delete the lock.
    During this, the lock expired and another program get the lock.
    This program will delete it.

    The real problem is that the lock and unlock operation is not atomic.
    On Solution for this is Lua script
    I will show you the code
     */


    @GetMapping("get_coupon3")
    public JsonData saveCoupon(HttpServletRequest request){
        
        String uuid = UUID.randomUUID().toString();
        lock(uuid, COUPON_LOCK_KEY);

        return JsonData.buildSuccess();

    }


    private void lock(String lock_value,String lockKey){

        //lua script
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        // lock
        Boolean nativeLock = redisTemplate.opsForValue().setIfAbsent(lockKey, lock_value, Duration.ofSeconds(30));

        if(nativeLock){
            try{
                //TODO Do something here
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            } finally{
                Boolean result = (Boolean) redisTemplate.execute( new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockKey),lock_value);
                System.out.println("解锁状态:"+result);
            }

        }else {
            // spin
            try {
                System.out.println("加锁失败，睡眠5秒 进行自旋");
                TimeUnit.MILLISECONDS.sleep(5000);
            }
            catch (InterruptedException e)
            { }
            lock(lock_value, lockKey);
        }
    }

    /*
    It sucks! right?
    In fact, using Lua to implement distributed lock is bothering.
    Luckily, redis provide a tool. see also https://redis.io/topics/distlock

     */


    private String getUserKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = CommonUtil.MD5(ip+userAgent);
        return key;
    }

}
