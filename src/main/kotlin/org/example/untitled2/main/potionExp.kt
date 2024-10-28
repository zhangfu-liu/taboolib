package org.example.untitled2.main


import org.bukkit.Location
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.max
import kotlin.math.min
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.pluginId
import taboolib.expansion.createHelper
import java.util.*
import kotlin.math.truncate

@CommandHeader("exppond", permission = "exppond.use")
object ExpPondCommand {
    private var pointA: Location? = null
    private var pointB: Location? = null
    private val playerExpBottles = mutableMapOf<UUID, Boolean>()

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val a = subCommand {
        execute<Player> { sender, _, _ ->
            pointA = sender.location
            sender.sendMessage("已设置A点为当前位置: ${locationToString(pointA)}")
        }
    }

    @CommandBody
    val b = subCommand {
        execute<Player> { sender, _, _ ->
            pointB = sender.location
            sender.sendMessage("已设置B点为当前位置: ${locationToString(pointB)}")
        }
    }

    @CommandBody
    val reload = subCommand {
        execute<Player> { sender, _, _ ->
            sender.sendMessage("插件配置已重载")
        }
    }

    @SubscribeEvent
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = event.to ?: return

        // 如果任一点位未设置，直接返回
        if (pointA == null || pointB == null) return

        val isInRegion = isLocationInRegion(location)
        val wasInRegion = playerExpBottles.containsKey(player.uniqueId)

        when {
            // 玩家进入区域
            isInRegion && !wasInRegion -> {
                player.sendTitle("你已进入经验区", "请享受经验雨", 10, 70, 20)
                val expBottle = ItemStack(Material.EXPERIENCE_BOTTLE, 1)
                player.inventory.addItem(expBottle)
                playerExpBottles[player.uniqueId] = true
            }
            // 玩家离开区域
            !isInRegion && wasInRegion -> {
                player.sendTitle("你已离开经验区", "经验药水已移除", 10, 70, 20)
                playerExpBottles.remove(player.uniqueId)
            }
        }
    }

    private fun isLocationInRegion(location: Location): Boolean {
        val minX = minOf(pointA!!.x, pointB!!.x)
        val maxX = maxOf(pointA!!.x, pointB!!.x)
        val minY = minOf(pointA!!.y, pointB!!.y)
        val maxY = maxOf(pointA!!.y, pointB!!.y)
        val minZ = minOf(pointA!!.z, pointB!!.z)
        val maxZ = maxOf(pointA!!.z, pointB!!.z)

        return location.x in minX..maxX &&
                location.y in minY..maxY &&
                location.z in minZ..maxZ
    }

    private fun locationToString(location: Location?): String {
        return location?.let { "x:${it.x}, y:${it.y}, z:${it.z}" } ?: "未设置"
    }
}
