package com.github.nescaaallz.arenasetada.commands;

import com.github.nescaaallz.arenasetada.ArenaSetada;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    protected final ArenaSetada plugin;

    public ArenaCommand(ArenaSetada plugin) {
        this.plugin = plugin;
    }

    public ArenaSetada getPlugin() {
        return plugin;
    }

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length == 0) {
                for(String str : getConfig().getStringList("Messages.Help")) {
                    player.sendMessage(str.replace('&', '§'));
                }
                if(player.hasPermission("arena.staff")) {
                    for(String str : getConfig().getStringList("Messages.HelpStaff")) {
                        player.sendMessage(str.replace('&', '§'));
                    }
                }
            } else {
                switch(args[0]) {
                    case "entrar": {
                        if(getPlugin().getArenaPlayers().contains(player)) {
                            player.sendMessage(getConfig().getString("Messages.Already").replace('&', '§'));
                        } else {
                            player.teleport(getPlugin().getEntrada());
                            getPlugin().setInventory(player);
                            getPlugin().getArenaPlayers().add(player);
                            player.sendMessage(getConfig().getString("Messages.Join").replace('&', '§'));
                        }
                        break;
                    }
                    case "sair": {
                        if(getPlugin().getArenaPlayers().contains(player)) {
                            player.getInventory().setArmorContents(null);
                            player.getInventory().clear();
                            player.teleport(getPlugin().getSaida());
                            getPlugin().getArenaPlayers().remove(player);
                            player.sendMessage(getConfig().getString("Messages.Left").replace('&', '§'));
                        } else {
                            player.sendMessage(getConfig().getString("Messages.NotFound").replace('&', '§'));
                        }
                        break;
                    }
                    case "setentrada": {
                        if(player.hasPermission("arena.staff")) {
                            getPlugin().setLocation(player.getLocation(), ArenaSetada.LocationType.Entrada);
                            player.sendMessage(getConfig().getString("Messages.SetEntrada").replace('&', '§'));
                        } else {
                            player.sendMessage(getConfig().getString("Messages.Unauthorized").replace('&', '§'));
                        }
                        break;
                    }
                    case "setsaida": {
                        if(player.hasPermission("arena.staff")) {
                            getPlugin().setLocation(player.getLocation(), ArenaSetada.LocationType.Saida);
                            player.sendMessage(getConfig().getString("Messages.SetSaida").replace('&', '§'));
                        } else {
                            player.sendMessage(getConfig().getString("Messages.Unauthorized").replace('&', '§'));
                        }
                        break;
                    }
                    case "setinventario": {
                        if(player.hasPermission("arena.staff")) {
                            getPlugin().setInventory(player.getInventory());
                            player.sendMessage(getConfig().getString("Messages.SetInventory").replace('&', '§'));
                        } else {
                            player.sendMessage(getConfig().getString("Messages.Unauthorized").replace('&', '§'));
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }
}
