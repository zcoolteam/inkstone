package com.zcool.inkstone.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

open class BuildInkstoneGenerator : DefaultTask() {

    @get:OutputDirectory
    var outputDir: File? = null

    @get:InputDirectory
    var manifestDir: File? = null

    @TaskAction
    fun brewJava() {
        brewJava(outputDir!!, manifestDir!!)
    }

}

fun brewJava(outputDir: File, manifestDir: File) {
    val manifestFile = File(manifestDir, "AndroidManifest.xml")

    val actionPrefix = "MODULE_MANIFEST_PACKAGE:"

    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document = builder.parse(manifestFile)

    InkstoneConfigClassBuilder()
            .apply {
                document.getElementsByTagName("activity").run {
                    for (i in 0 until this.length) {
                        val targetActivity = this.item(i)
                        when (targetActivity) {
                            is Element -> {
                                if (targetActivity.getAttribute("android:name") == "com.zcool.inkstone.app.InkstoneConfigActivity") {
                                    targetActivity.getElementsByTagName("action")?.run {
                                        for (j in 0 until this.length) {
                                            val action = this.item(j)
                                            when (action) {
                                                is Element -> {
                                                    val actionName = action.getAttribute("android:name")
                                                    actionName?.run {
                                                        if (this.startsWith(actionPrefix)) {
                                                            addModulePackageName(this.substring(actionPrefix.length))
                                                        } else {
                                                            error("found error action name $this, must starts with $actionPrefix, like $actionPrefix$this")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .create(outputDir)
}