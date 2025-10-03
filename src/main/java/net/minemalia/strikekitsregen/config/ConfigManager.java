package net.minemalia.strikekitsregen.config;

import net.minemalia.strikekitsregen.StrikeKitsRegen;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    
    private final StrikeKitsRegen plugin;
    private FileConfiguration config;
    
    public ConfigManager(StrikeKitsRegen plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // =============================================================================
    // HEALTH CONFIGURATION
    // =============================================================================
    
    public boolean isHealthRegenerationEnabled() {
        return config.getBoolean("health.enabled", true);
    }
    
    public List<String> getHealthExcludedArenas() {
        return config.getStringList("health.excluded_arenas");
    }
    
    public List<String> getHealthExcludedKits() {
        return config.getStringList("health.excluded_kits");
    }
    
    // =============================================================================
    // HUNGER CONFIGURATION
    // =============================================================================
    
    public boolean isHungerRegenerationEnabled() {
        return config.getBoolean("hunger.enabled", true);
    }
    
    public List<String> getHungerExcludedArenas() {
        return config.getStringList("hunger.excluded_arenas");
    }
    
    public List<String> getHungerExcludedKits() {
        return config.getStringList("hunger.excluded_kits");
    }
    
    // =============================================================================
    // KIT CONFIGURATION
    // =============================================================================
    
    public boolean isKitRestorationEnabled() {
        return config.getBoolean("kit.enabled", true);
    }
    
    public List<String> getKitExcludedArenas() {
        return config.getStringList("kit.excluded_arenas");
    }
    
    public List<String> getKitExcludedKits() {
        return config.getStringList("kit.excluded_kits");
    }
    
    // =============================================================================
    // GENERAL CONFIGURATION
    // =============================================================================
    
    public boolean isDebugEnabled() {
        return config.getBoolean("general.debug", false);
    }
    
    public int getDelayTicks() {
        return config.getInt("general.delay_ticks", 0);
    }
    
    // =============================================================================
    // UTILITY METHODS
    // =============================================================================
    
    public boolean isArenaExcluded(String arenaName, List<String> excludedArenas) {
        return excludedArenas.stream()
                .anyMatch(excluded -> excluded.equalsIgnoreCase(arenaName));
    }
    
    public boolean isKitExcluded(String kitName, List<String> excludedKits) {
        return excludedKits.stream()
                .anyMatch(excluded -> excluded.equalsIgnoreCase(kitName));
    }
}