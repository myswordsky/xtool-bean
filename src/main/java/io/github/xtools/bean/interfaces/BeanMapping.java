package io.github.xtools.bean.interfaces;

import io.github.xtools.bean.entity.config.Constant;
import io.github.xtools.bean.entity.groovy.MappingClassGroovyConfig;
import io.github.xtools.bean.fliter.groovy.MappingGroovyFiler;
import io.github.xtools.bean.tools.CommonUtils;
import io.github.xtools.bean.tools.ImplClassUtil;
import groovy.lang.GroovyClassLoader;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


@SuppressWarnings("unchecked")
public class BeanMapping {
    private static boolean isAuto;
    private static Boolean isInitAuto;
    private static Boolean isInitClazz;

    private static final Map<String, Object> groovyMap = new HashMap<>();

    private static final Map<String, Object> compileMap = new HashMap<>();

    /**
     *  全局只初始化加载一次
     * @param isAutoGen 是否使用groovy动态生成实现
     */
    public static String initAuto(Boolean isAutoGen){
        if(isInitAuto == null){
            isInitAuto = true;
            isAuto = isAutoGen;
        }
        return null;
    }
    public static synchronized void initClass(){
        if(isInitClazz != null){
            return;
        }
        isInitClazz = true;

        System.out.println("BeanMapping： 初始化开始");
        //Set<Class<?>> fileClass = ClassScanningUtil.findFileClass(packageName, false);
        try{
            Object object = Class.forName(Constant.getGenPackageClass()).getDeclaredConstructor().newInstance();
            Method getPackageList = object.getClass().getMethod(Constant.GenClazzMethodName);
            List<String> fileClass = (List<String>) getPackageList.invoke(object);

            for(String clazzFullName:fileClass){
                Class<?> clazz = Class.forName(clazzFullName);
                List<Class<?>> classes = Arrays.asList(clazz.getInterfaces());
                if(!classes.contains(BaseMapping.class)){
                    continue;
                }
                Type[] genericInterfaces = clazz.getGenericInterfaces();
                for (Type genericInterface : genericInterfaces) {
                    if (genericInterface instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        Class<?> entity = (Class<?>) actualTypeArguments[0];
                        Class<?> dto = (Class<?>) actualTypeArguments[1];

                        Object impl = clazz.getDeclaredConstructor().newInstance();
                        compileMap.put(CommonUtils.getJoinName(entity, dto), impl);
                        compileMap.put(ImplClassUtil.getDtoKey(dto.getName()), impl);
                        compileMap.put(ImplClassUtil.getEntityKey(entity.getName()), impl);
                    }
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        System.out.println("BeanMapping： 初始化结束");
    }


    public static <D, E> List<E> copy(List<D> sourceList, Class<E> target){
        List<E> list = new ArrayList<>();
        if(sourceList == null || sourceList.size() == 0){
            return list;
        }
        for(D source:sourceList){
            list.add(copy(source, target));
        }
        return list;
    }

    public static <D, E> E copy(D source, Class<E> target){
        if(source == null){
            return null;
        }

        initClass();
        String sourceName = source.getClass().getName();
        String targetName = target.getName();
        int result = sourceName.compareTo(targetName);
        if(result == 0){
            String key = sourceName + "," + targetName;
            Object baseMapping = compileMap.get(key);
            if(baseMapping != null){
                return ((BaseMapping<D, E>)baseMapping).toDto(source);
            }
            Object entityObject = compileMap.get(ImplClassUtil.getEntityKey(targetName));
            if(entityObject != null){
                return (E)((BaseMapping<D, E>)entityObject).thisEntity(source);
            }
            Object dtoObject = compileMap.get(ImplClassUtil.getDtoKey(targetName));
            if(dtoObject != null){
                return ((BaseMapping<D, E>)dtoObject).thisDto((E)source);
            }
            throwException();
        }else if(result < 0){//User compareTo UserDTO  result: < 0  --> User,UserDTO
            String joinName = sourceName + "," + targetName;
            Object o = compileMap.get(joinName);
            if(o != null){
                return ((BaseMapping<D, E>)o).toDto(source);
            }
            throwException();
        }else{
            String joinName = targetName + "," + sourceName;
            Object o = compileMap.get(joinName);
            if(o != null){
                return ((BaseMapping<E, D>)o).toEntity(source);
            }
            throwException();
        }
        //是否自动生成
        if(isAuto){
            return copyAuto(source, target);
        }
        throw new NullPointerException("不存在映射数据，请开启自动拷贝或配置映射方法");
    }

    private static Object newInstance(String classFullName){
        try{
            return Class.forName(classFullName).getDeclaredConstructor().newInstance();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static void throwException(){
        if(!isAuto){
            throw new NullPointerException(String.format("不存在映射数据，请开启自动拷贝或配置映射方法或初始化数据 see %s#init(String packageName)"
                    , BeanMapping.class.getName()));
        }
    }

    /**
     * groovy核心组装
     */
    public static <D, E> E copyAuto(D source, Class<E> target){
        String sourceName = source.getClass().getName();
        String targetName = target.getName();
        int result = sourceName.compareTo(target.getName());
        if(result == 0){
            String key = targetName + "," + sourceName;
            Object baseMapping = groovyMap.get(key);
            if(baseMapping == null){
                Object dtoObject = groovyMap.get(ImplClassUtil.getDtoKey(targetName));
                if(dtoObject != null){
                    return ((BaseMapping<D, E>)dtoObject).thisDto((E)source);
                }
                Object entityObject = groovyMap.get(ImplClassUtil.getEntityKey(targetName));
                if(entityObject != null){
                    return (E)((BaseMapping<D, E>)entityObject).thisEntity(source);
                }
                synchronized (key.intern()){
                    Object o = groovyMap.get(key);
                    if(o == null){
                        baseMapping = groovy(target, target);
                        groovyMap.put(key, baseMapping);
                    }else {
                        baseMapping = o;
                    }
                }
            }
            return ((BaseMapping<D, E>)baseMapping).toDto(source);
        }else if(result < 0){//User compareTo UserDTO  result: < 0  --> User,UserDTO
            String key = targetName + "," + sourceName;
            Object baseMapping = groovyMap.get(key);
            if(baseMapping == null){
                synchronized (key.intern()){
                    Object o = groovyMap.get(key);
                    if(o == null){
                        baseMapping = groovy(source.getClass(), target);
                        groovyMap.put(key, baseMapping);
                        groovyMap.putIfAbsent(ImplClassUtil.getDtoKey(targetName), baseMapping);
                        groovyMap.putIfAbsent(ImplClassUtil.getEntityKey(sourceName), baseMapping);
                    }else {
                        baseMapping = o;
                    }
                }
            }
            return ((BaseMapping<D, E>)baseMapping).toDto(source);
        }else{
            String key = sourceName + "," + targetName;
            Object baseMapping = groovyMap.get(key);
            if(baseMapping == null){
                synchronized (key.intern()){
                    Object o = groovyMap.get(key);
                    if(o == null){
                        baseMapping = groovy(target, source.getClass());
                        groovyMap.put(key, baseMapping);
                        groovyMap.putIfAbsent(ImplClassUtil.getEntityKey(targetName), baseMapping);
                        groovyMap.putIfAbsent(ImplClassUtil.getDtoKey(sourceName), baseMapping);
                    }else {
                        baseMapping = o;
                    }
                }
            }
            return ((BaseMapping<E, D>)baseMapping).toEntity(source);
        }
    }

    /**
     * 约500ms
     */
    private static Object groovy(Class<?> entity, Class<?> dto){
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        String build = new MappingGroovyFiler(new MappingClassGroovyConfig(entity, dto, null)).build();
        //System.err.println(build);
        Class<?> clazz = groovyClassLoader.parseClass(build);
        try{
            return clazz.newInstance();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
