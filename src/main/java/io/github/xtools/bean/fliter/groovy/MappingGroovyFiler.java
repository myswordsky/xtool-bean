package io.github.xtools.bean.fliter.groovy;


import io.github.xtools.bean.data.BeanCopyLocalGroovyData;
import io.github.xtools.bean.entity.groovy.MappingClassGroovyConfig;
import io.github.xtools.bean.entity.groovy.MethodGroovyEntity;
import io.github.xtools.bean.tools.CommonUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * Groovy方式
 *
 * @author xzb
 */
public class MappingGroovyFiler extends AbstractGroovy {

    public MappingGroovyFiler(MappingClassGroovyConfig config) {
        super(config);
    }

    public String build() {
        //方法体内容
        String sb =
                this.getClassHeader(config.getEntityClass(), config.getDtoClass()) +
                this.toEntity() +
                //this.toEntityList() +
                this.toDto() +
                //this.toDtoList() +
                this.thisEntity() +
                this.thisDto();
        //额外的导入
        StringBuilder importString = new StringBuilder();
        for(String str:getImportList()){
            importString.append(str).append("\n");
        }
        importString.append("\n");
        return sb.replace(FieldImportList, importString.toString()) + "}";
    }

    protected String getClassHeader(Class<?> source, Class<?> target){
        String packageName = config.getPackageGenName() == null ? source.getPackage().getName() : config.getPackageGenName();
        String sourceName = source.getSimpleName();
        String targetName = target.getSimpleName();
        StringBuilder sb = new StringBuilder(Package).append(packageName).append(";\n\n")
                .append(Import).append(source.getName()).append(";\n")
                .append(Import).append(target.getName()).append(";\n")
                .append(Import).append(BaseMappingPackageName).append(";\n")
                .append(Import).append(List).append(";\n")
                .append(Import).append(ArrayList).append(";\n")
                .append("\n");
        sb.append(FieldImportList);
        sb
                .append("public class ").append(source.getSimpleName()).append(CopyImpl)
                .append(" implements ").append(BaseMappingName)
                .append("<").append(sourceName).append(", ").append(targetName).append(">").append(" {").append("\n")
                .append("\n");
        return sb.toString();
    }

    protected String toEntity() {
        Class<?> dtoClass = config.getDtoClass();
        Class<?> entityClass = config.getEntityClass();
        String sb = "\t" + Override + "\n" +
                    "\t" + publicName + entityClass.getSimpleName() + " " + "toEntity(" + dtoClass.getSimpleName() + " source" + ") {\n";
        String body = setMethodContent(dtoClass, entityClass);
        return sb + body + "\t}\n\n";
    }
    protected String toEntityList() {
        Class<?> dtoClass = config.getDtoClass();
        Class<?> entityClass = config.getEntityClass();
        String sb = "\t" + Override + "\n" +
                    "\t" + publicName + "List<" + entityClass.getSimpleName() + "> " + "toEntity(List<" + dtoClass.getSimpleName() + "> " + "sourceList" + ") {\n";
        String bodyEnd = setMethodContentList(dtoClass, entityClass, "toEntity");
        return sb + bodyEnd;
    }
    protected String toDto() {
        Class<?> dtoClass = config.getDtoClass();
        Class<?> entityClass = config.getEntityClass();
        String sb = "\t" + Override + "\n" +
                    "\t" + publicName + dtoClass.getSimpleName() + " " + "toDto(" + entityClass.getSimpleName() + " source" + ") {\n";
        String body = setMethodContent(entityClass, dtoClass);
        return sb + body + "\t}\n\n";
    }
    protected String toDtoList() {
        Class<?> dtoClass = config.getDtoClass();
        Class<?> entityClass = config.getEntityClass();
        String sb = "\t" + Override + "\n" +
                    "\t" + publicName + "List<" + dtoClass.getSimpleName() + "> " + "toDto(List<" + entityClass.getSimpleName() + "> " + "sourceList" + ") {\n";
        String bodyEnd = setMethodContentList(entityClass, dtoClass, "toDto");
        return sb + bodyEnd;
    }
    protected String thisEntity() {
        Class<?> dtoClass = config.getEntityClass();
        Class<?> entityClass = config.getEntityClass();
        String sb = "\t" + Override + "\n" +
                "\t" + publicName + entityClass.getSimpleName() + " " + "thisEntity(" + dtoClass.getSimpleName() + " source" + ") {\n";
        String body = setMethodContent(dtoClass, entityClass);
        return sb + body + "\t}\n\n";
    }
    protected String thisDto() {
        Class<?> dtoClass = config.getDtoClass();
        Class<?> entityClass = config.getDtoClass();
        String sb = "\t" + Override + "\n" +
                "\t" + publicName + entityClass.getSimpleName() + " " + "thisDto(" + dtoClass.getSimpleName() + " source" + ") {\n";
        String body = setMethodContent(dtoClass, entityClass);
        return sb + body + "\t}\n\n";
    }

