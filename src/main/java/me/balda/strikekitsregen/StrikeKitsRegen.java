package me.balda.strikekitsregen;

import me.balda.strikekitsregen.config.ConfigManager;
import me.balda.strikekitsregen.events.DamageEvent;
import me.balda.strikekitsregen.events.DeathEvent;
import me.balda.strikekitsregen.events.QuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class StrikeKitsRegen extends JavaPlugin {
    private static StrikeKitsRegen instance;
    private final Map<Player, Player> inCombat = new HashMap<>();
    private ConfigManager configManager;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        // Initialize configuration
        configManager = new ConfigManager(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        
        // Register events
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new DamageEvent(this), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(this), this);
        
        inCombat.clear();
        
        logger.info("StrikeKitsRegen has been enabled!");
    }
    
    @Override
    public void onDisable() {
        inCombat.clear();
        logger.info("StrikeKitsRegen has been disabled!");
    }
    
    public static StrikeKitsRegen getInstance() {
        return instance;
    }
    
    public Map<Player, Player> getInCombat() {
        return inCombat;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
