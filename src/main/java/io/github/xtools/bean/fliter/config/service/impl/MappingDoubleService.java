package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

import java.math.BigDecimal;

public class MappingDoubleService extends MappingBaseService implements IMappingTypeService {

    @Override
    public String handle(AbstractFiler filer, MethodEntity entity) {
        String targetFieldType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        //类型转化
        String statement;
        if(isType(FieldType.String, targetFieldType)){
            statement = getNumberFormat(filer, entity, targetFieldNameUp, sourceFieldNameUp);
        }else if(isType(FieldType.Int, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Int, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).intValue());");
        }else if(isType(FieldType.Float, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Float, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).floatValue());");
        }else if(isType(FieldType.Long, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Long, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).longValue());");
        }else if(isType(FieldType.BigDecimal, targetFieldType)){
            filer.getImportList().add(BigDecimal.class);
            statement = getDealOrNull(filer, entity
                    , "target.set" + targetFieldNameUp + "(BigDecimal.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else{
            statement = getTypeGenString(entity);
        }
        return statement;
    }


}
