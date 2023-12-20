package io.github.xtools.bean.fliter.config.service;


import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;

public interface IMappingTypeService {

    /**
     * sourceFieldType.handle
     *
     * @param filer /
     * @param entity /
     * @return /
     */
    String handle(AbstractFiler filer, MethodEntity entity);

    /**
     * 外部扩展实现
     *
     * @return /
     */
    Class<?> extendImpl();

}
