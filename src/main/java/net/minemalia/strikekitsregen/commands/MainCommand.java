package net.minemalia.strikekitsregen.commands;

import net.minemalia.strikekitsregen.StrikeKitsRegen;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    
    private final StrikeKitsRegen plugin;
    
    public MainCommand(StrikeKitsRegen plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("strikekitsregen.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "StrikeKitsRegen configuration reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to reload configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }
    
    private void handleInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + "         StrikeKitsRegen Info         " + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════╣");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Version: " + ChatColor.YELLOW + plugin.getDescription().getVersion() + ChatColor.GOLD + "                     ║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Author: " + ChatColor.YELLOW + "mDevelopment" + ChatColor.GOLD + "                ║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Network: " + ChatColor.YELLOW + "Minemalia" + ChatColor.GOLD + "                ║");
        sender.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════╣");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Health Regen: " + getStatusColor(plugin.getConfigManager().isHealthRegenerationEnabled()) + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Hunger Regen: " + getStatusColor(plugin.getConfigManager().isHungerRegenerationEnabled()) + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Kit Restoration: " + getStatusColor(plugin.getConfigManager().isKitRestorationEnabled()) + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " Debug Mode: " + getStatusColor(plugin.getConfigManager().isDebugEnabled()) + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════╝");
    }
    
    private String getStatusColor(boolean enabled) {
        return enabled ? ChatColor.GREEN + "ENABLED    " : ChatColor.RED + "DISABLED   ";
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + "       StrikeKitsRegen Commands       " + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════╣");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " /skr reload " + ChatColor.GRAY + "- Reload config     " + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "║" + ChatColor.WHITE + " /skr info " + ChatColor.GRAY + "- Show plugin info    " + ChatColor.GOLD + "║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════╝");
    }
}