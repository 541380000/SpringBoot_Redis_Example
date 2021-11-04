package com.william.googlecaptcha.with.redis.cached.controller;

import com.william.googlecaptcha.with.redis.cached.model.VideoDO;
import com.william.googlecaptcha.with.redis.cached.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class RankController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String RANK_KEY = "rank-service:rank:video-rank";

    @GetMapping("get_rank")
    public JsonData getRank(){
        saveRank();
        List<VideoDO> list = redisTemplate.opsForList().range(RANK_KEY, 0, -1);
        return JsonData.buildSuccess(list);
    }

    void saveRank(){
        redisTemplate.delete(RANK_KEY);
        VideoDO video1 = new VideoDO("SpringBoot", "static/image/springboot", 9.99, 1);
        VideoDO video2 = new VideoDO("Redis", "static/image/redis", 199, 2);
        VideoDO video3 = new VideoDO("SpringCloud", "static/image/springcloud", 299, 3);
        VideoDO video4 = new VideoDO("Mysql", "static/image/mysql", 44, 4);
        VideoDO video5 = new VideoDO("MongoDB", "static/image/mangodb", 29.9, 5);
        redisTemplate.opsForList().leftPushAll(RANK_KEY, video1, video2, video3, video4, video5);
        /*
        redisTemplate.opsForValue().set("test", video1);
        System.out.println(redisTemplate.opsForValue().get("test").getClass());
        for (Object v : ((LinkedHashMap<Object, Object>)redisTemplate.opsForValue().get("test")).keySet()){
            System.out.println(v);
        }
        */
    }



}
