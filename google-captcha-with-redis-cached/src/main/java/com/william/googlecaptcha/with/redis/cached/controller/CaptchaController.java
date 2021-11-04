package com.william.googlecaptcha.with.redis.cached.controller;

import com.google.code.kaptcha.Producer;
import com.william.googlecaptcha.with.redis.cached.util.CommonUtil;
import com.william.googlecaptcha.with.redis.cached.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/")
public class CaptchaController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Producer captchaProducer;

    private static final String CAPTCHA_CACHE_TABLE_PREFIX = "captcha-service:captcha";

    @GetMapping("/get_captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        String text = captchaProducer.createText();

        String key = getCaptchaKey(request);

        // set 10 minutes expire
        redisTemplate.opsForValue().set(key, text, 10, TimeUnit.MINUTES);

        // write result to output stream
        BufferedImage image = captchaProducer.createImage(text);

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "jpg", os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("get_key")
    public JsonData getKey(HttpServletRequest request){
        String key = getCaptchaKey(request);
        String res = (String) redisTemplate.opsForValue().get(key);
        if (res == null)
            return JsonData.buildError("Expired");
        return JsonData.buildSuccess(res);
    }


    // Get captcha key of redis by IP and userAgent
    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = this.CAPTCHA_CACHE_TABLE_PREFIX + CommonUtil.MD5(ip+userAgent);
        return key;
    }

}
