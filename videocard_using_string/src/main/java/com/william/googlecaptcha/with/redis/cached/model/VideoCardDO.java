package com.william.googlecaptcha.with.redis.cached.model;

import java.util.List;

public class VideoCardDO {
    private String title;
    private List<VideoDO> videoList;

    public VideoCardDO(){}
    public VideoCardDO(String title, List<VideoDO> videoList) {
        this.title = title;
        this.videoList = videoList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VideoDO> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoDO> videoList) {
        this.videoList = videoList;
    }
}
