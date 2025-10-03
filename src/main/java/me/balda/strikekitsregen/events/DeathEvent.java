package me.balda.strikekitsregen.events;

import ga.strikepractice.StrikePractice;
import ga.strikepractice.api.StrikePracticeAPI;
import ga.strikepractice.arena.Arena;
import ga.strikepractice.battlekit.BattleKit;
import ga.strikepractice.fight.Fight;
import me.balda.strikekitsregen.StrikeKitsRegen;
import me.balda.strikekitsregen.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {
    private final StrikeKitsRegen plugin;
    private final StrikePracticeAPI api;
    private final ConfigManager configManager;
    
    public DeathEvent(StrikeKitsRegen plugin) {
        this.plugin = plugin;
        this.api = StrikePractice.getAPI();
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer != null && api.getFight(killer) != null) {
            giveKit(killer);
        } else if (plugin.getInCombat().containsKey(victim)) {
            Player damager = plugin.getInCombat().get(victim);
            if (api.getFight(damager) != null) {
                giveKit(damager);
            }
        }
        plugin.getInCombat().remove(victim);
    }

    private void giveKit(Player aggressor) {
        Fight fight = api.getFight(aggressor);
        if (fight == null) return;
        
        Arena arena = fight.getArena();
        if (!arena.isFFA()) return;
        
        String arenaName = arena.getName();
        BattleKit kit = fight.getKit();
        String kitName = kit != null ? kit.getName() : "";
        
        if (kit != null) {
            // Check and apply feed
            if (configManager.isFeedOnFFAKill() && 
                !configManager.isArenaExcluded(arenaName, configManager.getFeedExclude()) &&
                !configManager.isKitExcluded(kitName, configManager.getFeedExcludeKits())) {
                aggressor.setFoodLevel(20);
                aggressor.setSaturation(20.0f);
            }
            
            // Check and apply heal
            if (configManager.isHealOnFFAKill() && 
                !configManager.isArenaExcluded(arenaName, configManager.getHealExclude()) &&
                !configManager.isKitExcluded(kitName, configManager.getHealExcludeKits())) {
                aggressor.setHealth(aggressor.getMaxHealth());
            }
            
            // Check and apply kit restoration
            if (configManager.isRestoreKitOnFFAKill() && 
                !configManager.isArenaExcluded(arenaName, configManager.getRestoreExclude()) &&
                !configManager.isKitExcluded(kitName, configManager.getRestoreExcludeKits())) {
                restoreKit(aggressor, kit);
            }
        }
    }
    
    private void restoreKit(Player player, BattleKit currentKit) {
        try {
            BattleKit lastKit = api.getLastSelectedEditedKit(player);
            if (lastKit != null) {
                currentKit.giveKitStuff(player, lastKit);
            } else {
                String currentKitName = currentKit.getName();
                BattleKit defaultKit = api.getKit(currentKitName);
                if (defaultKit != null) {
                    currentKit.giveKitStuff(player, defaultKit);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to restore kit for player " + player.getName() + ": " + e.getMessage());
        }
    }
}
