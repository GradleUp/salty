pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.apply {
      mavenCentral()
      google()
    }
  }
}

include(":salty-gradle-plugin", "salty-core")