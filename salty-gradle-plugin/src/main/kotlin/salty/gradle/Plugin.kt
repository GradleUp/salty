package salty.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import salty.core.registerScanClassPathTask

class Plugin: Plugin<Project> {
  override fun apply(target: Project) {
    target.extensions.create("salty", SaltyExtension::class.java, target)
  }
}

abstract class SaltyExtension(private val project: Project) {
  fun check(name: String, action: Action<SaltySpec>) {
    val spec = SaltySpec(project.files(), project.objects.listProperty(String::class.java))

    action.execute(spec)

    project.registerScanClassPathTask(
      taskName = "saltyCheck${name.capitalizeFirstLetter()}",
      taskGroup = "salty",
      files = spec.files,
      forbiddenMethods = spec.forbiddenMethods
    )
  }
}

internal fun String.capitalizeFirstLetter(): String = this.replaceFirstChar { it.uppercase() }

class SaltySpec(internal val files: ConfigurableFileCollection, internal val forbiddenMethods: ListProperty<String>) {
  fun from(files: Any) {
    this.files.from(files)
  }

  fun forbiddenMethod(forbiddenMethod: String) {
    forbiddenMethods.add(forbiddenMethod)
  }
}