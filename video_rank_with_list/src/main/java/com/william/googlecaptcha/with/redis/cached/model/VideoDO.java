package com.william.googlecaptcha.with.redis.cached.model;

public class VideoDO {
    private String name;
    private String url;
    private double price;
    private int weight;

    public VideoDO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public VideoDO(String name, String url, double price, int weight) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.weight = weight;
    }
}
