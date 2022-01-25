import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask

plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin.get().dependsOn("generateKotlinGrammarSource")

sourceSets.main {
    java.srcDirs("build/generated-src/antlr/main", "src/main/kotlin")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":model"))
    testImplementation(kotlin("test-junit5"))
    implementation(Libs.AntlrKotlin.runtime)
}

tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create(Libs.AntlrKotlin.target)
    )
    maxHeapSize = "64m"
    packageName = "zama.giacomo.simplelogo.parser"
    arguments = listOf(
        "-visitor",
        "-no-listener"
    )
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/main/antlr").apply {
            include("Logo.g4")
        }
    outputDirectory = File("build/generated-src/antlr/main")
}