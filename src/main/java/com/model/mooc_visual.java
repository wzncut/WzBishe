package com.model;

public class mooc_visual {
    private Integer id;

    private String source;

    private String target;

    private String sup;

    private String conf;

    private Integer group;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
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

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }
}