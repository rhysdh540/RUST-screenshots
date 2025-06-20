import xyz.wagyourtail.manifold.plugin.manifold

plugins {
    id("dev.isxander.modstitch.base") version("0.5.12") apply(false)
    id("xyz.wagyourtail.manifold") version("1.0.0-SNAPSHOT") apply(false)
}

run {
    val (mc, platform) = stonecutter.current.project.split("-")
    ext["minecraft_version"] = mc
    ext["platform"] = platform

    ext["modstitch.platform"] = when {
        platform == "fabric" -> "loom"
        platform == "neoforge" -> "moddevgradle"
        platform == "forge" && stonecutter.eval(mc, ">=1.17") -> "moddevgradle-legacy"
        else -> error("unsupported platform: $platform")
    }

    apply(plugin = "dev.isxander.modstitch.base")
}

modstitch {
    minecraftVersion = "minecraft_version"()

    javaTarget = if (stonecutter.eval("minecraft_version", ">=1.20.5")) 21 else 17

    parchment {
        "parchment_version".maybe { mappingsVersion = it }
        "parchment_mc_version".maybe { minecraftVersion = it }
    }

    metadata {
        modId = "rust"
        modName = "rdh's Ultimate Screenshot Tool"
        modVersion = project.version.toString()
        modDescription = "screenshots if they were cool"
        modLicense = "ARR" // for now
        modGroup = project.group.toString()
        modAuthor = "rdh"
        replacementProperties.apply {
            put("mod_issue_tracker", "https://todo.lol")
        }
    }

    loom {
        fabricLoaderVersion = "0.16.14"
    }

    moddevgradle {
        enable {
            "forge_version"().let {
                if ("platform"() == "neoforge") {
                    neoForgeVersion = "minecraft_version"().removePrefix("1.") + "." + it
                } else {
                    forgeVersion = "minecraft_version"() + "-" + it
                }
            }
        }

        defaultRuns(server = false)
    }

    mixin {
        addMixinsToModManifest = true
        configs.register("rust")
    }
}

stonecutter {
    constants {
        match("platform"(), "fabric", "forge", "neoforge")
        put("forgelike", "platform"().let { it == "forge" || it == "neoforge" })
    }
}

apply(plugin = "xyz.wagyourtail.manifold")

manifold {
    version = "2025.1.22"

    preprocessor {
        config {
            property("MC", stonecutter.current.version.removePrefix("1."))
            stonecutter.constants.forEach {
                if (it.value) { property(it.key) }
            }
        }
    }
}

dependencies {
    annotationProcessor(manifold("preprocessor"))
}

operator fun String.invoke() = findProperty(this)?.toString() ?: error("No property \"$this\"")
fun String.maybe(block: (String) -> Unit) = findProperty(this)?.toString()?.let(block)
fun Project.modstitch(block: dev.isxander.modstitch.base.extensions.ModstitchExtension.() -> Unit) {
    the<dev.isxander.modstitch.base.extensions.ModstitchExtension>().apply(block)
}
fun Project.manifold(block: xyz.wagyourtail.manifold.plugin.ManifoldExtension.() -> Unit) {
    manifold.apply(block)
}