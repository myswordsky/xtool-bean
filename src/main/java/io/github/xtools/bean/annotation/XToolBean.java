package io.github.xtools.bean.annotation;

import io.github.xtools.bean.annotation.mapping.MappingCopyType;
import io.github.xtools.bean.annotation.mapping.MappingDeepClone;

import java.lang.annotation.*;

/**
 * 开启预编译功能，该注解上的类必须是接口
 * 两个bean必须要有getter setter和无参构造器
 * 如果有异常请查看target#generated-sources生成文件 或 maven-package查看编译报错信息
 * 文档：<a href="https://blog.csdn.net/weixin_47649446/article/details/131094679">...</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface XToolBean {

    /**
     * 是否是Spring Bean,加上Component 注解
     * 可使用统一管理
     *
     * @return /
     */
    boolean isSpring() default false;

    /**
     * 不同类型是否自动转换
     *  详见{@link MappingCopyType}
     *
     *  @return /
     */
    boolean copyType() default true;

    /**
     * 克隆模式(使用后此类下全部克隆)
     * 作用于方法 详见{@link MappingDeepClone}
     * DeepClone 深度克隆(需要实现Serializable接口)
     * 默认：基础类型(还有String)不影响，对象类型浅克隆
     *
     * @return /
     */
    Class<?> mappingControl() default void.class;

    /**
     * 生成类额外导入的类
     * 可以结合defaultValue或targetValue实现方法
     *
     * @return /
     */
    Class<?>[] imports() default {};

    /**
     * 实现类的继承
     *
     * @return /
     */
    Class<?> extendsClazz() default void.class;
}