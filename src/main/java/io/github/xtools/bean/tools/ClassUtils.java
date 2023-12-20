package io.github.xtools.bean.tools;


import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Method;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

public class ClassUtils {
    private final static Map<String, Method> methodMap = new HashMap<>();
    private final static Map<String, Object> clazzMap = new HashMap<>();
    private final static Set<String> clazzSet = new HashSet<>();



    public static boolean isPublicMethod(ExecutableElement element) {
        Set<Modifier> set = element.getModifiers();
        return set.contains(PUBLIC) && !set.contains(STATIC) && !set.contains(ABSTRACT);
    }


    private static Map<String/*FieldName*/, String/*FieldType*/> getFieldMap(VariableElement element, ProcessingEnvironment env){
        TypeElement typeElement = (TypeElement) env.getTypeUtils().asElement(element.asType());
        List<? extends Element> enclosedElements = env.getElementUtils().getAllMembers((TypeElement)typeElement.asType());
        Map<String, String> fieldMap = new HashMap<>();
        for(Element e:enclosedElements){
            if(e.getKind() == ElementKind.FIELD){
                String fieldName = e.getSimpleName().toString();
                if(fieldMap.containsKey(fieldName)){
                    continue;
                }
                fieldMap.put(fieldName, e.asType().toString());
            }
        }
        List<VariableElement> allFields = getAllFields(element);
        for(VariableElement e:allFields){
            String fieldName = e.getSimpleName().toString();
            if(fieldMap.containsKey(fieldName)){
                continue;
            }
            fieldMap.put(fieldName, e.asType().toString());
        }
        return fieldMap;

    }

    /**
     * 获取父类所有字段
     * @param element /
     * @return /
     */
    private static List<VariableElement> getAllFields(Element element){
        List<VariableElement> list = new ArrayList<>();
        if(element instanceof TypeElement){
            TypeMirror superClassType = ((TypeElement)element).getSuperclass();
            while (!superClassType.toString().equals(Object.class.getName())) {
                TypeElement superClassElement = asTypeElement(superClassType);
                for (Element enclosedElement : superClassElement.getEnclosedElements()) {
                    if (enclosedElement instanceof VariableElement) {
                        list.add((VariableElement) enclosedElement);
                    }
                }
                superClassType = superClassElement.getSuperclass();
            }
        }
        return list;
    }

    private static TypeElement asTypeElement(TypeMirror typeMirror) {
        return (TypeElement) ((DeclaredType) typeMirror).asElement();
    }

    /**
     * 方法执行器
     * @param clazzName /
     * @param targetFieldName /
     * @param sourceFieldValue /
     * @return /
     */
    public static Object executeMethod(String clazzName, String targetFieldName, Object sourceFieldValue){
        ClassUtils.init(clazzName);
        String execMethodName = clazzName + targetFieldName;
        Method excMethod = methodMap.get(execMethodName);
        Object object = clazzMap.get(clazzName);
        if(excMethod == null || object == null){
            System.err.printf("null: excMethod=%s object=%s%n", excMethod, object);
            return null;
        }
        try{
            return excMethod.invoke(object, sourceFieldValue);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void init(String name){
        if(clazzSet.contains(name)){
            return;
        }
        clazzSet.add(name);
        try{
            Class<?> clazz = Class.forName(name);
            Object object = clazz.getDeclaredConstructor().newInstance();
            clazzMap.put(name, object);
            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                String execMethodName = name + method.getName();
                methodMap.put(execMethodName, method);
            }
        }catch(Exception e){
            System.err.println("如果该类是内部类，需要开启XToolMapping#resultTypeIsInner");
            e.printStackTrace();
        }
    }


    public static String getInnerClazzName(String classFullName){
        return classFullName.substring(0, classFullName.lastIndexOf("."))
                + "$"
                + classFullName.substring(classFullName.lastIndexOf(".")+1);
    }


}
