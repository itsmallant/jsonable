package com.pt.jsonable.bean;

import com.pt.thirdlib.Pit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @desc:
 * @author: ningqiang.zhao
 * @time: 2019-12-16 16:30
 **/
@com.pt.jsonable.annotation.JSONAble
public class User {
    private String name;
    private int age;
    private Collection<Goods> buyedGoods;
    private Goods[] hateGoods;
    private String[] followers;
    public Pit[] lovePits;
    private ArrayList<Pit> hatePits;

    public Factory workFactory;

    public ArrayList<Pit> maybePits;


    public User(String name, int age, Collection<Goods> buyedGoods, Goods[] hateGoods, String[] followers, Pit[] lovePits, ArrayList<Pit> hatePits, Factory workFactory, ArrayList<Pit> maybePits) {
        this.name = name;
        this.age = age;
        this.buyedGoods = buyedGoods;
        this.hateGoods = hateGoods;
        this.followers = followers;
        this.lovePits = lovePits;
        this.hatePits = hatePits;
        this.workFactory = workFactory;
        this.maybePits = maybePits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Collection<Goods> getBuyedGoods() {
        return buyedGoods;
    }

    public void setBuyedGoods(Collection<Goods> buyedGoods) {
        this.buyedGoods = buyedGoods;
    }

    public Goods[] getHateGoods() {
        return hateGoods;
    }

    public void setHateGoods(Goods[] hateGoods) {
        this.hateGoods = hateGoods;
    }

    public String[] getFollowers() {
        return followers;
    }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public Pit[] getLovePits() {
        return lovePits;
    }

    public void setLovePits(Pit[] lovePits) {
        this.lovePits = lovePits;
    }

    public ArrayList<Pit> getHatePits() {
        return hatePits;
    }

    public void setHatePits(ArrayList<Pit> hatePits) {
        this.hatePits = hatePits;
    }
}
