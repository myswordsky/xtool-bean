package io.github.xtools.bean.annotation.mapping;


import java.lang.annotation.*;

/**
 *  不同类型是否自动转换(不加此方法 默认类型自动转换)
 *  例如：entity:   Date time;Date time; Integer a
 *          dto: String time;long time; String  a
 *  包装类型视为同一类型
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MappingCopyType {

    /**
     * 默认类型自动转换 true：且字段内用XToolMapping.copyType=false该字段不可转换  false：所有字段不可转换
     */
    boolean value() default true;
}
