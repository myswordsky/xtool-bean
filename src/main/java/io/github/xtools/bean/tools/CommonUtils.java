package io.github.xtools.bean.tools;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtils {
    /**
     * 取头字母大写
     */
    public static String getStartStringUp(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getStartStringLow(String str){
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


    public static void assertNotNull(Object obj, String msg){
        if(obj == null){
            throw new NullPointerException(msg);
        }
    }

    public static String toString(Throwable e) {
        try (StringWriter writer = new StringWriter(); PrintWriter print = new PrintWriter(writer)) {
            e.printStackTrace(print);
            return writer.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /**
     * entity.User,entity.UserOther
     */
    public static String getJoinName(Class<?> source, Class<?> target){
        int result = source.getName().compareTo(target.getName());
        if(result <= 0){
            return String.join(",",  source.getName(), target.getName());
        }else {
            return String.join(",", target.getName(), source.getName());
        }
    }

    public static String getJoinName(String source, String target){
        return source.compareTo(target) <= 0 ? String.join(",",  source, target) : String.join(",", target, source);
    }

    public static boolean isEntity(String source, String target){
        return source.compareTo(target) <= 0;
    }


    public static String getSimpleName(String classFullName){
        return classFullName.substring(classFullName.lastIndexOf(".")+1);
    }

}
