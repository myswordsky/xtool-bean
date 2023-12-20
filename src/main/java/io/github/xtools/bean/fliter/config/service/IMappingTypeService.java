package io.github.xtools.bean.fliter.config.service;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;

public interface IMappingTypeService {

    /**
     * sourceFieldType.handle
     */
    String handle(AbstractFiler filer, MethodEntity entity);

    /**
     * 外部扩展实现
     */
    Class<?> extendImpl();

}
