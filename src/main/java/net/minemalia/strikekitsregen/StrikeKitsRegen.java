package net.minemalia.strikekitsregen;

import net.minemalia.strikekitsregen.commands.MainCommand;
import net.minemalia.strikekitsregen.config.ConfigManager;
import net.minemalia.strikekitsregen.listeners.CombatTracker;
import net.minemalia.strikekitsregen.listeners.PlayerDeathListener;
import net.minemalia.strikekitsregen.listeners.PlayerQuitListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class StrikeKitsRegen extends JavaPlugin {
    
    private static StrikeKitsRegen instance;
    private ConfigManager configManager;
    private final Map<Player, Player> combatTracker = new HashMap<>();
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Check if StrikePractice is available
        if (!checkStrikePractice()) {
            return;
        }
        
        // Initialize configuration
        initializeConfig();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        getLogger().info("StrikeKitsRegen v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Plugin by mDevelopment for Minemalia Network");
    }
    
    @Override
    public void onDisable() {
        combatTracker.clear();
        getLogger().info("StrikeKitsRegen has been disabled!");
    }
    
    private boolean checkStrikePractice() {
        if (getServer().getPluginManager().getPlugin("StrikePractice") == null) {
            getLogger().severe("╔══════════════════════════════════════════════════════════════╗");
            getLogger().severe("║                    DEPENDENCY MISSING                       ║");
            getLogger().severe("║                                                              ║");
            getLogger().severe("║  StrikePractice plugin not found!                           ║");
            getLogger().severe("║  This plugin requires StrikePractice to function.           ║");
            getLogger().severe("║                                                              ║");
            getLogger().severe("║  Please install StrikePractice before using this plugin.    ║");
            getLogger().severe("╚══════════════════════════════════════════════════════════════╝");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
    
    private void initializeConfig() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        if (configManager.isDebugEnabled()) {
            getLogger().info("Debug mode is enabled - additional logging will be shown");
        }
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatTracker(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("strikekitsregen").setExecutor(new MainCommand(this));
    }
    
    public static StrikeKitsRegen getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public Map<Player, Player> getCombatTracker() {
        return combatTracker;
    }
    
    public void debug(String message) {
        if (configManager.isDebugEnabled()) {
            getLogger().info("[DEBUG] " + message);
        }
    }
}