    private String setMethodContentList(Class<?> source, Class<?> target, String method) {
        String sb =
                        "\t\t" + "if (sourceList == null) {" + "\n" +
                        "\t\t" + "    return null;" + "\n" +
                        "\t\t" + "}" + "\n" +
                        "\t\t" + "List<$T> list = new ArrayList<>(sourceList.size());" + "\n";
        sb = sb.replaceAll("\\$T", target.getSimpleName());
        sb +=
                        "\t\t" + "for ($T source : sourceList) {" + "\n" +
                        "\t\t" + "    list.add(" + method + "(source));" + "\n" +
                        "\t\t" + "}" + "\n";
        sb = sb.replaceAll("\\$T", source.getSimpleName());
        return sb + "\t\treturn list;\n\t}\n\n";
    }

    private String setMethodContent(Class<?> sourceClass, Class<?> targetClass) {
        String sb =
                "\t\t" + "if (source == null) {" + "\n" +
                "\t\t" + "    return null;" + "\n" +
                "\t\t" + "}" + "\n" +
                "\t\t" + targetClass.getSimpleName() + " target = new " + targetClass.getSimpleName() + "();" + "\n";
        //for循环所有公用字段
        StringBuilder content = new StringBuilder();
        for(Field target : BeanCopyLocalGroovyData.getClassMap(targetClass).values()){
            String targetFieldName = target.getName();
            Class<?> targetType = target.getType();
            String targetFieldNameUp = CommonUtils.getStartStringUp(targetFieldName);
            BeanCopyLocalGroovyData.getClassMap(sourceClass).values().stream().filter(e->e.getName().equals(targetFieldName)).findFirst().ifPresent(source->{
                String statement = "target.set" + targetFieldNameUp + "(source.get" + targetFieldNameUp + "());";
                String setNull = "target.set" + targetFieldNameUp + "(null);";

                String sourceFieldNameUp = CommonUtils.getStartStringUp(source.getName());
                MethodGroovyEntity methodEntity = new MethodGroovyEntity();
                methodEntity.setTargetFieldType(targetType);
                methodEntity.setTargetFieldName(targetFieldName);
                methodEntity.setTargetFieldNameUp(targetFieldNameUp);
                methodEntity.setSourceFieldNameUp(sourceFieldNameUp);
                methodEntity.setSourceField(source);
                methodEntity.setTargetField(target);
                methodEntity.setSource(sourceClass);
                methodEntity.setTarget(targetClass);
                methodEntity.setNullString(setNull);
                //特殊处理
                if (Date.class.equals(source.getType())) {
                    statement = handleDate(statement, setNull, methodEntity);
                } else if (Float.class.equals(source.getType()) || float.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(float.class.equals(source.getType()));
                    statement = handleFloat(statement, methodEntity);
                } else if (Double.class.equals(source.getType()) || double.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(double.class.equals(source.getType()));
                    statement = handleDouble(statement, methodEntity);
                } else if (BigDecimal.class.equals(source.getType())) {//仅支持Double和BigDecimal
                    if(Double.class.equals(targetType) || double.class.equals(targetType)) {
                        statement = getDealOrNull(targetFieldNameUp, "target.set" + targetFieldNameUp + "(source.get" + targetFieldNameUp + "().doubleValue());"
                                , double.class.equals(targetType) , methodEntity);
                    }
                } else if (Integer.class.equals(source.getType()) || int.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(int.class.equals(source.getType()));
                    statement = handleInteger(statement, methodEntity);
                } else if (Long.class.equals(source.getType()) || long.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(long.class.equals(source.getType()));
                    statement = handleLong(statement, methodEntity);
                } else if (String.class.equals(source.getType())) {
                    statement = handleString(statement, setNull, methodEntity);
                }else if (Boolean.class.equals(source.getType()) || boolean.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(boolean.class.equals(source.getType()));
                    statement = handleBoolean(statement, methodEntity);
                }else if (Byte.class.equals(source.getType()) || byte.class.equals(source.getType())) {
                    methodEntity.setSourceIsBaseType(byte.class.equals(source.getType()));
                    statement = handleByte(statement, methodEntity);
                }
                //其他不处理
                content.append("\t\t").append(statement).append("\n");
            });
        }
        sb += content;
        sb += "\t\treturn target;\n";
        return sb;
    }

