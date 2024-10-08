package kr.toxicity.hud.resource

import kr.toxicity.hud.manager.ConfigManagerImpl
import kr.toxicity.hud.pack.PackGenerator
import kr.toxicity.hud.util.*

class GlobalResource {
    private val assets = listOf("assets")

    private val hud = assets + NAME_SPACE_ENCODED
    private val minecraft = assets + "minecraft"

    val bossBar = minecraft + listOf("textures", "gui")
    val core = minecraft + listOf("shaders", "core")

    val font = hud + "font"
    val textures = hud + "textures"

    init {
        val key = ConfigManagerImpl.key
        BOOTSTRAP.resource("splitter.png")?.buffered()?.use {
            val read = it.readAllBytes()
            PackGenerator.addTask(textures + "${ConfigManagerImpl.key.splitterKey.value()}.png") {
                read
            }
        }
        BOOTSTRAP.resource("spaces.ttf")?.buffered()?.use {
            val read = it.readAllBytes()
            PackGenerator.addTask(font + "${ConfigManagerImpl.key.spacesTtfKey.value()}.ttf") {
                read
            }
        }
        PackGenerator.addTask(font + "${ConfigManagerImpl.key.spaceKey.value()}.json") {
            val center = 0xD0000
            jsonObjectOf(
                "providers" to jsonArrayOf(
                    jsonObjectOf(
                        "type" to "bitmap",
                        "file" to "${key.splitterKey.asString()}.png",
                        "ascent" to -9999,
                        "height" to -2,
                        "chars" to jsonArrayOf(0xC0000.parseChar())
                    ),
                    jsonObjectOf(
                        "type" to "space",
                        "advances" to jsonObjectOf(*(-8192..8192).map { i ->
                            (center + i).parseChar() to i
                        }.toTypedArray())
                    )
                )
            ).toByteArray()
        }
        PackGenerator.addTask(font + "${ConfigManagerImpl.key.legacySpaceKey.value()}.json") {
            jsonObjectOf(
                "providers" to jsonObjectOf(
                    "type" to "ttf",
                    "file" to "${key.spacesTtfKey.asString()}.ttf",
                    "size" to 2.5,
                    "oversample" to 1.0,
                    "shift" to jsonArrayOf(0.0, 0.0),
                    "skip" to jsonArrayOf()
                )
            ).toByteArray()
        }
    }
}