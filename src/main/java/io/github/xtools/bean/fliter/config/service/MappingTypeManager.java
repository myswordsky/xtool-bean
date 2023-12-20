package io.github.xtools.bean.fliter.config.service;


import java.util.*;

public class MappingTypeManager {
    private static final Set<String> baseEnumsSet = new HashSet<>();
    private static final Map<String, IMappingTypeService> map = new HashMap<String, IMappingTypeService>(){{
        for(FieldType enums:FieldType.values()){
            try {
                IMappingTypeService service = (IMappingTypeService) Class.forName(enums.getService().getName()).getDeclaredConstructor().newInstance();
                put(enums.name().toLowerCase(), service);
                put(enums.getType().getName(), service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<FieldType> fieldTypes = Arrays.asList(FieldType.Boolean, FieldType.Byte, FieldType.Short, FieldType.Int, FieldType.Long
                , FieldType.Float, FieldType.Double);
        for(FieldType enums:fieldTypes){
            baseEnumsSet.add(enums.name().toLowerCase());
        }
    }};

    public static IMappingTypeService getService(String fieldType){
        return map.get(fieldType);
    }

    /**
     * 是否是基础类型
     * @param fieldType /
     * @return /
     */
    public static boolean isBaseEnums(String fieldType){
        return baseEnumsSet.contains(fieldType);
    }

    /**
     * 是否同类型(包装类型视为同类型)
     * @param sourceFieldType /
     * @param targetFieldType /
     * @return /
     */
    public static boolean isSameType(String sourceFieldType, String targetFieldType) {
        if(sourceFieldType.equals(targetFieldType)){
            return true;
        }
        if(isBaseEnums(sourceFieldType) || isBaseEnums(targetFieldType)){
            String[] source = sourceFieldType.split("\\.");
            String[] target = targetFieldType.split("\\.");
            return source[source.length - 1].equals(target[target.length - 1]);
        }
        return false;
    }
}
