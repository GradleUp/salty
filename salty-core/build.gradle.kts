import com.gradleup.librarian.gradle.Librarian

plugins {
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.gratatouille.implementation")
}

Librarian.module(project)

dependencies {
  implementation(libs.asm)
}
