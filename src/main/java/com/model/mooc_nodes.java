package com.model;

public class mooc_nodes {
    private Integer id;

    private String projectid;

    private String projectname;

    private Integer projectcount;

    private Integer catycray;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid == null ? null : projectid.trim();
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname == null ? null : projectname.trim();
    }

    public Integer getProjectcount() {
        return projectcount;
    }

    public void setProjectcount(Integer projectcount) {
        this.projectcount = projectcount;
    }

    public Integer getCatycray() {
        return catycray;
    }

    public void setCatycray(Integer catycray) {
        this.catycray = catycray;
    }
}