package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

import java.util.Date;

public class MappingIntegerService extends MappingBaseService implements IMappingTypeService {

    @Override
    public String handle(AbstractFiler filer, MethodEntity entity) {
        String targetFieldType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        //类型转化
        String statement;
        if(isType(FieldType.String, targetFieldType)){
            statement = getNumberFormat(filer, entity, targetFieldNameUp, sourceFieldNameUp);
        }else if(isType(FieldType.Long, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Long, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Double, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Double, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Float, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Float, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Float.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.Short, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Short, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Integer.valueOf(source.get" + sourceFieldNameUp + "()).shortValue());");
        }else if(isType(FieldType.Byte, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Byte, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Integer.valueOf(source.get" + sourceFieldNameUp + "()).byteValue());");
        }else if(isType(FieldType.Boolean, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Boolean, targetFieldType)
                    , "target.set" + targetFieldNameUp
                            + "(source.get" + sourceFieldNameUp + "() == 1 ? Boolean.TRUE : source.get" + sourceFieldNameUp + "() == 0 ? Boolean.FALSE : Boolean.TRUE);");
        }else if(isType(FieldType.Date, targetFieldType)){
            statement = getDealOrNull(filer, entity
                    , "target.set" + targetFieldNameUp + "(new Date(source.get" + sourceFieldNameUp + "() * 1000L));");
            filer.getImportList().add(Date.class);
        }else{
            statement = getTypeGenString(entity);
        }
        return statement;
    }


}
