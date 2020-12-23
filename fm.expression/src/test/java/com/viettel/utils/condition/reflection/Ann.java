package com.viettel.utils.condition.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Ann {
    String ten() default "mot cai ten";
}
