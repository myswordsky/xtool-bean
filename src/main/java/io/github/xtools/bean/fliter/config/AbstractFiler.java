package io.github.xtools.bean.fliter.config;

import io.github.xtools.bean.annotation.XToolBean;
import io.github.xtools.bean.annotation.XToolMapping;
import io.github.xtools.bean.annotation.mapping.MappingIndexEnums;
import io.github.xtools.bean.entity.config.ClassInfo;
import io.github.xtools.bean.entity.config.Constant;
import io.github.xtools.bean.entity.config.XToolBeanConfig;
import io.github.xtools.bean.interfaces.BaseMapping;
import com.squareup.javawriter.JavaWriter;
import io.github.xtools.bean.tools.AnnotationEntity;
import io.github.xtools.bean.tools.CommonUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "UnusedReturnValue"})
public abstract class AbstractFiler {
    private static final Set<String> classNames = new HashSet<>();

    protected XToolBeanConfig config;
    protected ClassInfo classInfo;
    protected AnnotationEntity annotationEntity;
    /**
     * 额外导入的类
     */
    protected List<Class<?>> importList = new ArrayList<>();
    /**
     * 指向性注解配置
     */
    Map<String/*target*/, Map<MappingIndexEnums, XToolMapping>> mappingAnnotationMap;

    protected String comment;

    public AbstractFiler(XToolBeanConfig config) {
        this.config = config;
        classInfo = config.getClassInfo();
        this.comment = "BeanCopyUtils";

        annotationEntity = new AnnotationEntity();
        config.xToolBean = annotationEntity.parseAnnotation(classInfo.getQualifiedName().toString(), ""
                , classInfo.getClassElement().getAnnotation(XToolBean.class));
    }

    public XToolBeanConfig getConfig() {
        return config;
    }

    public List<Class<?>> getImportList() {
        return importList;
    }

    public Set<String> getClassNames() {
        return classNames;
    }


    public void createSourceFile(Filer filer, Consumer<String> logger) {
        List<ExecutableElement> methods = classInfo.getMethods();
        for(ExecutableElement method:methods){
            try{
                List<? extends VariableElement> parameters = method.getParameters();
                VariableElement first = parameters.get(0);
                VariableElement second = parameters.get(1);
                String firstFullName = first.asType().toString();
                String secondFullName = second.asType().toString();
                String firstSimpleName = CommonUtils.getSimpleName(firstFullName);
                String secondSimpleName = CommonUtils.getSimpleName(secondFullName);
                String classSimpleName = getContactClassName(firstSimpleName, secondSimpleName);

                String classFullName = classInfo.getPackageName() + "." + classSimpleName;
                if(classNames.contains(classFullName)){
                    continue;//判断是否重复生成文件
                }
                classNames.add(classFullName);
                ///E:/project/xtool-bean/ugc/target/generated-sources/annotations/StringGenAndStringGenDTO.java
                System.err.println();

                JavaFileObject sourceFile = filer.createSourceFile(classSimpleName);
                JavaWriter jw = new JavaWriter(sourceFile.openWriter());
                jw.emitPackage(classInfo.getPackageName());
                jw.emitImports(BaseMapping.class);
                jw.emitImports(firstFullName);
                if(!secondFullName.equals(firstFullName)){
                    jw.emitImports(secondFullName);
                }

                //初始化方法注解
                mappingAnnotationMap = buildMethodAnno(method);

                boolean entityIsFirst = CommonUtils.isEntity(firstSimpleName, secondSimpleName);
                VariableElement entity = entityIsFirst ? first : second;
                VariableElement dto = entityIsFirst ? second : first;
                this.build(jw, classSimpleName, method, entity, dto, entityIsFirst);
                jw.endType().close();
                compile(sourceFile.toUri().getPath());

                importList.clear();
            }catch(Exception e){
                System.err.println(classInfo.getQualifiedName() + "#" + method.getSimpleName() + "方法异常");
                e.printStackTrace();
            }
        }
    }

    protected abstract void build(JavaWriter jw, String classSimpleName, ExecutableElement method, VariableElement entity, VariableElement dto, boolean entityIsFirst) throws IOException;

    /**
     * 编译文件
     */
    public static void compile(String path) throws IOException {
        //拿到编译器
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();
        //文件管理者
        StandardJavaFileManager fileMgr = complier.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        //获取文件
        Iterable units = fileMgr.getJavaFileObjects(path);
        //编译任务
        JavaCompiler.CompilationTask t = complier.getTask(null, fileMgr, null, null, null, units);
        //进行编译
        //t.call();
        fileMgr.close();
    }


    public static String getContactClassName(String source, String target){
        return source.compareTo(target) <= 0 ? String.join(Constant.ContactMethod,  source, target) : String.join(Constant.ContactMethod, target, source);
    }

    public static String getImplName(String source, String target){
        String impl = BaseMapping.class.getSimpleName() + "<";
        impl += source.compareTo(target) <= 0 ? String.join(",", source, target) : String.join(",", target, source);
        impl += ">";
        return impl;
    }

    private Map<String/*target*/, Map<MappingIndexEnums, XToolMapping>> buildMethodAnno(ExecutableElement method) {
        XToolMapping[] annotationsByType = method.getAnnotationsByType(XToolMapping.class);
        Map<String, Map<MappingIndexEnums, XToolMapping>> map = new HashMap<>();
        for(XToolMapping xToolMapping:annotationsByType){
            XToolMapping e = annotationEntity.parseAnnotation(classInfo.getQualifiedName().toString(), method.toString(), xToolMapping);
            assert e != null;
            map.putIfAbsent(e.target(), new HashMap<>());
            map.get(e.target()).put(e.index(), e);
        }
        return map;
    }
}