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
                        if(getPlugin().isUnstable()) return true;
                        if(getPlugin().getArenaPlayers().contains(player)) {
                            player.sendMessage(getConfig().getString("Messages.Already").replace('&', '§'));
                        } else {
                            if(emptyInventory(player)) {
                                player.sendMessage("§4Você tem algum item no inventário!");
                                return true;
                            }
                            try {
                                player.teleport(getPlugin().getEntrada());
                            } catch (Exception e) {
                                player.sendMessage("§4A localização da arena não está definida, reporte a algum STAFF!");
                                return true;
                            }
                            getPlugin().setInventory(player, false);
                            getPlugin().getArenaPlayers().add(player);
                            player.sendMessage(getConfig().getString("Messages.Join").replace('&', '§'));
                        }
                        break;
                    }
                    case "sair": {
                        if(getPlugin().isUnstable()) return true;
                        if(getPlugin().getArenaPlayers().contains(player)) {
                            player.getInventory().setArmorContents(null);
                            player.getInventory().clear();
                            try {
                                player.teleport(getPlugin().getSaida());
                            } catch (Exception e) {
                                player.sendMessage("§4A localização da saída não está definida, reporte a algum STAFF!");
                                return true;
                            }
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
                            if(getPlugin().getArenaPlayers().contains(player)) {
                                player.sendMessage("§4Saia da arena antes de setar o inventário.");
                                return true;
                            }
                            getPlugin().setInventory(player, true);
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

    public boolean emptyInventory(Player player) {
        if(player.getInventory().getHelmet() != null) {
            return false;
        }
        if(player.getInventory().getChestplate() != null) {
            return false;
        }
        if(player.getInventory().getLeggings() != null) {
            return false;
        }
        if(player.getInventory().getBoots() != null) {
            return false;
        }
        for(int x = 0; x < 35; x++) {
            if(player.getInventory().getItem(x) != null) {
                return false;
            }
        }
        return true;
    }
}