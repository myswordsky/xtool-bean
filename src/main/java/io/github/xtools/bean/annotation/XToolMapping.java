package io.github.xtools.bean.annotation;

import io.github.xtools.bean.annotation.mapping.MappingIndexEnums;
import io.github.xtools.bean.fliter.config.service.FieldType;

import java.lang.annotation.*;



@Repeatable(XToolMappings.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface XToolMapping {
    String XToolMapping_defaultValue = "this#XToolMapping#defaultValue";
    String XToolMapping_targetValue = "this#XToolMapping#targetValue";
    /**
     * 目标字段名
     * 同target()&&index()用多个，否则随机取其中一个
     *
     * @return /
     */
    String target();

    /**
     * copy来源字段名
     *
     * @return /
     */
    String source() default "";

    /**
     * 目标指向第几个参数 例如使用defaultValue表达式或者指定target对象时
     * XToolMapping(source = "name", target = "name", index = MappingIndexEnums.First, defaultValue = "\"1\"")
     * void copy(UserDTO first, User second)
     * 则会为第一个参数UserDTO first的name字段赋值 "1" Default两者皆可(前提是两者类型一致，否者需要指定配置)
     *
     * @return /
     */
    MappingIndexEnums index() default MappingIndexEnums.Default;

    /**
     * 设置目标字段默认值(支持表达式) 字段不同类型需要指定index(如果同类型可以无需指定)
     * 例：defaultValue = "1"
     * 支持{@link FieldType}的基础类型和包装类型
     * 其它类型(或新对象) 需要XToolMapping.index()配置
     * 1 数字:defaultValue = "25"
     * 2 字符串:defaultValue = "\"25\""
     * 3 使用拷贝来源变量值(固定为source.getXX)
     *      defaultValue = "source.getName().toString"
     * 4 对象需要携带全名：
     * defaultValue = "new java.util.Date()"
     * defaultValue = "new java.math.BigDecimal(1.23)"
     * defaultValue = "new java.util.ArrayList<io.github.xtools.bean.entity.config.ClassInfo>(){{
     *             add(new io.github.xtools.bean.entity.config.ClassInfo());
     *         }};"
     * defaultValue = "new new io.github.xtools.bean.entity.config.User(\"name\", 25)"
     * 5 调用接口方法
     *
     * @return /
     */
    String defaultValue() default XToolMapping_defaultValue;

    /**
     * 设置目标字段目标值(忽略来源值，支持表达式) 字段不同类型需要指定index(如果同类型可以无需指定)
     * 例：targetValue = "1"
     * 支持{@link FieldType}的基础类型和包装类型
     * 其它类型(或新对象) 需要XToolMapping.index()配置
     * 1 数字:defaultValue = "25"
     * 2 字符串:defaultValue = "\"25\""
     * 3 使用拷贝来源变量值(固定为source.getXX)
     *      defaultValue = "source.getName().toString"
     * 4 对象需要携带全名：
     * targetValue = "new java.util.Date()"
     * targetValue = "new java.math.BigDecimal(1.23)"
     * targetValue = "new java.util.ArrayList<io.github.xtools.bean.entity.config.ClassInfo>(){{
     *             add(new io.github.xtools.bean.entity.config.ClassInfo());
     *         }};"
     * targetValue = "new new io.github.xtools.bean.entity.config.User(\"name\", 25)"
     * 5 调用接口方法
     *
     * @return /
     */
    String targetValue() default XToolMapping_targetValue;

    /**
     * 日期格式
     * 例：dateFormat = "yyyy-MM-dd"
     * 默认："yyyy-MM-dd HH:mm:ss"
     *
     * @return /
     */
    String dateFormat() default "";

    /**
     * 日期时区
     * 例：timeZone = "GMT+8"
     *
     * @return /
     */
    String timeZone() default "";

    /**
     * 小数格式化(默认四色五入)
     * 目前支持格式：Double->String Float->String BigDecimal->String
     * 例： numberFormat = "#.##"   1.234 -> "1.23"
     *
     * @return /
     */
    String numberFormat() default "";

    /**
     * 是否忽略拷贝
     *
     * @return /
     */
    boolean ignore() default false;

    /**
     *  不同类型是否自动生成(可以映射的就直接映射)
     *  例如：entity:   Date time;Date time; Integer a
     *          dto: String time;long time; String  a
     *  包装类型视为同一类型
     *
     *  @return /
     */
    boolean copyType() default true;

    /**
     * json转对象或对象转json(需要fastjson的支持)
     *
     * @return /
     */
    boolean jsonMapping() default false;


    /**
     * 字段方法执行器 (使用此功能defaultValue失效、且需要配合resultTypeIsInner使用) 必须指定index First or Second且类型一致
     * user方法(目标字段名) 执行映射时， 可以将返回值转换为 User 类型。
     *     XToolMapping(source = "name", target = "name", index = MappingIndexEnums.First, resultType = Name.class)//source不为空则为默认字段 找不到则不处理
     *     void copy(Source source, Target target);
     * <p>
     * //public外部类  非public内部类需要加上resultTypeIsInner = false
     * public class Name {
     *     public User user(String user) {  //User:返回字段类型  user:返回字段名称(String:来源字段类型 user:随意变量值(取决于source))
     *         String[] parts = user.split(" ");
     *         return new User(parts[0], parts[1]);
     *     }
     *     public String name(String name) {
     *         return "new User(parts[0], parts[1])";
     *     }
     *
     * @return /
     */
    Class<?> resultType() default void.class;

    /**
     * #resultType 是否是内部类
     *
     * @return /
     */
    boolean resultTypeIsInner() default false;

    /**
     * 尚未实现(应该单独抽成注解)： 执行 preProcess 方法。
     *     XToolMapping(source = "name", target = "name", beforeOrAfterHandle = UserImpl.class)
     *     void copy(Source first, Target second);
     * <p>
     *      public class impl I {
     *       public String preProcess() {}
     *       public String afterProcess() {}
     *      }
     *
     *  @return /
     */
    Class<?> beforeOrAfterHandle() default void.class;

}