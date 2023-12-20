package io.github.xtools.bean.entity.groovy;

import com.squareup.javapoet.ClassName;

/**
 * 映射类配置
 *
 * @author xzb
 */
@SuppressWarnings({"UnusedReturnValue"})
public class MappingClassGroovyConfig {

    private String packageGenName;

    private final Class<?> entityClass;
    private final Class<?> dtoClass;


    public MappingClassGroovyConfig(Class<?> entityClass, Class<?> dtoClass, String packageGenName) {
        this.packageGenName = packageGenName;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public ClassName entity() {
        return ClassName.get(this.getEntityClass().getPackage().getName(), this.getEntityClass().getSimpleName());
    }
    public ClassName dto() {
        return ClassName.get(this.getDtoClass().getPackage().getName(), this.getDtoClass().getSimpleName());
    }


    public MappingClassGroovyConfig setPackageGenName(String packageGenName){
        this.packageGenName = packageGenName;
        return this;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Class<?> getDtoClass() {
        return dtoClass;
    }

    public String getPackageGenName() {
        return packageGenName;
    }
}