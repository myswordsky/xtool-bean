package io.github.xtools.bean.processor;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class BaseProcessor extends AbstractProcessor {
    protected ProcessingEnvironment env;
    protected Filer filer;
    protected Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.env = env;
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        Set<? extends Element> elseElements = roundEnv.getElementsAnnotatedWith(this.annotation());
        for(Element element:elseElements){
            try {
                if (element instanceof TypeElement && element.getKind() == ElementKind.INTERFACE) {
                    this.doFileProcessor((TypeElement) element);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.doFileSummary();
        return true;
    }

    /**
     * 对注解等配置前置处理
     * @param element /
     * @throws Exception /
     */
    protected abstract void doFileProcessor(TypeElement element) throws Exception;

    /**
     * 文件生成
     */
    protected void doFileSummary() {
    }

    protected abstract Class<? extends Annotation> annotation();

    /**
     * 编译文件
     * @param path /
     * @throws IOException /
     */
    @SuppressWarnings({"unused", "unchecked"})
    private void compile(String path) throws IOException {
        //拿到编译器
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();
        //文件管理者
        StandardJavaFileManager fileMgr = complier.getStandardFileManager(null, null, null);
        //获取文件
        Iterable units = fileMgr.getJavaFileObjects(path);
        //编译任务
        JavaCompiler.CompilationTask t = complier.getTask(null, fileMgr, null, null, null, units);
        //进行编译
        t.call();
        fileMgr.close();
    }

}