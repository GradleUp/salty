import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kgp).apply(false)
  alias(libs.plugins.librarian).apply(false)
  alias(libs.plugins.gratatouille).apply(false)
  alias(libs.plugins.ksp).apply(false)
}

Librarian.root(project)