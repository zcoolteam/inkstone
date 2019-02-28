package com.zcool.inkstone.gradle.plugin

import com.squareup.javapoet.*
import com.zcool.inkstone.annotation.Config
import com.zcool.inkstone.annotation.ModuleConfig
import java.io.File
import javax.lang.model.element.Modifier

class InkstoneConfigClassBuilder {

    private var modulePackageNames = mutableListOf<String>()

    fun addModulePackageName(modulePackageName: String) {
        modulePackageNames.add(modulePackageName)
    }

    fun create(outputDir: File) {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement("\$T result = new \$T()", Config::class.java, Config::class.java)
        modulePackageNames.forEach {
            val moduleConfigClassName = ClassName.bestGuess("$it.InkstoneModuleConfigImpl")
            codeBlockBuilder.addStatement("result.add(new \$T().getConfig())", moduleConfigClassName)
        }
        codeBlockBuilder.addStatement("return result")

        val targetClassBuilder = TypeSpec.classBuilder("InkstoneAppConfigImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(ClassName.bestGuess("androidx.annotation.Keep"))
                .addSuperinterface(ModuleConfig::class.java)
                .addMethod(MethodSpec.methodBuilder("getConfig")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Config::class.java)
                        .addAnnotation(Override::class.java)
                        .addCode(codeBlockBuilder.build())
                        .build())
        JavaFile.builder("com.zcool.inkstone", targetClassBuilder.build())
                .build()
                .writeTo(outputDir)
    }

}