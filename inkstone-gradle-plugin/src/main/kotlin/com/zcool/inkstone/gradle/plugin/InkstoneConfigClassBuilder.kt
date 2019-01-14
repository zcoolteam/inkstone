package com.zcool.inkstone.gradle.plugin

import com.squareup.javapoet.*
import com.zcool.inkstone.annotation.Config
import com.zcool.inkstone.annotation.ModuleConfig
import java.io.File
import javax.lang.model.element.Modifier

class InkstoneConfigClassBuilder {

    private var rClassPackageNames = mutableListOf<String>()

    fun addModuleRClassPackageName(className: String) {
        rClassPackageNames.add(className)
    }

    fun create(outputDir: File) {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement("\$T result = new \$T()", Config::class.java, Config::class.java)
        rClassPackageNames.forEach {
            val moduleConfigClassName = ClassName.bestGuess(it.substring(0, it.length - 1) + "InkstoneModuleConfigImpl")
            codeBlockBuilder.addStatement("result.add(new \$T())", moduleConfigClassName)
        }
        codeBlockBuilder.addStatement("return result")

        val targetClassBuilder = TypeSpec.classBuilder("InkstoneAppConfigImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
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