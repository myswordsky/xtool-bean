package io.github.xtools.bean.entity.groovy;


import java.lang.reflect.Field;

public class MethodGroovyEntity {
    /**
     * 字段Filed类型
     */
    private Class<?> targetFieldType;
    private Field sourceField;
    private Field targetField;
    /**
     * 目标类字段头部大写
     */
    private String targetFieldNameUp;
    /**
     * 源类字段头部大写
     */
    private String sourceFieldNameUp;
    /**
     * 目标类字段
     */
    private String targetFieldName;

    private Class<?> source;
    private Class<?> target;



    /**
     * source是否是基本类型
     */
    private boolean sourceIsBaseType;

    private String nullString;

    public Class<?> getTargetFieldType() {
        return targetFieldType;
    }

    public void setTargetFieldType(Class<?> targetFieldType) {
        this.targetFieldType = targetFieldType;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public void setSourceField(Field sourceField) {
        this.sourceField = sourceField;
    }

    public Field getTargetField() {
        return targetField;
    }

    public void setTargetField(Field targetField) {
        this.targetField = targetField;
    }

    public String getTargetFieldNameUp() {
        return targetFieldNameUp;
    }

    public void setTargetFieldNameUp(String targetFieldNameUp) {
        this.targetFieldNameUp = targetFieldNameUp;
    }

    public String getSourceFieldNameUp() {
        return sourceFieldNameUp;
    }

    public void setSourceFieldNameUp(String sourceFieldNameUp) {
        this.sourceFieldNameUp = sourceFieldNameUp;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }

    public Class<?> getSource() {
        return source;
    }

    public void setSource(Class<?> source) {
        this.source = source;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public boolean isSourceIsBaseType() {
        return sourceIsBaseType;
    }

    public void setSourceIsBaseType(boolean sourceIsBaseType) {
        this.sourceIsBaseType = sourceIsBaseType;
    }

    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }
}
