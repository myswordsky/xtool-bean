package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

public class MappingStringService extends MappingBaseService implements IMappingTypeService {

    @Override
    public String handle(AbstractFiler filer, MethodEntity entity) {
        String targetFieldType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        //类型转化
        String statement;
        if(isType(FieldType.Long, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Long, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Long.parseLong(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Double, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Double, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Double.parseDouble(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Float, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Float, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Float.parseFloat(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Int, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Int, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Integer.parseInt(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Short, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Short, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Short.parseShort(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Byte, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Byte, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Byte.parseByte(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Boolean, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Boolean, targetFieldType)
                    , "target.set" + targetFieldNameUp
                            + "(String.valueOf(source.get" + sourceFieldNameUp + "()).equals(Boolean.TRUE.toString()) ? Boolean.TRUE :  Boolean.FALSE);");
        }else if(isType(FieldType.Date, targetFieldType)){
            statement = getDateOrNull(filer, entity, false);
        }else{
            if(entity.getTargetFieldType().equals(entity.getSourceFieldType())){
                statement = buildBaseStatement(targetFieldNameUp, sourceFieldNameUp);
            }else{
                XToolMapping xToolMapping = entity.getXToolMapping();
                if(xToolMapping != null && xToolMapping.jsonMapping()){
                    filer.getImportList().add(com.alibaba.fastjson.JSON.class);
                    filer.getImportList().add(com.alibaba.fastjson.TypeReference.class);
                    statement = getDealOrNull(filer, entity
                                , "target.set" + targetFieldNameUp + "(JSON.parseObject(source.get" + sourceFieldNameUp
                                + "(), new TypeReference<" + entity.getTargetFieldType() + ">() {}));");
                }else{
                    statement = "";//不支持BigDecimal等
                }
            }
        }
        return statement;
    }


}
