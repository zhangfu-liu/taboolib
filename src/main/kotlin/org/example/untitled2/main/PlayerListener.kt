package org.example.untitled2.main

import org.bukkit.event.player.PlayerMoveEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerListener {

    @SubscribeEvent
    fun onPlayerMove(event: PlayerMoveEvent){
        val player = event.player
        val location = event.to
    }
}