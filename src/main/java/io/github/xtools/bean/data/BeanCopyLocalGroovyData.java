package io.github.xtools.bean.data;


import io.github.xtools.bean.tools.CommonUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *  类映射工具
 * @author xzb
 */
public class BeanCopyLocalGroovyData {
    /**
     * 映射类字段
     */
    private static final Map<Class<?>, Map<String, Field>> classMap = new HashMap<>();

    public static Map<String, Field> getClassMap(Class<?> clazz){
        return classMap.get(clazz);
    }

    public static Map<String, Field> putClassMap(Class<?> clazz){
        Map<String, Field> stringFieldMap = classMap.get(clazz);
        if(stringFieldMap != null){
            return stringFieldMap;
        }
        Map<String, Field> map = new HashMap<>();
        getClassAllFields(clazz, map);
        classMap.put(clazz, map);
        return map;
    }


    public static void getClassAllFields(Class<?> clazz, Map<String, Field> map){
        if(clazz == null){
            return;
        }
        //判断父类的字段是否有Get方法
        Field[] declaredFields = clazz.getDeclaredFields();
        for(Field field :declaredFields){
            StringBuilder base = new StringBuilder(CommonUtils.getStartStringUp(field.getName()));
            String getName = "get";
            String setName = "set";
            if(field.getType().equals(boolean.class)){
                getName = "is";
                getName += base;
                setName += base;
            }else{
                getName += base;
                setName += base;
            }
            try {
                Method method = clazz.getMethod(getName);
                Method method2 = clazz.getMethod(setName, field.getType());
                if(isPublic(method) && isPublic(method2)){
                    if(!map.containsKey(field.getName())){//以子类为准
                        map.put(field.getName(), field);
                    }
                }
            } catch (NoSuchMethodException ignored) {
            }

        }
        getClassAllFields(clazz.getSuperclass(), map);
    }

    private static boolean isPublic(Method method){
        return method.getModifiers() == Modifier.PUBLIC;//method.isDefault();
    }
}
