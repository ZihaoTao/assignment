package com.assignment.pojo;

import java.util.Date;

/**
 * Created by tino on 12/15/18.
 */
public class Dapper {
    private Integer id;

    private String name;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public Dapper(Integer id, String name, Integer status, Date createTime, Date updateTime) {
        this.id = id;
        this .name = name;
        this.status = status;
        this .createTime = createTime;
        this.updateTime = updateTime;
    }

    public Dapper() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