    private String handleDate(String statement, String setNull, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(source.get" + sourceFieldNameUp + "().getTime());"
                    , long.class.equals(targetType) , entity);
        }else if (String.class.equals(targetType)) {
            statement = getDateOrNull(setNull, entity, false);
        }
        return statement;
    }

    private String handleByte(String statement, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Integer.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , int.class.equals(targetType) , entity);
        }
        return statement;
    }

    private String handleBoolean(String statement, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    , "target.set" + targetFieldNameUp
                            + "(source.is" + sourceFieldNameUp + "() == Boolean.TRUE ? 1 : source.is" + sourceFieldNameUp + "() == Boolean.FALSE ? 0 : null);"
                    , int.class.equals(targetType) , entity);
        } else if (String.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    , "target.set" + targetFieldNameUp + "(String.valueOf(source.is" + sourceFieldNameUp + "());"
                    , entity);
        }
        return statement;
    }

    private String handleString(String statement, String setNull, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Integer.parseInt(source.get" + sourceFieldNameUp + "()));"
                    , int.class.equals(targetType) , entity);
        }else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Long.parseLong(source.get" + sourceFieldNameUp + "()));"
                    , long.class.equals(targetType) , entity);
        }else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Float.parseFloat(source.get" + sourceFieldNameUp + "()));"
                    , float.class.equals(targetType) , entity);
        }else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()).doubleValue());"
                    , double.class.equals(targetType) , entity);
        }else if (Date.class.equals(targetType)) {
            statement = getDateOrNull(setNull, entity, true);
        }else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Boolean.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , boolean.class.equals(targetType) , entity);
        }
        return statement;
    }

    private String getDateOrNull(String nullStatement, MethodGroovyEntity entity, boolean annotationInTargetFieldClass){
        String targetFieldName = entity.getTargetFieldName();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
        Class<?> clazz = annotationInTargetFieldClass ? entity.getTarget() : entity.getSource();
        String parseString = annotationInTargetFieldClass ? "parse" : "format";
        Field field = BeanCopyLocalGroovyData.getClassMap(clazz).get(targetFieldName);
        Annotation annoy = Arrays.stream(field.getDeclaredAnnotations()).filter(e -> e.toString().contains("JsonFormat")).findFirst().orElse(null);
        String formatBody;
        if(annoy != null){
            formatBody =
                            "\t\t\t" + "\t" + "JsonFormat annotation = field.getAnnotation(JsonFormat.class);" + "\n" +
                            "\t\t\t" + "\t" + "String pattern = annotation.pattern();" + "\n" +
                            "\t\t\t" + "\t" + "String timezone = annotation.timezone();" + "\n" +
                            "\t\t\t" + "\t" + "SimpleDateFormat sf;" + "\n" +
                            "\t\t\t" + "\t" + "if(!JsonFormat.DEFAULT_TIMEZONE.equals(pattern)){" + "\n" +
                            "\t\t\t" + "\t\t" + "sf = new SimpleDateFormat(pattern);" + "\n" +
                            "\t\t\t" + "\t" + "} else {" + "\n" +
                            "\t\t\t" + "\t\t" + "sf = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");" + "\n" +//默认"yyyy-MM-dd HH:mm:ss"
                            "\t\t\t" + "\t" + "}" + "\n" +
                            "\t\t\t" + "\t" + "if(!JsonFormat.DEFAULT_TIMEZONE.equals(timezone)){" + "\n" +
                            "\t\t\t" + "\t\t" + "sf.setTimeZone(TimeZone.getTimeZone(timezone));" + "\n" +
                            "\t\t\t" + "\t" + "}" + "\n" +
                            "\t\t\t" + "\t" + "target.set" + targetFieldNameUp + "(sf." + parseString + "(source.get" + sourceFieldNameUp + "()));" + "\n";
            getImportList().add("import java.util.Date;");
            getImportList().add("import java.lang.reflect.Field;");
            getImportList().add("import com.fasterxml.jackson.annotation.JsonFormat;");
            getImportList().add("import java.text.SimpleDateFormat;");
            getImportList().add("import java.util.TimeZone;");
            getImportList().add("import java.util.Arrays;");
            getImportList().add("import java.lang.annotation.Annotation;");
        }else {
            formatBody =    "\t\t\t" + "\t" + "target.set" + targetFieldNameUp + "(new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\")." + parseString + "(source.get" + sourceFieldNameUp + "()));" + "\n";
            getImportList().add("import java.text.SimpleDateFormat;");
        }
        return
                "try {" + "\n" +
                "\t\t\t" + "if (source.get" + sourceFieldNameUp + "() != null) {" + "\n" +
                        formatBody +
                "\t\t\t" + "} else {" + "\n" +
                "\t\t\t" + "\t" + nullStatement + "\n" +
                "\t\t\t" + "}" + "\n" +
                "\t\t" + "}catch (Exception e) {" + "\n" +
                "\t\t\t" + "throw new RuntimeException(e);" + "\n" +
                "\t\t" + "}" + "\n";
    }

    private String handleInteger(String statement, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (String.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , entity);
        }else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , long.class.equals(targetType) , entity);
        }else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , double.class.equals(targetType) , entity);
        }else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Float.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , float.class.equals(targetType) , entity);
        }else if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Integer.valueOf(source.get" + sourceFieldNameUp + "()).byteValue());"
                    , byte.class.equals(targetType) , entity);
        }else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp
                        + "(source.get" + sourceFieldNameUp + "() == 1 ? Boolean.TRUE : source.get" + sourceFieldNameUp + "() == 0 ? Boolean.FALSE : Boolean.TRUE));"
                    , boolean.class.equals(targetType) , entity);
        }
        return statement;
    }

    private String handleLong(String statement, MethodGroovyEntity entity) {
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (String.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , entity);
        }else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()).intValue());"
                    , int.class.equals(targetType) , entity);
        }else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Long.valueOf(source.get" + sourceFieldNameUp + "()).doubleValue());"
                    , double.class.equals(targetType) , entity);
        }else if (Date.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(new Date(source.get" + sourceFieldNameUp + "()));"
                    , entity);
            getImportList().add("import java.util.Date;");
        }
        return statement;
    }

    private String handleDouble(String statement, MethodGroovyEntity entity){
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if(Float.class.equals(targetType) || float.class.equals(targetType)){
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).floatValue());"
                    , float.class.equals(targetType) , entity);
        }else if (String.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , entity);
        }else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).intValue());"
                    , int.class.equals(targetType) , entity);
        }else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Double.valueOf(source.get" + sourceFieldNameUp + "()).longValue());"
                    , long.class.equals(targetType) , entity);
        }else if (BigDecimal.class.equals(targetType)) {
            getImportList().add("import java.math.BigDecimal;");
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(new BigDecimal(source.get" + sourceFieldNameUp + "()));"
                    , entity);
        }
        return statement;
    }
    private String handleFloat(String statement, MethodGroovyEntity entity){
        Class<?> targetType = entity.getTargetFieldType();
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();

        if (String.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));"
                    , entity);
        }else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Float.valueOf(source.get" + sourceFieldNameUp + "()).intValue());"
                    , int.class.equals(targetType) , entity);
        }else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Float.valueOf(source.get" + sourceFieldNameUp + "()).longValue());"
                    , long.class.equals(targetType) , entity);
        }else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
            statement = getDealOrNull(sourceFieldNameUp
                    ,"target.set" + targetFieldNameUp + "(Float.valueOf(source.get" + sourceFieldNameUp + "()).doubleValue());"
                    , double.class.equals(targetType) , entity);
        }
        return statement;
    }

    protected String getDealOrNull(String sourceFieldNameUp, String statement, Object... params){
        if(params.length == 1){
            MethodGroovyEntity entity = (MethodGroovyEntity)params[0];
            if(entity.isSourceIsBaseType()){
                return statement + "\n";
            }
            getImportList().add("import java.util.Objects;");
            return
                    "if (Objects.equals(source.get" + sourceFieldNameUp + "(), null)) {" + "\n" +
                    "\t\t" + "    " + entity.getNullString() + "\n" +
                    "\t\t" + "} else {" + "\n" +
                    "\t\t" + "    " + statement + "\n" +
                    "\t\t" + "}" + "\n";
        }else{
            boolean targetIsBaseType = (boolean)params[0];
            MethodGroovyEntity entity = (MethodGroovyEntity)params[1];
            String nullStatement = entity.getNullString();
            if(entity.isSourceIsBaseType()){
                return statement + "\n";
            }
            if(targetIsBaseType){
                getImportList().add("import java.util.Objects;");
                return
                        "if (!Objects.equals(source.get" + sourceFieldNameUp + "(), null)) {" + "\n" +
                        "\t\t" + "    " + statement + "\n" +
                        "\t\t" + "}\n";
            }else{
                getImportList().add("import java.util.Objects;");
                return
                        "if (Objects.equals(source.get" + sourceFieldNameUp + "(), null)) {" + "\n" +
                        "\t\t" + "    " + nullStatement + "\n" +
                        "\t\t" + "} else {" + "\n" +
                        "\t\t" + "    " + statement + "\n" +
                        "\t\t" + "}" + "\n";

            }
        }

    }
}