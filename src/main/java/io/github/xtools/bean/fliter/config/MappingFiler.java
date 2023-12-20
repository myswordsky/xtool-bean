package io.github.xtools.bean.fliter.config;

import io.github.xtools.bean.annotation.XToolBean;
import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.annotation.mapping.MappingCopyType;
import io.github.xtools.bean.annotation.mapping.MappingDeepClone;
import io.github.xtools.bean.annotation.mapping.MappingIndexEnums;
import io.github.xtools.bean.entity.config.XToolBeanConfig;
import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.service.FieldType;
import io.github.xtools.bean.fliter.config.service.IMappingTypeService;
import io.github.xtools.bean.fliter.config.service.MappingBaseService;
import io.github.xtools.bean.fliter.config.service.MappingTypeManager;
import io.github.xtools.bean.interfaces.MappingUtils;
import io.github.xtools.bean.tools.AnnotationEntity;
import io.github.xtools.bean.tools.ClassUtils;
import io.github.xtools.bean.tools.CommonUtils;
import io.github.xtools.bean.tools.StringLangUtils;
import com.squareup.javawriter.JavaWriter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * 生成Entity对应的Mapper类
 *
 * @author xzb
 */
public class MappingFiler extends AbstractFiler {
    private static final String SOURCE = "source";
    private boolean deepClone = false;
    private boolean entityFirst;

    public MappingFiler(XToolBeanConfig config) {
        super(config);
    }

    @Override
    public void build(JavaWriter jw, String classSimpleName, ExecutableElement method, VariableElement entity, VariableElement dto, boolean entityIsFirst) throws IOException {
        entityFirst = entityIsFirst;
        String entitySimpleName = CommonUtils.getSimpleName(entity.asType().toString());
        String dtoSimpleName = CommonUtils.getSimpleName(dto.asType().toString());
        String toEntity = toEntity(method, entity, dto, entityIsFirst);
        String toDto = toDto(method, entity, dto, entityIsFirst);
        String thisEntity = thisEntity(method, entity);
        String thisDto = thisDto(method, dto);
        //导入代码添加的类、注解类
        jw.emitImports(importList.toArray(new Class<?>[0]));
        String implName = getImplName(entitySimpleName, dtoSimpleName);
        XToolBean xToolBean = getConfig().getXToolBean();
        if(xToolBean.isSpring()){
            jw.emitImports("org.springframework.stereotype.Component");
            jw.emitAnnotation("Component");
        }
        AnnotationEntity.AnnotationClazz clazz = annotationEntity.getClassNameByMap(classInfo.getQualifiedName().toString(), "");
        String extendsType = null;
        if(clazz != null){
            //生成类额外导入的类
            if(clazz.getImports() != null){
                jw.emitImports(clazz.getImports());
            }
            //需要继承的类
            String extendsClazz = clazz.getExtendsClazz();
            if(extendsClazz != null){
                extendsType = extendsClazz.substring(extendsClazz.lastIndexOf(".")+1);
                jw.emitImports(extendsClazz);
            }
        }
        //类头
        jw.beginType(classSimpleName, "class", EnumSet.of(Modifier.PUBLIC), extendsType, implName);
        //类方法
        jw.beginMethod(entitySimpleName, "toEntity", EnumSet.of(Modifier.PUBLIC), dtoSimpleName, SOURCE)
                .emitStatement(toEntity)
                .endMethod();
        jw.beginMethod(dtoSimpleName, "toDto", EnumSet.of(Modifier.PUBLIC), entitySimpleName, SOURCE)
                .emitStatement(toDto)
                .endMethod();
        jw.beginMethod(entitySimpleName, "thisEntity", EnumSet.of(Modifier.PUBLIC), entitySimpleName, SOURCE)
                .emitStatement(thisEntity)
                .endMethod();
        jw.beginMethod(dtoSimpleName, "thisDto", EnumSet.of(Modifier.PUBLIC), dtoSimpleName, SOURCE)
                .emitStatement(thisDto)
                .endMethod();
//            jw.beginMethod("Object", "deepCopy", EnumSet.of(Modifier.PUBLIC), "Object", "object")
//                    .emitStatement(buildDeepClone())
//                    .endMethod();
    }

    private String toEntity(ExecutableElement method, VariableElement entity, VariableElement dto, boolean entityIsFirst) {
        return getServiceContent(method, entity, dto, entityIsFirst, false);
    }

    private String toDto(ExecutableElement method, VariableElement entity, VariableElement dto, boolean entityIsFirst) {
        return getServiceContent(method, dto, entity, !entityIsFirst, false);
    }

    private String thisEntity(ExecutableElement method, VariableElement entity) {
        return getServiceContent(method, entity, entity, true, true);
    }

    private String thisDto(ExecutableElement method, VariableElement dto) {
        return getServiceContent(method, dto, dto, true, false);
    }

