package com.william.googlecaptcha.with.redis.cached.controller;

import com.william.googlecaptcha.with.redis.cached.model.VideoCardDO;
import com.william.googlecaptcha.with.redis.cached.model.VideoDO;
import com.william.googlecaptcha.with.redis.cached.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class VideoCardController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "page-service:video-card:";

    @GetMapping("get_card")
    public JsonData getCard(@RequestParam(name="sort") String sort){
        Object res = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + sort);
        if (res == null){
            // simulate retrieving from database or file
            VideoDO video1 = new VideoDO("SpringBoot", "static/image/springboot", 9.99, 1);
            VideoDO video2 = new VideoDO("Redis", "static/image/redis", 199, 2);
            VideoDO video3 = new VideoDO("SpringCloud", "static/image/springcloud", 299, 3);
            VideoDO video4 = new VideoDO("Mysql", "static/image/mysql", 44, 4);
            VideoDO video5 = new VideoDO("MongoDB", "static/image/mangodb", 29.9, 5);
            List<VideoDO> list = new LinkedList<VideoDO>();
            list.add(video1);
            list.add(video2);
            list.add(video3);
            list.add(video4);
            list.add(video5);
            VideoCardDO videoCard = new VideoCardDO("Hot", list);
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + sort, list, 10, TimeUnit.MINUTES);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return JsonData.buildSuccess(videoCard);
        }
        else{
            res = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + sort);
            return JsonData.buildSuccess(res);
        }
    }
}
