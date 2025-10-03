package me.balda.strikekitsregen.config;

import me.balda.strikekitsregen.StrikeKitsRegen;
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
    
    public boolean isFeedOnFFAKill() {
        return config.getBoolean("feed_on_ffa_kill", true);
    }
    
    public boolean isHealOnFFAKill() {
        return config.getBoolean("heal_on_ffa_kill", true);
    }
    
    public boolean isRestoreKitOnFFAKill() {
        return config.getBoolean("restore_kit_on_ffa_kill", true);
    }
    
    public List<String> getFeedExclude() {
        return config.getStringList("feed_exclude");
    }
    
    public List<String> getHealExclude() {
        return config.getStringList("heal_exclude");
    }
    
    public List<String> getRestoreExclude() {
        return config.getStringList("restore_exclude");
    }
    
    public boolean isArenaExcluded(String arenaName, List<String> excludeList) {
        return excludeList.contains(arenaName.toLowerCase());
    }
}