    private String getServiceContent(ExecutableElement method, VariableElement target, VariableElement source, boolean targetIsFirst, boolean isEntity){
        XToolBean xToolBean = getConfig().getXToolBean();
        List<Map<String, String>> fieldTypeList = getConfig().getClassInfo().getMethodMap().get(method.toString()).getFieldTypeList();
        Map<String, String> targetFieldMap;
        Map<String, String> sourceFieldMap;
        if(target.asType().toString().equals(source.asType().toString())){
            if(isEntity){
                targetFieldMap = entityFirst ? fieldTypeList.get(0) : fieldTypeList.get(1);
            }else{
                targetFieldMap = entityFirst ? fieldTypeList.get(1) : fieldTypeList.get(0);
            }
            sourceFieldMap = targetFieldMap;
        }else{
            targetFieldMap = targetIsFirst ? fieldTypeList.get(0) : fieldTypeList.get(1);
            sourceFieldMap = targetIsFirst ? fieldTypeList.get(1) : fieldTypeList.get(0);
        }

        Map<String/*target*/, Map<MappingIndexEnums, XToolMapping>> mappingMap = mappingAnnotationMap;
        boolean methodDeepClone = isMethodAnnoDeepClone(method);
        MappingCopyType copyType = method.getAnnotation(MappingCopyType.class);
        deepClone = xToolBean.mappingControl() == MappingDeepClone.class || methodDeepClone;//是否深克隆

        StringBuilder sb = new StringBuilder();
        sb.append(assertNull(target));
        for(Map.Entry<String, String> entry : targetFieldMap.entrySet()){
            String targetFieldName = entry.getKey();
            String targetFieldType = entry.getValue();
            String searchName = targetFieldName;

            Map<MappingIndexEnums, XToolMapping> map = mappingMap.get(targetFieldName);
            //处理指定对象
            XToolMapping xToolMapping = map == null ? null
                    : map.get(MappingIndexEnums.Default) != null ? map.get(MappingIndexEnums.Default)
                    : targetIsFirst ? map.get(MappingIndexEnums.First) : map.get(MappingIndexEnums.Second);
            if(xToolMapping != null){
                //处理指定值
                if(StringLangUtils.isNotEmpty(xToolMapping.source())){
                    searchName = xToolMapping.source();
                }
                String finalSearchName = searchName;

                //处理方法执行器
                String annotationGenMap = annotationEntity.getAnnotationGenMap(classInfo.getQualifiedName().toString(), method.toString(), xToolMapping.toString());
                AnnotationEntity.AnnotationClazz clazz = annotationEntity.getClassNameByMap(classInfo.getQualifiedName().toString(), method + annotationGenMap);
                if(clazz != null && clazz.getResultType() != null){
                    Map.Entry<String, String> entrySource = sourceFieldMap.entrySet().stream()
                            .filter(s -> s.getKey().equals(finalSearchName)).findFirst().orElse(null);
                    if(entrySource != null){
                        getImportList().add(ClassUtils.class);
                        String getString = MappingBaseService.isBaseType(FieldType.Boolean, entrySource.getValue()) ? "is" :"get";
                        String resultType = clazz.getResultType();
                        String resultClazzType = xToolMapping.resultTypeIsInner() ? ClassUtils.getInnerClazzName(resultType) : resultType;
                        String defaultString = "target.set" + CommonUtils.getStartStringUp(targetFieldName)
                                + "(("+ targetFieldType +")ClassUtils.executeMethod" +
                                "(\"" + resultClazzType + "\", \"" + targetFieldName +"\", source." + getString + CommonUtils.getStartStringUp(entrySource.getKey()) + "()));";
                        sb.append("\t").append(defaultString).append("\n");
                        continue;
                    }
                }
                //处理目标值
                String targetValue = xToolMapping.targetValue();
                if(!targetValue.equals(XToolMapping.XToolMapping_targetValue)){
                    String defaultString = "target.set" + CommonUtils.getStartStringUp(targetFieldName) + "(" + targetValue + ");";
                    sb.append(defaultString).append("\n");
                    continue;
                }
                //处理忽略值
                if(xToolMapping.ignore()){
                    continue;
                }
            }

            String finalSearchName = searchName;
            sourceFieldMap.entrySet().stream().filter(e->e.getKey().equals(finalSearchName)).findFirst().ifPresent(sourceField-> {
                String sourceFieldType = sourceField.getValue();
                //处理不同类型
                boolean thisSameType = MappingTypeManager.isSameType(sourceFieldType, targetFieldType);
                if(!xToolBean.copyType() && !thisSameType){
                    return;
                }
                if(!thisSameType){
                    if(copyType == null){
                        if(xToolMapping != null && !xToolMapping.copyType()){//字段Mapping不支持
                            return;
                        }
                    }else{
                        boolean supportType = copyType.value();
                        if(!supportType){//方法不支持
                            return;
                        }
                        if(xToolMapping != null && !xToolMapping.copyType()){//方法支持且字段Mapping不支持
                            return;
                        }
                    }
                }

                MethodEntity methodEntity = new MethodEntity();
                methodEntity.setSourceFieldName(sourceField.getKey());
                methodEntity.setSourceFieldType(sourceFieldType);
                methodEntity.setTargetFieldName(targetFieldName);
                methodEntity.setTargetFieldType(targetFieldType);
                methodEntity.setSource(source);
                methodEntity.setTarget(target);
                methodEntity.setMethod(method);
                methodEntity.setNullString(getNullString(methodEntity.getTargetFieldNameUp()));
                methodEntity.setXToolMapping(xToolMapping);
                methodEntity.setDeepClone(deepClone);
                //本地实现
                IMappingTypeService service = MappingTypeManager.getService(sourceFieldType);
                if(service == null){
                    handleObject(sb, methodEntity);
                    return;
                }
                sb.append(service.handle(this, methodEntity)).append("\n");
            });
            //最后处理默认值
            if(xToolMapping != null){
                String defaultValue = xToolMapping.defaultValue();
                if(!defaultValue.equals(XToolMapping.XToolMapping_defaultValue)){
                    String startStringUp = CommonUtils.getStartStringUp(targetFieldName);
                    String defaultString =
                            "if (target." + MappingBaseService.genGetString(targetFieldType) + startStringUp + "() != null) {" + "\n" +
                            "    " + "target.set" + startStringUp + "(" + defaultValue + ");" + "\n" +
                            "}";
                    sb.append(defaultString).append("\n");
                }
            }
        }
        sb.append("return target");
        return sb.toString();
    }


