package io.github.xtools.bean.interfaces;

public interface BaseMapping<E, D> {

    /**
     * DTO转Entity
     */
    E toEntity(D dto);

    /**
     * Entity转DTO
     */
    D toDto(E entity);

    /**
     * entity自身Copy
     */
    E thisEntity(E entity);

    /**
     * Dto自身Copy
     */
    D thisDto(D dto);
}
