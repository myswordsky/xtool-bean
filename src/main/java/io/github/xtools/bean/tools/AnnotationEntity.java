package io.github.xtools.bean.tools;


import io.github.xtools.bean.annotation.XToolBean;
import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.annotation.mapping.MappingIndexEnums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationEntity {
    private static final Set<String> another = new HashSet<String>(){{
        add("equals");
        add("toString");
        add("hashCode");
        add("annotationType");
    }};
    private static final Map<String, Set<String>> annotationMethodMap = new HashMap<String, Set<String>>(){{
        put(XToolBean.class.getName(), Arrays.stream(XToolBean.class.getDeclaredMethods()).map(Method::getName).filter(e->!another.contains(e))
                .collect(Collectors.toSet()));
        put(XToolMapping.class.getName(), Arrays.stream(XToolMapping.class.getDeclaredMethods()).map(Method::getName).filter(e->!another.contains(e))
                .collect(Collectors.toSet()));
    }};

    private final Map<String, AnnotationClazz> map = new HashMap<>();
    public AnnotationClazz getClassNameByMap(String clazzName, String methodName){
        return map.get(getNameKey(clazzName, methodName));
    }

    private final Map<String/*new*/, String/*old*/> annotationGenMap = new HashMap<>();
    public String getAnnotationGenMap(String clazzName, String methodName, String newAnnotation){
        return annotationGenMap.get(getNameKey(clazzName, methodName) + newAnnotation);
    }


    @SuppressWarnings("unchecked")
    public <T> T parseAnnotation(String classKey, String methodName, T clazz){
        //@io.github.xtools.bean.annotation.XToolBean(imports=io.github.user.test.BigDecimalGen,io.github.user.test.BigDecimalGenDTO, mappingControl=io.github.xtools.bean.annotation.mapping.MappingDeepClone, copyType=false, isSpring=false)
        String s = clazz.toString();
        return (T)parse(s, getNameKey(classKey, methodName), clazz);
    }

    private static String getNameKey(String classKey, String methodName){
        return classKey + "#" + methodName;
    }

    private Object parse(String oldAnnotation, String nameKey, Object clazz){
        Map<String, String> map = new HashMap<>();
        int i = oldAnnotation.indexOf("(");
        String clazzName = oldAnnotation.substring(1, i);
        String substring = oldAnnotation.substring(i + 1);
        String params = substring.substring(0, substring.length() - 1);
        Set<String> set = new HashSet<>(annotationMethodMap.get(clazzName));
        String paramKey = params.split("=")[0];
        params = params.substring(paramKey.length() + 1);
        set.remove(paramKey);
        Map<Integer, String> mapp = new TreeMap<>();
        for(String str:set){
            int index = params.indexOf(str + "=");
            if (index >= 0) {
                mapp.put(index, str);
            }
        }
        for(String group:mapp.values()){
            String beforeValue = params.substring(0, params.indexOf(group + "="));
            map.put(paramKey, beforeValue.substring(0, beforeValue.length() - 2));
            paramKey = group;
            params = params.substring(beforeValue.length() + paramKey.length() + 1);
        }
        map.put(paramKey, params);
        //System.err.println("map:" + map);
        if(XToolBean.class.getName().equals(clazzName)){
            XToolBean xToolBean = getXToolBeanAnnotation(map, nameKey);
            //初始化map
            Class<?>[] imports = xToolBean.imports();
            Class<?> aClass = xToolBean.extendsClazz();
            return xToolBean;
        }else if(XToolMapping.class.getName().equals(clazzName)){
            String newNameKey = nameKey + oldAnnotation;
            XToolMapping xToolMapping = getXToolMappingAnnotation(map, newNameKey);
            Class<?> aClass = xToolMapping.resultType();
            annotationGenMap.put(nameKey + xToolMapping, oldAnnotation);
            return xToolMapping;
        }else{
            return null;
        }
    }

    public static void main(String[] args) {
//        String s = "@io.github.xtools.bean.annotation.XToolBean(mappingControl=io.github.xtools.bean.annotation.mapping.MappingDeepClone, isAutoGen=false)";
//        XToolBean o = (XToolBean)parseAnnotation(s, "a", null);
//        assert o != null;
        //System.err.println(o.mappingControl() == MappingDeepClone.class);

    }

    private XToolMapping getXToolMappingAnnotation(Map<String, String> paramMap, String nameKey){
        return new XToolMapping(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return XToolMapping.class;
            }

            @Override
            public String target() {
                return paramMap.get("target");
            }

            @Override
            public String source() {
                return paramMap.get("source");
            }

            @Override
            public MappingIndexEnums index() {
                String index = paramMap.get("index");
                if(MappingIndexEnums.First.name().equals(index)){
                    return MappingIndexEnums.First;
                }else if (MappingIndexEnums.Second.name().equals(index)){
                    return MappingIndexEnums.Second;
                }else{
                    return MappingIndexEnums.Default;
                }
            }

            @Override
            public String defaultValue() {
                return paramMap.get("defaultValue");
            }

            @Override
            public String targetValue() {
                return paramMap.get("targetValue");
            }

            @Override
            public String dateFormat() {
                return paramMap.get("dateFormat");
            }

            @Override
            public String timeZone() {
                return paramMap.get("timeZone");
            }

            @Override
            public String numberFormat() {
                return paramMap.get("numberFormat");
            }

            @Override
            public boolean ignore() {
                return "true".equalsIgnoreCase(paramMap.get("ignore"));
            }

            @Override
            public boolean copyType() {
                return "true".equalsIgnoreCase(paramMap.get("copyType"));
            }

            @Override
            public boolean jsonMapping() {
                return "true".equalsIgnoreCase(paramMap.get("jsonMapping"));
            }

            @Override
            public Class<?> resultType() {
                String resultType = paramMap.get("resultType");
                if("void".equals(resultType)){
                    return void.class;
                }
                map.putIfAbsent(nameKey, new AnnotationClazz());
                map.get(nameKey).setResultType(resultType);
                return void.class;
            }

            @Override
            public boolean resultTypeIsInner() {
                return "true".equalsIgnoreCase(paramMap.get("resultTypeIsInner"));
            }

            @Override
            public Class<?> beforeOrAfterHandle() {
                String resultType = paramMap.get("beforeOrAfterHandle");
                Class<?> type = getClazz(resultType);
                if(type != void.class){
                    map.putIfAbsent(nameKey, new AnnotationClazz());
                    map.get(nameKey).setBeforeOrAfterHandle(resultType);
                }
                return type;
            }
        };
    }

    private XToolBean getXToolBeanAnnotation(Map<String, String> paramMap, String nameKey){
        return new XToolBean(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return XToolBean.class;
            }
            @Override
            public boolean isSpring() {
                return "true".equalsIgnoreCase(paramMap.get("isSpring"));
            }
            @Override
            public boolean copyType() {
                return "true".equalsIgnoreCase(paramMap.get("copyType"));
            }
            @Override
            public Class<?> mappingControl() {
                return getClazz(paramMap.get("mappingControl"));
            }
            @Override
            public Class<?>[] imports() {
                //{} == ""
                String imports = paramMap.get("imports");
                if(StringLangUtils.isEmpty(imports)){
                    return new Class[0];
                }
                map.putIfAbsent(nameKey, new AnnotationClazz());
                map.get(nameKey).setImports(Arrays.stream(imports.split(",")).map(String::trim).collect(Collectors.toList()));
                return new Class[0];
            }
            @Override
            public Class<?> extendsClazz() {
                String resultType = paramMap.get("extendsClazz");
                if("void".equals(resultType)){
                    return void.class;
                }
                map.putIfAbsent(nameKey, new AnnotationClazz());
                map.get(nameKey).setExtendsClazz(resultType);
                return void.class;
            }
        };
    }

    private static Class<?> getClazz(String clazzName){
        try{
            if(clazzName == null || "void".equals(clazzName)){
                return void.class;
            }
            return Class.forName(clazzName);
        }catch(Exception ignored){
        }
        return void.class;
    }

    public static class AnnotationClazz {
        private String resultType;
        private String beforeOrAfterHandle;
        private List<String> imports;
        private String extendsClazz;

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public String getBeforeOrAfterHandle() {
            return beforeOrAfterHandle;
        }

        public void setBeforeOrAfterHandle(String beforeOrAfterHandle) {
            this.beforeOrAfterHandle = beforeOrAfterHandle;
        }

        public List<String> getImports() {
            return imports;
        }

        public void setImports(List<String> imports) {
            this.imports = imports;
        }

        public String getExtendsClazz() {
            return extendsClazz;
        }

        public void setExtendsClazz(String extendsClazz) {
            this.extendsClazz = extendsClazz;
        }
    }

}
