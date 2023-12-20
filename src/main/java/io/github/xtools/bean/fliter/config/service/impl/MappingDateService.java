package io.github.xtools.bean.fliter.config.service.impl;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;

import java.util.Date;

public class MappingDateService extends MappingBaseService implements IMappingTypeService {

    @Override
    public String handle(AbstractFiler filer, MethodEntity entity) {
        String targetFieldType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        //类型转化
        String statement;
        if(isType(FieldType.Long, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Long, targetFieldType)
                    , "target.set" + targetFieldNameUp + "(source.get" + sourceFieldNameUp + "().getTime());");
        }else if(isType(FieldType.Int, targetFieldType)){
            statement = getDealOrNull(filer, entity, isBaseType(FieldType.Int, targetFieldType)
                    , "target.set" + targetFieldNameUp + "((int) (source.get" + sourceFieldNameUp + "().getTime() / 1000));");
        }else if(isType(FieldType.String, targetFieldType)){
            statement = getDateOrNull(filer, entity, true);
        }else if(isType(FieldType.Date, targetFieldType)){
            if(entity.isDeepClone()){//自身类型处理
                filer.getImportList().add(Date.class);
                statement = getDealOrNull(filer, entity
                        , "target.set" + targetFieldNameUp + "(new Date(source.get" + sourceFieldNameUp + "().getTime()));");
            }else{
                statement = buildBaseStatement(targetFieldNameUp, sourceFieldNameUp);
            }
        }else{
            statement = getTypeGenString(entity);
        }
        return statement;
    }

}
