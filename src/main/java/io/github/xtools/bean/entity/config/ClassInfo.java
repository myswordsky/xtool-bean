package io.github.xtools.bean.entity.config;


import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;
import java.util.stream.Collectors;

public class ClassInfo {
    String className;               //类名 ClassInfo
    Name qualifiedName;             //包节点   io.github.annotation.util.ClassInfo
    String packageName;             //包名    io.github.annotation.util
    Set<Modifier> modifiers;        //获取类的修饰符
    List<VariableElement> fields;   //获取类的所有字段
    List<ExecutableElement> methods;//获取类的所有方法
    List<TypeElement> innerClass;   //内部类
    TypeMirror superclass;          //获取类的父类
    List<? extends TypeMirror> interfaceTypes;// 获取类实现的接口
    Map<String, ParamsMethodField> methodMap;
    TypeElement classElement;


    public static ClassInfo buildClassInfo(ProcessingEnvironment env, TypeElement classElement){
        String className = classElement.getSimpleName().toString();
        Name qualifiedName = classElement.getQualifiedName();
        String packageName = qualifiedName.toString().replace("." + className, "");
        Set<Modifier> modifiers = classElement.getModifiers();
        List<VariableElement> fields = new ArrayList<>();

        Map<String, ParamsMethodField> methodMap = new LinkedHashMap<>();
        List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());
        for(ExecutableElement method:methods){
            ParamsMethodField params = new ParamsMethodField();
            for(VariableElement var:method.getParameters()){
                List<Map<String, String>> fieldTypeList = params.getFieldTypeList();
                Map<String, String> map = new LinkedHashMap<>();
                TypeElement parameterTypeElement = (TypeElement) env.getTypeUtils().asElement(var.asType());
                for (VariableElement field : ElementFilter.fieldsIn(parameterTypeElement.getEnclosedElements())) {
                    map.put(field.getSimpleName().toString(), field.asType().toString());
                }
                //父类处理
                TypeMirror superClassType = parameterTypeElement.getSuperclass();
                while (!superClassType.toString().equals(Object.class.getName())) {
                    TypeElement superClassElement = asTypeElement(superClassType);
                    for (Element enclosedElement : superClassElement.getEnclosedElements()) {
                        if (enclosedElement instanceof VariableElement) {
                            map.put(enclosedElement.getSimpleName().toString(), enclosedElement.asType().toString());
                        }
                    }
                    superClassType = superClassElement.getSuperclass();
                }
                fieldTypeList.add(map);
            }
            methodMap.put(method.toString(), params);
        }

        //内部类
        List<TypeElement> innerClass = new ArrayList<>();
        //静态字段，静态方法同理
        List<VariableElement> staticFields = fields.stream()
                .filter(field -> field.getKind() == ElementKind.FIELD && field.getModifiers().contains(Modifier.STATIC))
                .collect(Collectors.toList());
        // 获取类的直接成员（字段、方法、内部类等）
        for(Element element:classElement.getEnclosedElements()){
            if(element instanceof TypeElement){
                innerClass.add((TypeElement)element);
            }
            if(element instanceof VariableElement){
                fields.add((VariableElement)element);
            }
            if(element instanceof ExecutableElement){
                //methods.add((ExecutableElement)element);
            }
        }
        TypeMirror superclass = classElement.getSuperclass();
        List<? extends TypeMirror> interfaceTypes = classElement.getInterfaces();


        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(className);
        classInfo.setQualifiedName(qualifiedName);
        classInfo.setPackageName(packageName);
        classInfo.setModifiers(modifiers);
        classInfo.setFields(fields);
        classInfo.setMethods(methods);
        classInfo.setInnerClass(innerClass);
        classInfo.setSuperclass(superclass);
        classInfo.setInterfaceTypes(interfaceTypes);
        classInfo.setMethodMap(methodMap);
        classInfo.setClassElement(classElement);
        return classInfo;
    }

    public static class ParamsMethodField {
        private final List<Map<String, String>> fieldTypeMap = new ArrayList<>();

        public List<Map<String, String>> getFieldTypeList() {
            return fieldTypeMap;
        }
    }

    private static TypeElement asTypeElement(TypeMirror typeMirror) {
        return (TypeElement) ((DeclaredType) typeMirror).asElement();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Name getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(Name qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public List<VariableElement> getFields() {
        return fields;
    }

    public void setFields(List<VariableElement> fields) {
        this.fields = fields;
    }

    public List<ExecutableElement> getMethods() {
        return methods;
    }

    public void setMethods(List<ExecutableElement> methods) {
        this.methods = methods;
    }

    public List<TypeElement> getInnerClass() {
        return innerClass;
    }

    public void setInnerClass(List<TypeElement> innerClass) {
        this.innerClass = innerClass;
    }

    public TypeMirror getSuperclass() {
        return superclass;
    }

    public void setSuperclass(TypeMirror superclass) {
        this.superclass = superclass;
    }

    public List<? extends TypeMirror> getInterfaceTypes() {
        return interfaceTypes;
    }

    public void setInterfaceTypes(List<? extends TypeMirror> interfaceTypes) {
        this.interfaceTypes = interfaceTypes;
    }

    public Map<String, ParamsMethodField> getMethodMap() {
        return methodMap;
    }

    public void setMethodMap(Map<String, ParamsMethodField> methodMap) {
        this.methodMap = methodMap;
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    public void setClassElement(TypeElement classElement) {
        this.classElement = classElement;
    }
}
