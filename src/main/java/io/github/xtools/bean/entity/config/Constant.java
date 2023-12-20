package io.github.xtools.bean.entity.config;


public class Constant {
    public static final String ContactMethod = "And";



    public static final String GenClazzPackage = "io.github.xtools.bean";
    public static final String GenClazzName = "BeanPackageClazz";
    public static final String GenClazzMethodName = "getPackageClazzList";


    public static String getGenPackageClass(){
        return GenClazzPackage + "." + GenClazzName;
    }
}
