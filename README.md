## salty

When you can't desugar, add salt!

A Gradle plugin that inspects your bytecode to detect bad functions that are not safe to use on older Android versions.

- See https://youtrack.jetbrains.com/issue/KT-71375
- See https://jakewharton.com/kotlins-jdk-release-compatibility-flag/

## Usage

Add the plugin:

```kotlin
plugins {
    id("com.gradleup.salty").version(latest)
}
```

Configure the methods to forbid:

```kotlin
salty {
    forbiddenMethods.add("java.util.List.removeFirst")
}
```

Salty adds a `saltyCheck${variantName}` task that visits your app bytecode using [ASM](https://asm.ow2.io/) and fails if an usage of any forbidden method is found. 

`saltyCheck${variantName}` is added to the `check` task so that it is run automatically.
