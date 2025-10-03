package net.minemalia.strikekitsregen.listeners;

import net.minemalia.strikekitsregen.StrikeKitsRegen;
import net.minemalia.strikekitsregen.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.Method;

public class PlayerDeathListener implements Listener {
    
    private final StrikeKitsRegen plugin;
    private final ConfigManager config;
    private Object strikePracticeAPI;
    private boolean apiAvailable = false;
    
    public PlayerDeathListener(StrikeKitsRegen plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        initializeStrikePracticeAPI();
    }
    
    private void initializeStrikePracticeAPI() {
        try {
            Class<?> strikePracticeClass = Class.forName("ga.strikepractice.StrikePractice");
            Method getAPIMethod = strikePracticeClass.getMethod("getAPI");
            this.strikePracticeAPI = getAPIMethod.invoke(null);
            this.apiAvailable = true;
            plugin.getLogger().info("Successfully connected to StrikePractice API");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize StrikePractice API: " + e.getMessage());
            this.apiAvailable = false;
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!apiAvailable) return;
        
        Player victim = event.getEntity();
        Player killer = determineKiller(victim);
        
        if (killer == null) {
            plugin.debug("No killer found for " + victim.getName());
            return;
        }
        
        plugin.debug("Processing death: " + victim.getName() + " killed by " + killer.getName());
        
        // Clean up combat tracking
        plugin.getCombatTracker().remove(victim);
        
        // Apply regeneration effects with delay if configured
        int delay = config.getDelayTicks();
        if (delay > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> processKill(killer), delay);
        } else {
            processKill(killer);
        }
    }
    
    private Player determineKiller(Player victim) {
        // First check direct killer
        Player killer = victim.getKiller();
        if (killer != null) {
            return killer;
        }
        
        // Fall back to combat tracker
        return plugin.getCombatTracker().get(victim);
    }
    
    private void processKill(Player killer) {
        try {
            Object fight = getFight(killer);
            if (fight == null) {
                plugin.debug("Player " + killer.getName() + " is not in a fight");
                return;
            }
            
            Object arena = getArena(fight);
            if (!isFFAArena(arena)) {
                plugin.debug("Player " + killer.getName() + " is not in an FFA arena");
                return;
            }
            
            String arenaName = getArenaName(arena);
            Object kit = getKit(fight);
            String kitName = kit != null ? getKitName(kit) : "Unknown";
            
            plugin.debug("Processing kill in arena: " + arenaName + " with kit: " + kitName);
            
            // Apply health regeneration
            applyHealthRegeneration(killer, arenaName, kitName);
            
            // Apply hunger regeneration
            applyHungerRegeneration(killer, arenaName, kitName);
            
            // Apply kit restoration
            applyKitRestoration(killer, arenaName, kitName, kit);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error processing kill for " + killer.getName() + ": " + e.getMessage());
        }
    }
    
    private void applyHealthRegeneration(Player player, String arenaName, String kitName) {
        if (!config.isHealthRegenerationEnabled()) return;
        
        if (config.isArenaExcluded(arenaName, config.getHealthExcludedArenas())) {
            plugin.debug("Health regen skipped for " + player.getName() + " - arena excluded: " + arenaName);
            return;
        }
        
        if (config.isKitExcluded(kitName, config.getHealthExcludedKits())) {
            plugin.debug("Health regen skipped for " + player.getName() + " - kit excluded: " + kitName);
            return;
        }
        
        player.setHealth(player.getMaxHealth());
        plugin.debug("Applied health regeneration to " + player.getName());
    }
    
    private void applyHungerRegeneration(Player player, String arenaName, String kitName) {
        if (!config.isHungerRegenerationEnabled()) return;
        
        if (config.isArenaExcluded(arenaName, config.getHungerExcludedArenas())) {
            plugin.debug("Hunger regen skipped for " + player.getName() + " - arena excluded: " + arenaName);
            return;
        }
        
        if (config.isKitExcluded(kitName, config.getHungerExcludedKits())) {
            plugin.debug("Hunger regen skipped for " + player.getName() + " - kit excluded: " + kitName);
            return;
        }
        
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        plugin.debug("Applied hunger regeneration to " + player.getName());
    }
    
    private void applyKitRestoration(Player player, String arenaName, String kitName, Object currentKit) {
        if (!config.isKitRestorationEnabled()) return;
        if (currentKit == null) return;
        
        if (config.isArenaExcluded(arenaName, config.getKitExcludedArenas())) {
            plugin.debug("Kit restoration skipped for " + player.getName() + " - arena excluded: " + arenaName);
            return;
        }
        
        if (config.isKitExcluded(kitName, config.getKitExcludedKits())) {
            plugin.debug("Kit restoration skipped for " + player.getName() + " - kit excluded: " + kitName);
            return;
        }
        
        try {
            restorePlayerKit(player, currentKit);
            plugin.debug("Applied kit restoration to " + player.getName());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to restore kit for " + player.getName() + ": " + e.getMessage());
        }
    }
    
    private void restorePlayerKit(Player player, Object currentKit) throws Exception {
        // Try to get the player's custom kit layout first
        Method getLastSelectedEditedKitMethod = strikePracticeAPI.getClass().getMethod("getLastSelectedEditedKit", Player.class);
        Object customKit = getLastSelectedEditedKitMethod.invoke(strikePracticeAPI, player);
        
        if (customKit != null) {
            // Use custom kit layout
            Method giveKitStuffMethod = currentKit.getClass().getMethod("giveKitStuff", Player.class, Object.class);
            giveKitStuffMethod.invoke(currentKit, player, customKit);
            plugin.debug("Restored custom kit layout for " + player.getName());
        } else {
            // Fall back to default kit
            String kitName = getKitName(currentKit);
            Method getKitMethod = strikePracticeAPI.getClass().getMethod("getKit", String.class);
            Object defaultKit = getKitMethod.invoke(strikePracticeAPI, kitName);
            
            if (defaultKit != null) {
                Method giveKitStuffMethod = currentKit.getClass().getMethod("giveKitStuff", Player.class, Object.class);
                giveKitStuffMethod.invoke(currentKit, player, defaultKit);
                plugin.debug("Restored default kit for " + player.getName());
            }
        }
    }
    
    // =============================================================================
    // STRIKEPRACTICE API REFLECTION METHODS
    // =============================================================================
    
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