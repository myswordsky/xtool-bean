package io.github.xtools.bean.fliter.groovy;

import io.github.xtools.bean.data.BeanCopyLocalGroovyData;
import io.github.xtools.bean.entity.groovy.MappingClassGroovyConfig;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGroovy {
    public static final String Import = "import ";
    public static final String Package = "package ";
    public static final String List = "java.util.List ";
    public static final String ArrayList = "java.util.ArrayList ";
    public static final String BaseMappingPackageName = "io.github.xtools.bean.interfaces.BaseMapping";
    public static final String BaseMappingName = "BaseMapping";
    public static final String CopyImpl = "CopyImpl";
    public static final String Override = "@Override";
    public static final String publicName = "public ";
    public static final String FieldImportList = "$FieldImportList$";
    protected MappingClassGroovyConfig config;
    /**
     * 生成的文件包名
     */
    protected String packageName;
    /**
     * 生成的文件类名
     */
    protected String className;
    /**
     * 额外导入的类
     */
    protected Set<String> importList = new HashSet<>();

    protected String comment;

    public AbstractGroovy(MappingClassGroovyConfig config) {
        this.config = config;
        BeanCopyLocalGroovyData.putClassMap(config.getEntityClass());
        BeanCopyLocalGroovyData.putClassMap(config.getDtoClass());
        this.packageName = config.getPackageGenName();
        this.className = config.getEntityClass().getSimpleName() + "BeanCopyImpl";
        this.comment = "BeanCopyUtils";
    }

    public MappingClassGroovyConfig getConfig() {
        return config;
    }

    public Set<String> getImportList() {
        return importList;
    }

    public String getPackageName() {
        return packageName;
    }
}