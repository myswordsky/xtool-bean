package io.github.xtools.bean.fliter.config.service;


import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.entity.config.domain.MethodEntity;
import io.github.xtools.bean.fliter.config.AbstractFiler;
import io.github.xtools.bean.tools.StringLangUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MappingBaseService {
    public static final String Default_Format = "yyyy-MM-dd HH:mm:ss";


    public Class<?> extendImpl() {
        return null;
    }


    public static boolean isType(FieldType enums, String targetFieldType){
        return enums.name().toLowerCase().equals(targetFieldType) || enums.getType().getName().equals(targetFieldType);
    }

    /**
     * 是否是基础类型
     * @param enums /
     * @param targetFieldType /
     * @return /
     */
    public static boolean isBaseType(FieldType enums, String targetFieldType){
        return enums.name().toLowerCase().equals(targetFieldType);
    }

    public static String setNotNull(String sourceFieldNameUp, String statement, String getString){
       return
               "if (source." + getString + sourceFieldNameUp + "() != null) {" + "\n" +
               "    " + statement + "\n" +
               "}";

    }

    /**
     * 没有BaseType的类型
     * @param filer /
     * @param entity /
     * @param statement /
     * @return /
     */
    protected static String getDealOrNull(AbstractFiler filer, MethodEntity entity, String statement){
        return getDealOrNull(filer, entity, null, statement);
    }

    public static String getDealOrNull(AbstractFiler filer, MethodEntity entity, Boolean isTargetBaseType, String statement){
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        boolean isSourceBaseEnums = MappingTypeManager.isBaseEnums(entity.getSourceFieldType());
        if(isTargetBaseType == null){
            return statement;
        }
        if(isTargetBaseType){
            return isSourceBaseEnums ? statement : setNotNull(sourceFieldNameUp, statement, genGetString(entity.getSourceFieldType()));
        }
        return statement;
    }

    /**
     * 获取日期转化
     * @param filer /
     * @param entity /
     * @param sourceIsDateType /
     * @return /
     */
    protected String getDateOrNull(AbstractFiler filer, MethodEntity entity, boolean sourceIsDateType){
        String targetFieldNameUp = entity.getTargetFieldNameUp();
        String sourceFieldNameUp = entity.getSourceFieldNameUp();
        XToolMapping xToolMapping = entity.getXToolMapping();

        List<String> list = new ArrayList<>();
        String parseString = sourceIsDateType ? "format" : "parse";
        if(xToolMapping == null){
            filer.getImportList().add(SimpleDateFormat.class);
            list.add("target.set" + targetFieldNameUp + "(new SimpleDateFormat(\"" + Default_Format + "\")." + parseString + "(source.get" + sourceFieldNameUp + "()));");
        }else{
            filer.getImportList().add(Date.class);
            filer.getImportList().add(SimpleDateFormat.class);
            filer.getImportList().add(TimeZone.class);

            String dateFormat = xToolMapping.dateFormat();
            String timeZone = xToolMapping.timeZone();
            if(StringLangUtils.isNotEmpty(dateFormat)){
                list.add("SimpleDateFormat sf = new SimpleDateFormat(\"" + dateFormat + "\");");
            }else{
                list.add("SimpleDateFormat sf = new SimpleDateFormat(\"" + Default_Format + "\");");
            }
            if(StringLangUtils.isNotEmpty(timeZone)){
                list.add("sf.setTimeZone(TimeZone.getTimeZone(\"" + timeZone + "\"));");
            }
            list.add("target.set" + targetFieldNameUp + "(sf." + parseString + "(source.get" + sourceFieldNameUp + "()));");
        }

        StringBuilder sb = new StringBuilder();
        list.forEach(e->sb.append("\t\t").append(e).append("\n"));
        return
                "try {" + "\n" +
                "\t" + "if (source.get" + sourceFieldNameUp + "() != null) {" + "\n" +
                sb +
                "\t" + "} \n" +
                "}catch (Exception e) {" + "\n" +
                "\t" + "throw new RuntimeException(e);" + "\n" +
                "}" + "\n";
    }


    protected String buildBaseStatement(String targetFieldNameUp, String sourceFieldNameUp){
        return  "target.set" + targetFieldNameUp + "(source.get" + sourceFieldNameUp + "());";
    }

    protected String buildBaseStatement(String targetFieldNameUp, String sourceFieldNameUp, String sGet){
        return  "target.set" + targetFieldNameUp + "(source." + sGet + sourceFieldNameUp + "());";
    }


    protected String getNumberFormat(AbstractFiler filer, MethodEntity entity, String targetFieldNameUp, String sourceFieldNameUp){
        String statement;
        XToolMapping xToolMapping = entity.getXToolMapping();
        if(xToolMapping != null && !"".equals(xToolMapping.numberFormat())){
            filer.getImportList().add(DecimalFormat.class);
            String numberFormat = xToolMapping.numberFormat();
            statement = "target.set" + targetFieldNameUp + "(new DecimalFormat(\"" + numberFormat + "\").format(source.get" + sourceFieldNameUp + "()));";
        }else{
            statement = "target.set" + targetFieldNameUp + "(String.valueOf(source.get" + sourceFieldNameUp + "()));";
        }
        return getDealOrNull(filer, entity, statement);
    }

    public String getTypeGenString(MethodEntity entity){
        if(MappingTypeManager.isSameType(entity.getTargetFieldType(), entity.getSourceFieldType())){
            String getString = genGetString(entity.getSourceFieldType());
            boolean isSourceBaseEnums = MappingTypeManager.isBaseEnums(entity.getSourceFieldType());
            String statement = buildBaseStatement(entity.getTargetFieldNameUp(), entity.getSourceFieldNameUp(), getString);
            if(isSourceBaseEnums){
                return statement;
            }else{
                return setNotNull(entity.getSourceFieldNameUp(), statement, getString);
            }
        }
        return "";//其它不支持
    }

    /**
     * 获取Get or Is
     * 基础类型 boolean 是is  包装类是Get
     * @param sourceFieldType /
     * @return /
     */
    public static String genGetString(String sourceFieldType){
        return isBaseType(FieldType.Boolean, sourceFieldType) ? "is" : "get";
    }


}
