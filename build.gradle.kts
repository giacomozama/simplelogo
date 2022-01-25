
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.Kotlin
    id("com.github.ben-manes.versions") version "0.41.0"
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

    tasks.withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}
