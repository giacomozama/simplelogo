
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.Kotlin
    id("com.github.ben-manes.versions") version "0.38.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

buildscript {
    dependencies {
        classpath(Libs.AntlrKotlin.plugin)
    }
}

subprojects {
    group = "zama.giacomo"
    version = "1.0"

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}
