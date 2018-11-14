package com.zcool.inkstone.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zcool.inkstone.annotation.ServicesProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ServicesProviderProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> all = roundEnv.getElementsAnnotatedWith(ServicesProvider.class);
        for (Element item : all) {
            if (item.getKind() != ElementKind.CLASS) {
                throw new IllegalArgumentException(ServicesProvider.class.getName() + " must set on class");
            }

            TypeElement typeItem = (TypeElement) item;
            String targetClass = typeItem.getQualifiedName().toString();

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ServicesProviderProcessor process found target class " + targetClass);

            try {
                generateForTargetClass(targetClass);
            } catch (Throwable e) {
                throw new RuntimeException("fail to generate for target class " + targetClass, e);
            }
        }

        return true;
    }

    private void generateForTargetClass(String className) throws Throwable {
        ClassName targetType = ClassName.bestGuess(className);
        MethodSpec methodGet = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(targetType)
                .addCode("return new $T();", targetType)
                .build();

        TypeSpec finderClass = TypeSpec.classBuilder("ServicesProviderInstance")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodGet)
                .build();

        JavaFile javaFile = JavaFile.builder("com.zcool.inkstone.service", finderClass).build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Throwable e) {
            throw new RuntimeException("fail to write java file", e);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();

        types.add(ServicesProvider.class.getName());

        return Collections.unmodifiableSet(types);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

}
