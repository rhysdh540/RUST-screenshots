@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.util.gradle.SourceSetReference
import xyz.wagyourtail.commonskt.utils.capitalized

plugins {
    id("fabric-loom") version("1.10.+") apply(false)
    id("net.neoforged.moddev") version("2.0.95") apply(false)
    id("xyz.wagyourtail.manifold") version("1.1.0-SNAPSHOT")
}

run {
    val (mc, platform) = stonecutter.current.project.split("-")
    ext["minecraft_version"] = mc
    ext["platform"] = platform

    project.group = rootProject.group
    project.base.archivesName = "rust-${mc}-${platform}"
    project.version = rootProject.version
}

stonecutter {
    constants {
        match("platform"(), "fabric", "forge", "neoforge")
        put("forgelike", "platform"().let { it == "forge" || it == "neoforge" })
    }
}

manifold {
    version = "manifold_version"()
    pluginArgs.add("--no-bootstrap")

    preprocessor {
        config {
            property("MC", stonecutter.current.version.removePrefix("1."))
            stonecutter.constants.forEach {
                if (it.value) { property(it.key) }
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
    includeEmptyDirs = false
}

fun applyLoom() {
    apply(plugin = "fabric-loom")

    repositories {
        exclusiveContent {
            forRepository { maven("https://maven.parchmentmc.org") }
            filter { includeGroupAndSubgroups("org.parchmentmc") }
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${"minecraft_version"()}")
        "mappings"(loom.layered {
            officialMojangMappings()
            "parchment_version".maybe {
                parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${it}@zip")
            }
        })

        "modImplementation"("net.fabricmc:fabric-loader:0.16.14")
    }

    loom {
        mods {
            create("rust") {
                modSourceSets.add(sourceSets.main.map { SourceSetReference(it, project) })
            }
        }

        runs {
            named("client") {
                client()
                name("Fabric " + "minecraft_version"())

                ideConfigGenerated(true)
            }

            remove(getByName("server"))
        }

        mixin {
            useLegacyMixinAp = false
        }
    }
}

fun applyMDG() {
    apply(plugin = "net.neoforged.moddev")

    neoForge {
        version = "minecraft_version"().removePrefix("1.") + "." + "forge_version"()
    }

    applyGenericForge()
}

fun applyLegacyMDG() {
    apply(plugin = "net.neoforged.moddev.legacyforge")

    legacyForge {
        version = "minecraft_version"() + "-" + "forge_version"()
    }

    mixin {
        config("rust.mixins.json")
    }

    applyGenericForge()
}

fun applyGenericForge() {
    forge {
        "parchment_version".maybe {
            parchment {
                minecraftVersion = "minecraft_version"()
                mappingsVersion = it
            }
        }

        mods.create("rust") {
            modSourceSets.add(sourceSets.main)
        }

        runs {
            create("client") {
                client()
                this.ideName = "platform"().capitalized() + " " + "minecraft_version"()
            }
        }
    }
}

when("platform"()) {
    "fabric" -> applyLoom()
    "neoforge" -> applyMDG()
    "forge" -> applyLegacyMDG()
    else -> error("Unrecognized mod platform")
}

tasks.processResources {
    when("platform"()) {
        "fabric" -> { exclude("META-INF/*.toml") }
        "forge" -> { exclude("META-INF/neoforge.mods.toml", "fabric.mod.json") }
        "neoforge" -> { exclude("META-INF/mods.toml", "fabric.mod.json") }
        else -> error("Unknown platform: ${"platform"()}")
    }

    val version = rootProject.version as String

    filesMatching(listOf("META-INF/*.toml", "fabric.mod.json", "pack.mcmeta")) {
        expand(
            "mod_issue_tracker" to "https://todo.lol",
            "mod_license" to "ARR", // for now
            "mod_id" to "rust",
            "mod_version" to version,
            "mod_name" to "RDH'S ULTIMATE SCREENSHOT TOOL",
            "mod_description" to "screenshots if they were cool",
            "mod_author" to "rdh",
        )
    }
}

dependencies {
    annotationProcessor(manifold("preprocessor"))
    annotationProcessor(manifold("exceptions"))
    annotationProcessor(manifold("rt"))
}

// region Helpers
operator fun String.invoke() = findProperty(this)?.toString() ?: error("No property \"$this\"")
fun String.maybe(block: (String) -> Unit) = findProperty(this)?.toString()?.let(block)

val Project.manifold get() = the<xyz.wagyourtail.manifold.plugin.ManifoldExtension>()
fun Project.manifold(block: xyz.wagyourtail.manifold.plugin.ManifoldExtension.() -> Unit) {
    manifold.apply(block)
}

val Project.loom get() = the<net.fabricmc.loom.api.LoomGradleExtensionAPI>()
fun Project.loom(block: net.fabricmc.loom.api.LoomGradleExtensionAPI.() -> Unit) {
    loom.apply(block)
}

val Project.forge get() = the<net.neoforged.moddevgradle.dsl.ModDevExtension>()
fun Project.forge(block: net.neoforged.moddevgradle.dsl.ModDevExtension.() -> Unit) {
    forge.apply(block)
}

val Project.neoForge get() = the<net.neoforged.moddevgradle.dsl.NeoForgeExtension>()
fun Project.neoForge(block: net.neoforged.moddevgradle.dsl.NeoForgeExtension.() -> Unit) {
    neoForge.apply(block)
}

val Project.legacyForge get() = the<net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension>()
fun Project.legacyForge(block: net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension.() -> Unit) {
    legacyForge.apply(block)
}

val Project.mixin get() = the<net.neoforged.moddevgradle.legacyforge.dsl.MixinExtension>()
fun Project.mixin(block: net.neoforged.moddevgradle.legacyforge.dsl.MixinExtension.() -> Unit) {
    mixin.apply(block)
}
// endregion