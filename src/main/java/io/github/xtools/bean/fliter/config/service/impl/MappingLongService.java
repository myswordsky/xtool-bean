package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

import java.util.Date;

public class MappingLongService extends MappingBaseService implements IMappingTypeService {

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
                    , "target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()).intValue());");
        }else if(isType(FieldType.Double, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Double, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()).doubleValue());");
        }else if(isType(FieldType.Date, targetFieldType)){
            statement = getDealOrNull(filer, entity
                    , "target.set" + targetFieldNameUp + "(new Date(source.get" + sourceFieldNameUp + "()));");
            filer.getImportList().add(Date.class);
        }else{
            statement = getTypeGenString(entity);
        }
        return statement;
    }


}
