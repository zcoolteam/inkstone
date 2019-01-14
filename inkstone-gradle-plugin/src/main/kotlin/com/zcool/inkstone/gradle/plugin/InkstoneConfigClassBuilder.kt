package com.zcool.inkstone.gradle.plugin

import java.io.File

class InkstoneConfigClassBuilder {

    private var rClassPackageNames = mutableListOf<String>()

    fun addModuleRClassPackageName(className: String) {
        rClassPackageNames.add(className)
    }

    fun create(outputDir: File) {
        for (rClassPackageName in rClassPackageNames) {
            println("rClassPackageName -> $rClassPackageName")
        }
    }

}