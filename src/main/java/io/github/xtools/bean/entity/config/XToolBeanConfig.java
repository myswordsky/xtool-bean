package io.github.xtools.bean.entity.config;

import io.github.xtools.bean.annotation.XToolBean;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.function.Consumer;

/**
 * XToolBean注解类配置
 *
 * @author xzb
 */
public class XToolBeanConfig {
    public final Messager messager;
    public final ProcessingEnvironment env;
    public XToolBean xToolBean;
    public ClassInfo classInfo;


    public XToolBeanConfig(Messager messager, ProcessingEnvironment env) {
        this.messager = messager;
        this.env = env;
    }


    public void parse(TypeElement element, Filer filer) {
        classInfo = ClassInfo.buildClassInfo(env, element);
        List<ExecutableElement> methods = classInfo.getMethods();
        for(ExecutableElement method:methods){
            //必须是两个参数
            List<? extends VariableElement> parameters = method.getParameters();
            if(parameters == null || parameters.size() != 2){
                System.err.println("Error parameters size in: " + method);
            }
        }
    }

    public XToolBean getXToolBean() {
        return xToolBean;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public Consumer<String> getErrorLog(){
        return err -> this.messager.printMessage(Diagnostic.Kind.ERROR, err);
    }

}