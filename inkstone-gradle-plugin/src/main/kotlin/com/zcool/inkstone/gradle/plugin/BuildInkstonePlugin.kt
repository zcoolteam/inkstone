package com.zcool.inkstone.gradle.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project

open class BuildInkstonePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("BuildInkstonePlugin#apply")
        project.plugins.all {
            when (it) {
                is AppPlugin -> {
                    println("is AppPlugin")
                    project.extensions.getByType(AppExtension::class.java).run {
                        buildInkstone(project, applicationVariants)
                    }
                }
            }
        }
    }

    private fun buildInkstone(project: Project, variants: DomainObjectSet<ApplicationVariant>) {
        variants.all { variant ->
            val outputDir = project.buildDir.resolve(
                    "generated/source/buildInkstone/${variant.dirName}")
            variant.outputs.all { output ->
                val manifestDir = output.processManifestProvider.get().manifestOutputDirectory.get().asFile
                manifestDir.run {
                    project.tasks.create("generate${variant.name.capitalize()}BuildInkstone", BuildInkstoneGenerator::class.java) {
                        it.outputDir = outputDir
                        it.manifestDir = manifestDir
                        it.setDependsOn(listOf(output.processManifestProvider.get()))
                        variant.registerJavaGeneratingTask(it, outputDir)
                    }
                }
            }
        }
    }

}