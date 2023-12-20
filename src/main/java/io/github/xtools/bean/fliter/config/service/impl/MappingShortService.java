package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

public class MappingShortService extends MappingBaseService implements IMappingTypeService {

    @Override
    public String handle(AbstractFiler filer, MethodEntity entity) {
        String targetFieldType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        //类型转化
        String statement;
        if(isType(FieldType.Int, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Int, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(Integer.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else if(isType(FieldType.String, targetFieldType)){
            statement = getDealOrNull(filer, entity
                    , "target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));");
        }else{
            statement = getTypeGenString(entity);
        }
        return statement;
    }


}
