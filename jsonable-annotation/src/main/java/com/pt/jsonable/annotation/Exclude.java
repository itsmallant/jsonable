package com.pt.jsonable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @desc:
 * @author: ningqiang.zhao
 * @time: 2019-12-12 14:11
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Exclude {
}
