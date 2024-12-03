package salty.gradle

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import salty.core.registerScanClassPathTask

class AndroidPlugin: Plugin<Project> {
  private var registered = false

  override fun apply(target: Project) {

    target.pluginManager.withPlugin("com.android.application") {
      target.register()
    }
    target.pluginManager.withPlugin("com.android.library") {
      target.register()
    }

    target.afterEvaluate {
      if (!registered) {
        error("Salty requires the Android plugin")
      }
    }
  }

  private fun Project.register() {
    registered = true
    val extension = extensions.create("salty", SaltyAndroidExtension::class.java)

    val androidComponents = (extensions.getByName("androidComponents") as AndroidComponentsExtension<*,*,*,>)

    androidComponents.onVariants { variant ->
      val unusedTask = tasks.register("saltyUnused${variant.name.capitalizeFirstLetter()}", UnusedTask::class.java)

      variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
        .use(unusedTask)
        .toGet(
          ScopedArtifact.CLASSES,
          UnusedTask::projectJars,
          UnusedTask::allDirectories
        )

      val files = files()
      files.from(unusedTask.map { it.allDirectories })
      files.from(unusedTask.map { it.projectJars })
      val task = registerScanClassPathTask(
        taskName = "saltyCheck${variant.name.capitalizeFirstLetter()}",
        files = files,
        forbiddenMethods = extension.forbiddenMethods
      )

      tasks.named("check").configure { it.dependsOn(task) }
    }

  }
}

abstract class UnusedTask: DefaultTask() {
  @get:InputFiles
  @get:PathSensitive(PathSensitivity.NONE)
  abstract val projectJars: ListProperty<RegularFile>

  @get:InputFiles
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val allDirectories: ListProperty<Directory>

  @TaskAction
  fun doStuff() {
    // do nothing
  }
}

abstract class SaltyAndroidExtension {
  abstract val forbiddenMethods: ListProperty<String>
}