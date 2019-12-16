package com.pt.jsonable.bean;

/**
 * @desc:
 * @author: ningqiang.zhao
 * @time: 2019-12-16 16:34
 **/
@com.pt.jsonable.annotation.JSONAble
public class Pit {
    public String name;
    public int id;

    public Pit(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
