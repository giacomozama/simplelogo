pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "simplelogo"
include("parser", "gui")
include("model")
