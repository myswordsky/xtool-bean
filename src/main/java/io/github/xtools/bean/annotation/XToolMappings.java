package io.github.xtools.bean.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface XToolMappings {
    XToolMapping[] value();
}