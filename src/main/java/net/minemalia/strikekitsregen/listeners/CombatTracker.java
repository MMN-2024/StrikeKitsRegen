package net.minemalia.strikekitsregen.listeners;

import net.minemalia.strikekitsregen.StrikeKitsRegen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatTracker implements Listener {
    
    private final StrikeKitsRegen plugin;
    
    public CombatTracker(StrikeKitsRegen plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        
        // Track who damaged whom for kill attribution
        plugin.getCombatTracker().put(victim, attacker);
        
        plugin.debug("Combat tracked: " + attacker.getName() + " damaged " + victim.getName());
    }
}