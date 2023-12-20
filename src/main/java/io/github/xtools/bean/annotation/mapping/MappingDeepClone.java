package io.github.xtools.bean.annotation.mapping;


import java.lang.annotation.*;

/**
 * 深度克隆(需要实现Serializable接口)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MappingDeepClone {

}
