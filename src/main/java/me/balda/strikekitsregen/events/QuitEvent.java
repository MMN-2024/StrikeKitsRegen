package me.balda.strikekitsregen.events;

import me.balda.strikekitsregen.StrikeKitsRegen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {
    private final StrikeKitsRegen plugin;
    
    public QuitEvent(StrikeKitsRegen plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getInCombat().remove(event.getPlayer());
    }
}
