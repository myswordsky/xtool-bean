package io.github.xtools.bean.processor;

import io.github.xtools.bean.annotation.XToolBean;
import io.github.xtools.bean.entity.config.GlobalClassConfig;
import io.github.xtools.bean.entity.config.XToolBeanConfig;
import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 编译拦截器
 *
 * @author xzb
 */
@SuppressWarnings({"rawtypes"})
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.github.xtools.bean.annotation.XToolBean")
public class BeanCopyProcessor extends BaseProcessor {

    @Override
    protected void doFileProcessor(TypeElement element) {
        XToolBeanConfig config = new XToolBeanConfig(super.messager, env);
        config.parse(element, filer);
        GlobalClassConfig.getAnnotationMap().put(element.getQualifiedName().toString(), config);
    }

    @Override
    protected void doFileSummary() {
        GlobalClassConfig.generate(filer, err -> messager.printMessage(NOTE, err));
    }

    @Override
    protected Class<? extends Annotation> annotation() {
        return XToolBean.class;
    }
}