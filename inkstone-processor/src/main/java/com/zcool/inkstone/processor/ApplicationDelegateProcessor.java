package com.zcool.inkstone.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zcool.inkstone.annotation.ApplicationDelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

@AutoService(Processor.class)
public class ApplicationDelegateProcessor extends AbstractProcessor {

    private Map<String, TypeElement> mTargetClasses = new HashMap<>();
    private boolean mGenerated = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> all = roundEnv.getElementsAnnotatedWith(ApplicationDelegate.class);
        if (all != null) {
            for (Element item : all) {
                if (!ElementKind.CLASS.equals(item.getKind())) {
                    throw new IllegalArgumentException(ApplicationDelegate.class.getName() + " must set on class");
                }

                TypeElement typeItem = (TypeElement) item;
                String targetClass = typeItem.getQualifiedName().toString();
                Object old = mTargetClasses.put(targetClass, typeItem);
                if (old != null) {
                    throw new RuntimeException("ApplicationDelegateProcessor process found duplicate class " + targetClass);
                }
            }
        }

        Set<? extends Element> rootElements = roundEnv.getRootElements();
        if (rootElements == null || rootElements.isEmpty()) {
            if (!mGenerated) {
                mGenerated = true;

                generateWithTargetClasses();
            }
        }

        return false;
    }

    private void generateWithTargetClasses() {
        ClassName subApplicationDelegateType = ClassName.bestGuess("com.zcool.inkstone.SubApplicationDelegate");
        ClassName listType = ClassName.get("java.util", "List");
        ClassName arrayListType = ClassName.get("java.util", "ArrayList");
        TypeName listTypeAsT = ParameterizedTypeName.get(listType, subApplicationDelegateType);

        CodeBlock.Builder methodGetBlockBuilder = CodeBlock.builder();
        methodGetBlockBuilder.addStatement("$T result = new $T<>()", listTypeAsT, arrayListType);

        List<TypeElement> elements = new ArrayList<>(mTargetClasses.values());
        elements.sort(new Comparator<TypeElement>() {
            @Override
            public int compare(TypeElement lo, TypeElement ro) {
                return lo.getAnnotation(ApplicationDelegate.class).priority() -
                        ro.getAnnotation(ApplicationDelegate.class).priority();
            }
        });
        for (TypeElement element : elements) {
            String className = element.getQualifiedName().toString();
            ClassName classNameType = ClassName.bestGuess(className);
            methodGetBlockBuilder.addStatement("result.add(new $T())", classNameType);
        }

        methodGetBlockBuilder.addStatement("return result");

        MethodSpec methodGet = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(listTypeAsT)
                .addCode(methodGetBlockBuilder.build())
                .build();

        TypeSpec finderClass = TypeSpec.classBuilder("SubApplicationDelegateGroup")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodGet)
                .build();

        JavaFile javaFile = JavaFile.builder("com.zcool.inkstone", finderClass).build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Throwable e) {
            throw new RuntimeException("fail to write java file", e);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();

        types.add(ApplicationDelegate.class.getName());

        return Collections.unmodifiableSet(types);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

}
