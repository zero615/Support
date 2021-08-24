package com.zero.support.recycler.annotation;


import com.zero.support.recycler.ItemViewBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecyclerViewBind {
    Class<? extends ItemViewBinder> value() default ItemViewBinder.class;

    int layout() default -1;

    int br() default -1;
}
