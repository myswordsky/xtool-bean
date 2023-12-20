package io.github.xtools.bean.tools;


public class ImplClassUtil {



    public static String getEntityKey(String name){
        return name + "-e-" + name;
    }

    public static String getDtoKey(String name){
        return name + "-d-" + name;
    }
}
