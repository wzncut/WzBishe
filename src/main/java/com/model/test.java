package com.model;

public class test {
    private Integer aaa;

    private String bbb;

    public Integer getAaa() {
        return aaa;
    }

    public void setAaa(Integer aaa) {
        this.aaa = aaa;
    }

    public String getBbb() {
        return bbb;
    }

    public void setBbb(String bbb) {
        this.bbb = bbb == null ? null : bbb.trim();
    }
}