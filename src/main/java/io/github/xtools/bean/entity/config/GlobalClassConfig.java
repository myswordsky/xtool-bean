package io.github.xtools.bean.entity.config;

import io.github.xtools.bean.fliter.config.MappingFiler;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 总配置
 *
 * @author xzb
 */
public class GlobalClassConfig {
    /**
     * 异常情况下不生成
     */
    public static String Error;
    public static Set<String> clazzSet = new HashSet<>();
    /**
     * 生成的所有包
     */
    public static Set<String> packageGenClazzList = new HashSet<>();

    private static final Map<String, XToolBeanConfig> annotationMap = new HashMap<>();

    public static Map<String, XToolBeanConfig> getAnnotationMap(){
        return annotationMap;
    }

    /**
     * 生成java文件
     */
    public static void generate(Filer filer, Consumer<String> logger) {
        if(Error != null){
            System.err.println("方法检查失败,终止编译");
            return;
        }
        for(Map.Entry<String, XToolBeanConfig> entry : annotationMap.entrySet()){
            if(clazzSet.contains(entry.getKey())){
                continue;
            }
            clazzSet.add(entry.getKey());
            XToolBeanConfig config = entry.getValue();
            MappingFiler mappingFiler = new MappingFiler(config);
            mappingFiler.createSourceFile(filer, logger);
            packageGenClazzList.addAll(mappingFiler.getClassNames());
        }
        //生成记录文件
        if(packageGenClazzList.size() != 0){
            buildGenClazz(filer, packageGenClazzList);
            packageGenClazzList.clear();
        }
    }

    public static void buildGenClazz(Filer filer, Set<String> packageGenClazzList){
        try{
            String genClazzPackage = Constant.GenClazzPackage;
            String genClazzName = Constant.GenClazzName;
            String genClazzMethodName = Constant.GenClazzMethodName;
            JavaFileObject sourceFile = filer.createSourceFile(Constant.getGenPackageClass());
            Writer writer = sourceFile.openWriter();
            writer.write("package " + genClazzPackage + ";\n\n");

            StringBuilder sb = new StringBuilder();
            packageGenClazzList.forEach(e->sb.append("\t\t").append("list.add(\"").append(e).append("\");\n"));

            writer.write("import java.util.ArrayList;\nimport java.util.List;");
            writer.write("public class " + genClazzName + " {\n\n");
            writer.write(
                    "\tpublic static List<String> " + genClazzMethodName + "(){\n" +
                        "\t\tList<String> list = new ArrayList<>();\n" +
                            sb +
                        "\t\treturn list;\n" +
                        "\t}\n");
            writer.write("}\n");
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}