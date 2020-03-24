package com.model;

public class mooc_visual {
    private Integer id;

    private String sources;

    private String target;

    private String sup;

    private String conf;

    private Integer catycray;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources == null ? null : sources.trim();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target == null ? null : target.trim();
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup == null ? null : sup.trim();
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf == null ? null : conf.trim();
    }

    public Integer getCatycray() {
        return catycray;
    }

    public void setCatycray(Integer catycray) {
        this.catycray = catycray;
    }
}