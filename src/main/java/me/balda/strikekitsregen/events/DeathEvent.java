package me.balda.strikekitsregen.events;

import me.balda.strikekitsregen.StrikeKitsRegen;
import me.balda.strikekitsregen.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.Method;

public class DeathEvent implements Listener {
    private final StrikeKitsRegen plugin;
    private final ConfigManager configManager;
    private Object strikePracticeAPI;
    private boolean strikePracticeAvailable = false;
    
    public DeathEvent(StrikeKitsRegen plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        initializeStrikePracticeAPI();
    }
    
    private void initializeStrikePracticeAPI() {
        try {
            Class<?> strikePracticeClass = Class.forName("ga.strikepractice.StrikePractice");
            Method getAPIMethod = strikePracticeClass.getMethod("getAPI");
            this.strikePracticeAPI = getAPIMethod.invoke(null);
            this.strikePracticeAvailable = true;
            plugin.getLogger().info("StrikePractice API initialized successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("StrikePractice API not found! This plugin requires StrikePractice to function.");
            plugin.getLogger().severe("Please ensure StrikePractice is installed and the API JAR is in the libs/ directory.");
            this.strikePracticeAvailable = false;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!strikePracticeAvailable) {
            return;
        }
        
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        try {
            if (killer != null && getFight(killer) != null) {
                giveKit(killer);
            } else if (plugin.getInCombat().containsKey(victim)) {
                Player damager = plugin.getInCombat().get(victim);
                if (getFight(damager) != null) {
                    giveKit(damager);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error processing death event: " + e.getMessage());
        }
        
        plugin.getInCombat().remove(victim);
    }

    private void giveKit(Player aggressor) {
        try {
            Object fight = getFight(aggressor);
            if (fight == null) return;
            
            Object arena = getArena(fight);
            if (!isFFAArena(arena)) return;
            
            String arenaName = getArenaName(arena);
            Object kit = getKit(fight);
            String kitName = kit != null ? getKitName(kit) : "";
            
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
        } catch (Exception e) {
            plugin.getLogger().warning("Error giving kit to player " + aggressor.getName() + ": " + e.getMessage());
        }
    }
    
    private void restoreKit(Player player, Object currentKit) {
        try {
            // Try to get the last selected edited kit
            Method getLastSelectedEditedKitMethod = strikePracticeAPI.getClass().getMethod("getLastSelectedEditedKit", Player.class);
            Object lastKit = getLastSelectedEditedKitMethod.invoke(strikePracticeAPI, player);
            
            if (lastKit != null) {
                // Give kit stuff using the last selected kit
                Method giveKitStuffMethod = currentKit.getClass().getMethod("giveKitStuff", Player.class, Object.class);
                giveKitStuffMethod.invoke(currentKit, player, lastKit);
            } else {
                // Fall back to default kit
                String currentKitName = getKitName(currentKit);
                Method getKitMethod = strikePracticeAPI.getClass().getMethod("getKit", String.class);
                Object defaultKit = getKitMethod.invoke(strikePracticeAPI, currentKitName);
                
                if (defaultKit != null) {
                    Method giveKitStuffMethod = currentKit.getClass().getMethod("giveKitStuff", Player.class, Object.class);
                    giveKitStuffMethod.invoke(currentKit, player, defaultKit);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to restore kit for player " + player.getName() + ": " + e.getMessage());
        }
    }
    
    // Reflection helper methods
    private Object getFight(Player player) throws Exception {
        Method getFightMethod = strikePracticeAPI.getClass().getMethod("getFight", Player.class);
        return getFightMethod.invoke(strikePracticeAPI, player);
    }
    
    private Object getArena(Object fight) throws Exception {
        Method getArenaMethod = fight.getClass().getMethod("getArena");
        return getArenaMethod.invoke(fight);
    }
    
    private boolean isFFAArena(Object arena) throws Exception {
        Method isFFAMethod = arena.getClass().getMethod("isFFA");
        return (Boolean) isFFAMethod.invoke(arena);
    }
    
    private String getArenaName(Object arena) throws Exception {
        Method getNameMethod = arena.getClass().getMethod("getName");
        return (String) getNameMethod.invoke(arena);
    }
    
    private Object getKit(Object fight) throws Exception {
        Method getKitMethod = fight.getClass().getMethod("getKit");
        return getKitMethod.invoke(fight);
    }
    
    private String getKitName(Object kit) throws Exception {
        Method getNameMethod = kit.getClass().getMethod("getName");
        return (String) getNameMethod.invoke(kit);
    }
}