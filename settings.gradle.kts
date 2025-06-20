pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://maven.isxander.dev/releases/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")

        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.23"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, vararg loaders: String) =
            loaders.forEach { vers("$mcVersion-$it", mcVersion) }

        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "fabric", "neoforge")
        mc("1.21.5", "fabric", "neoforge")

        vcsVersion = "1.21.1-fabric"
    }
}

rootProject.name = "RUST"