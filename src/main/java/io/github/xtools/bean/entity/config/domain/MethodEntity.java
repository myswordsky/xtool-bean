package io.github.xtools.bean.entity.config.domain;


import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.tools.CommonUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class MethodEntity {

    VariableElement source;
    VariableElement target;

    String sourceFieldName;
    String sourceFieldType;

    String targetFieldName;
    String targetFieldType;

    String nullString;
    ExecutableElement method;
    XToolMapping xToolMapping;
    boolean deepClone;




    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public String getSourceFieldType() {
        return sourceFieldType;
    }

    public void setSourceFieldType(String sourceFieldType) {
        this.sourceFieldType = sourceFieldType;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }

    public String getTargetFieldType() {
        return targetFieldType;
    }

    public void setTargetFieldType(String targetFieldType) {
        this.targetFieldType = targetFieldType;
    }

    public VariableElement getSource() {
        return source;
    }

    public void setSource(VariableElement source) {
        this.source = source;
    }

    public VariableElement getTarget() {
        return target;
    }

    public void setTarget(VariableElement target) {
        this.target = target;
    }

    public String getNullString() {
        return nullString;
    }

    public void setNullString(String nullString) {
        this.nullString = nullString;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public void setMethod(ExecutableElement method) {
        this.method = method;
    }

    public XToolMapping getXToolMapping() {
        return xToolMapping;
    }

    public void setXToolMapping(XToolMapping xToolMapping) {
        this.xToolMapping = xToolMapping;
    }

    public boolean isDeepClone() {
        return deepClone;
    }

    public void setDeepClone(boolean deepClone) {
        this.deepClone = deepClone;
    }

    public String getSourceFieldNameUp() {
        return CommonUtils.getStartStringUp(sourceFieldName);
    }

    public String getTargetFieldNameUp() {
        return CommonUtils.getStartStringUp(targetFieldName);
    }
}
