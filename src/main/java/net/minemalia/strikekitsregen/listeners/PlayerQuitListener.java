package net.minemalia.strikekitsregen.listeners;

import net.minemalia.strikekitsregen.StrikeKitsRegen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final StrikeKitsRegen plugin;
    
    public PlayerQuitListener(StrikeKitsRegen plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up combat tracking data when player leaves
        plugin.getCombatTracker().remove(event.getPlayer());
        
        plugin.debug("Cleaned up combat data for " + event.getPlayer().getName());
    }
}