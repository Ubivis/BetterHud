package kr.toxicity.hud.manager

import kr.toxicity.hud.api.manager.ConfigManager
import kr.toxicity.hud.configuration.PluginConfiguration
import kr.toxicity.hud.pack.PackType
import kr.toxicity.hud.resource.GlobalResource
import kr.toxicity.hud.resource.KeyResource
import kr.toxicity.hud.util.*
import kr.toxicity.hud.yaml.YamlObjectImpl
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.io.File
import java.text.DecimalFormat

object ConfigManagerImpl: BetterHudManager, ConfigManager {
    var key = KeyResource(NAME_SPACE)
        private set

    val info = EMPTY_COMPONENT.append(Component.text("[!] ").color(NamedTextColor.GOLD))
    val warn = EMPTY_COMPONENT.append(Component.text("[!] ").color(NamedTextColor.RED))
    private var line = 1

    private var needToUpdateConfig = false

    var defaultHud = emptyList<String>()
        private set
    var defaultPopup = emptyList<String>()
        private set
    var defaultCompass = emptyList<String>()
        private set
    var versionCheck = true
        private set

    var numberFormat = DecimalFormat("#,###.#")
        private set
    var defaultFontName = "font.ttf"
        private set
    var tickSpeed = 1L
        private set
    var disableToBedrockPlayer = true
        private set
    var buildFolderLocation = "BetterHud/build".replace('/', File.separatorChar)
        private set
    var enableProtection = true
        private set
    var forceUpdate = false
        private set

    var mergeBossBar = true
        private set
    var packType = PackType.FOLDER
        private set
    var enableSelfHost = false
        private set
    var selfHostIp = "*"
        private set
    var selfHostPort = 8163
        private set
    var mergeOtherFolders = emptyList<String>()
        private set

    var needToUpdatePack = false
        private set
    var loadingHead = "random"
        private set
    var debug = false
        private set

    var resourcePackObfuscation = false
        private set

    var clearBuildFolder = true
        private set

    var minecraftJarVersion = "bukkit"
        private set

    var loadMinecraftDefaultTextures = true
        private set
    var includedMinecraftTextures = listOf(
        "block",
        "item"
    )
        private set

    var useLegacyFormat = true
    var legacySerializer = LEGACY_AMPERSAND
        private set

    override fun start() {
    }

    override fun getBossbarLine(): Int = line
    override fun reload(sender: Audience, resource: GlobalResource) {
    }

    override fun preReload() {
        runCatching {
            File(DATA_FOLDER, "version.yml").apply {
                if (!exists()) createNewFile()
            }.run {
                val yaml = toYaml() as YamlObjectImpl
                needToUpdateConfig = yaml.get("plugin-version")?.asString() != BOOTSTRAP.version()
                yaml.put("plugin-version", BOOTSTRAP.version())
                yaml.save(this)
            }

            needToUpdatePack = false
            val yaml = PluginConfiguration.CONFIG.create()
            debug = yaml.getAsBoolean("debug", false)
            defaultHud = yaml.get("default-hud")?.asArray()?.map {
                it.asString()
            } ?: emptyList()
            defaultPopup = yaml.get("default-popup")?.asArray()?.map {
                it.asString()
            } ?: emptyList()
            defaultCompass = yaml.get("default-compass")?.asArray()?.map {
                it.asString()
            } ?: emptyList()
            yaml.get("default-font-name")?.asString()?.let {
                if (defaultFontName != it) needToUpdatePack = true
                defaultFontName = it
            }
            yaml.get("pack-type")?.asString()?.let {
                runWithExceptionHandling(CONSOLE, "Unable to find this pack type: $it") {
                    packType = PackType.valueOf(it.uppercase())
                }
            }
            tickSpeed = yaml.getAsLong("tick-speed", 1)
            numberFormat = yaml.get("number-format")?.asString()?.let {
                runWithExceptionHandling(CONSOLE, "Unable to read this number-format: $it") {
                    DecimalFormat(it)
                }.getOrNull()
            } ?: DecimalFormat("#,###.#")
            disableToBedrockPlayer = yaml.getAsBoolean("disable-to-bedrock-player", true)
            yaml.get("build-folder-location")?.asString()?.let {
                buildFolderLocation = it.replace('/', File.separatorChar)
            }
            val newLine = yaml.getAsInt("bossbar-line", 1).coerceAtLeast(1).coerceAtMost(7)
            if (line != newLine) {
                line = newLine
                needToUpdatePack = true
            }
            versionCheck = yaml.getAsBoolean("version-check", false)
            enableProtection = yaml.getAsBoolean("enable-protection", false)
            mergeBossBar = yaml.getAsBoolean("merge-boss-bar", true)
            enableSelfHost = yaml.getAsBoolean("enable-self-host", false)
            mergeOtherFolders = yaml.get("merge-other-folders")?.asArray()?.map {
                it.asString()
            } ?: emptyList()
            yaml.get("self-host-ip")?.asString()?.let { ip ->
                selfHostIp = ip
            }
            selfHostPort = yaml.getAsInt("self-host-port", 8163)
            forceUpdate = yaml.getAsBoolean("force-update", false)
            resourcePackObfuscation = yaml.getAsBoolean("resourcepack-obfuscation", false)
            if (yaml.getAsBoolean("metrics", false)) {
                BOOTSTRAP.startMetrics()
            } else {
                BOOTSTRAP.endMetrics()
            }
            yaml.get("loading-head")?.asString()?.let {
                loadingHead = it
            }
            clearBuildFolder = yaml.getAsBoolean("clear-build-folder", true)
            loadMinecraftDefaultTextures = yaml.getAsBoolean("load-minecraft-default-textures", true)
            includedMinecraftTextures = yaml.get("included-minecraft-list")?.asArray()?.map {
                it.asString()
            } ?: emptyList()
            useLegacyFormat = yaml.getAsBoolean("use-legacy-format", true)
            yaml.get("legacy-serializer")?.asString()?.let {
                runWithExceptionHandling(CONSOLE, "Unable to find legacy serializer.") {
                    legacySerializer = it.toLegacySerializer()
                }
            }
            key = KeyResource(yaml.get("namespace")?.asString() ?: NAME_SPACE)
            minecraftJarVersion = yaml.get("minecraft-jar-version")?.asString() ?: "bukkit"
        }.onFailure { e ->
            warn(
                "Unable to load config.yml",
                "Reason: ${e.message}"
            )
        }
    }
    override fun end() {
    }
}