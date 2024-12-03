import com.gradleup.librarian.gradle.Librarian
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
  id("java-gradle-plugin")
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.gratatouille.api")
}

Librarian.module(project)

dependencies {
  gratatouille(project(":salty-core"))
  compileOnly(libs.agp)
}

gradlePlugin {
  plugins.create("com.gradleup.salty") {
    id = "com.gradleup.salty"
    implementationClass = "salty.gradle.Plugin"
  }
  plugins.create("com.gradleup.salty.android") {
    id = "com.gradleup.salty.android"
    implementationClass = "salty.gradle.AndroidPlugin"
  }
}