    /**
     * 处理对象
     */
    private void handleObject(StringBuilder sb, MethodEntity methodEntity) {
        try{
            String targetFieldNameUp = methodEntity.getTargetFieldNameUp();
            String sourceFieldNameUp = methodEntity.getSourceFieldNameUp();
            String result;
            if(methodEntity.getTargetFieldType().equals(methodEntity.getSourceFieldType())){
                if(methodEntity.isDeepClone()){
                    getImportList().add(MappingUtils.class);
                    result = MappingBaseService.getDealOrNull(this, methodEntity, null,
                            "target.set" + targetFieldNameUp + "((" +
                                    methodEntity.getSourceFieldType() + ")MappingUtils.deepCopy(source.get" + sourceFieldNameUp + "()));");
                }else{
                    result = MappingBaseService.getDealOrNull(this, methodEntity, null,
                            buildBaseStatement(targetFieldNameUp, sourceFieldNameUp));
                }
                sb.append("\t").append(result).append("\n");
            }else{//Object->String
                XToolMapping xToolMapping = methodEntity.getXToolMapping();
                if(xToolMapping != null && xToolMapping.jsonMapping()){
                    getImportList().add(com.alibaba.fastjson.JSON.class);
                    result = "target.set" + targetFieldNameUp + "(JSON.toJSONString(source.get" + sourceFieldNameUp + "()));";
                }else{
                    result = "";
                }
                sb.append("\t").append(result).append("\n");
            }
        }catch(Exception e){
            sb.append(String.format("String s_%s = \"该类型暂不支持或使用深拷贝没有实现序列化接口:%s\"", methodEntity.getTargetFieldName(), methodEntity.getTargetFieldType()));
        }
    }

    private boolean isMethodAnnoDeepClone(ExecutableElement method) {
        return method.getAnnotation(MappingDeepClone.class) != null;
    }


    private String assertNull(VariableElement target){
        String simpleName =  CommonUtils.getSimpleName(target.asType().toString());
        return
                "\t" + "if (source == null) {" + "\n" +
                "    return null;" + "\n" +
                "}" + "\n" +
                simpleName + " target = new " + simpleName + "();" + "\n";
    }

    public String getNullString(String targetFieldNameUp){
        return "target.set" + targetFieldNameUp + "(null);";
    }

    protected String buildBaseStatement(String targetFieldNameUp, String sourceFieldNameUp){
        return  "target.set" + targetFieldNameUp + "(source.get" + sourceFieldNameUp + "());";
    }


    public String buildDeepClone(){
        getImportList().add(java.io.ByteArrayInputStream.class);
        getImportList().add(java.io.ByteArrayOutputStream.class);
        getImportList().add(java.io.ObjectOutputStream.class);
        getImportList().add(java.io.ObjectInputStream.class);
        return "        try{\n" +
                "            ByteArrayOutputStream bos = new ByteArrayOutputStream();\n" +
                "            ObjectOutputStream oos = new ObjectOutputStream(bos);\n" +
                "            oos.writeObject(object);\n" +
                "            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());\n" +
                "            ObjectInputStream ois = new ObjectInputStream(bis);\n" +
                "            return ois.readObject();\n" +
                "        }catch(Exception e){\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "        return null;";

    }

}