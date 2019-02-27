package com.zcool.inkstone.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.zcool.inkstone.annotation.ApplicationDelegate;
import com.zcool.inkstone.annotation.Config;
import com.zcool.inkstone.annotation.ModuleConfig;
import com.zcool.inkstone.annotation.ServicesProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedOptions({Constants.OPTION_MODULE_MANIFEST_PACKAGE})
@SupportedAnnotationTypes({Constants.ANNOTATION_TYPE_APPLICATION_DELEGATE, Constants.ANNOTATION_TYPE_SERVICES_PROVIDER})
public class InkstoneGenProcessor extends AbstractProcessor {

    private boolean mGen;
    private Map<String, TypeElement> mApplicationDelegateElements = new HashMap<>();
    private Map<String, TypeElement> mServicesProviderElements = new HashMap<>();

    private String mModuleManifestPackage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mModuleManifestPackage = processingEnv.getOptions().get(Constants.OPTION_MODULE_MANIFEST_PACKAGE);
        if (mModuleManifestPackage != null) {
            mModuleManifestPackage = mModuleManifestPackage.trim();
        }

        if (Strings.isNullOrEmpty(mModuleManifestPackage)) {
            throw new RuntimeException(Constants.TIP_NO_OPTION_MODULE_MANIFEST_PACKAGE);
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "option " + Constants.OPTION_MODULE_MANIFEST_PACKAGE + ": " + mModuleManifestPackage);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        {
            Set<? extends Element> appDelegateElements = roundEnv.getElementsAnnotatedWith(ApplicationDelegate.class);
            if (appDelegateElements != null) {
                for (Element item : appDelegateElements) {
                    if (!ElementKind.CLASS.equals(item.getKind())) {
                        throw new IllegalArgumentException(ApplicationDelegate.class.getName() + " must set on class");
                    }

                    TypeElement typeItem = (TypeElement) item;
                    String targetClass = typeItem.getQualifiedName().toString();
                    Object old = mApplicationDelegateElements.put(targetClass, typeItem);
                    if (old != null) {
                        throw new RuntimeException("InkstoneGenProcessor process found duplicate ApplicationDelegate class " + targetClass);
                    }

                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "InkstoneGenProcessor process found ApplicationDelegate class " + targetClass);
                }
            }
        }

        {
            Set<? extends Element> servicesProviderElements = roundEnv.getElementsAnnotatedWith(ServicesProvider.class);
            if (servicesProviderElements != null) {
                for (Element item : servicesProviderElements) {
                    if (!ElementKind.CLASS.equals(item.getKind())) {
                        throw new IllegalArgumentException(ServicesProvider.class.getName() + " must set on class");
                    }

                    TypeElement typeItem = (TypeElement) item;
                    String targetClass = typeItem.getQualifiedName().toString();
                    Object old = mServicesProviderElements.put(targetClass, typeItem);
                    if (old != null) {
                        throw new RuntimeException("InkstoneGenProcessor process found duplicate ServicesProvider class " + targetClass);
                    }

                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "InkstoneGenProcessor process found ServicesProvider class " + targetClass);
                }
            }
        }

        if (!mGen) {
            mGen = true;

            brewJava();
        }

        return false;
    }

    private void brewJava() {
        try {
            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
            codeBlockBuilder.addStatement("$T result = new $T()", Config.class, Config.class);

            mApplicationDelegateElements.forEach((key, value) -> codeBlockBuilder.addStatement(
                    "result.addApplicationDelegate($T.valueOf($S, $L))",
                    Config.ApplicationDelegate.class,
                    key,
                    value.getAnnotation(ApplicationDelegate.class).priority()));
            mServicesProviderElements.forEach((key, value) -> codeBlockBuilder.addStatement(
                    "result.addServicesProvider($T.valueOf($S, $L))",
                    Config.ServicesProvider.class,
                    key,
                    value.getAnnotation(ServicesProvider.class).priority()));

            codeBlockBuilder.addStatement("return result");

            TypeSpec.Builder targetClassBuilder = TypeSpec.classBuilder("InkstoneModuleConfigImpl")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ModuleConfig.class)
                    .addMethod(MethodSpec.methodBuilder("getConfig")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(Config.class)
                            .addAnnotation(Override.class)
                            .addCode(codeBlockBuilder.build())
                            .build());
            JavaFile.builder(mModuleManifestPackage, targetClassBuilder.build())
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (Throwable e) {
            throw new RuntimeException("fail to generate InkstoneModuleConfigImpl class", e);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
