package com.pt.jsonable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @desc: 排除不需要的字段，为要生产JSONObject的类的字段增加这个注解会排除掉
 * @author: ningqiang.zhao
 * @time: 2019-12-12 14:11
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Exclude {
}
