package com.github.nescaaallz.arenasetada.listeners;

import com.github.nescaaallz.arenasetada.ArenaSetada;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {

    protected final ArenaSetada plugin;

    public ArenaListener(ArenaSetada plugin) {
        this.plugin = plugin;
    }

    public ArenaSetada getPlugin() {
        return plugin;
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        if(getPlugin().getArenaPlayers().contains(e.getPlayer())) {
            e.getPlayer().teleport(getPlugin().getSaida());
            e.getPlayer().getInventory().clear();
            e.getPlayer().getInventory().setArmorContents(null);
            e.getPlayer().setHealth(0.0D);
            getPlugin().getArenaPlayers().remove(e.getPlayer());
        }
    }

    @EventHandler
    void onKick(PlayerKickEvent e) {
        if(getPlugin().getArenaPlayers().contains(e.getPlayer())) {
            e.getPlayer().teleport(getPlugin().getSaida());
            e.getPlayer().getInventory().clear();
            e.getPlayer().getInventory().setArmorContents(null);
            e.getPlayer().setHealth(0.0D);
            getPlugin().getArenaPlayers().remove(e.getPlayer());
        }
    }

    @EventHandler
    void onMove(PlayerMoveEvent e) {
        if(!getPlugin().getArenaPlayers().contains(e.getPlayer())) {
            removeArenaItems(e.getPlayer());
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent e) {
        if(getPlugin().getArenaPlayers().contains(e.getEntity())) {
            getPlugin().getArenaPlayers().remove(e.getEntity());
            if(e.getEntity().getKiller() instanceof Player && getPlugin().getArenaPlayers().contains(e.getEntity().getKiller())) {
                getPlugin().setInventory(e.getEntity().getKiller());
            }
        }
    }

    public boolean isArenaItem(ItemStack itemStack) {
        if(itemStack != null) {
            if(itemStack.getItemMeta() != null) {
                for(String lore : getPlugin().getConfig().getStringList("Inventory.Fingerprint")) {
                    if(itemStack.getItemMeta().getLore().contains(lore.replace('&', '§'))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int removeArenaItems(Player player) {

        int removedItems = 0;

        if(isArenaItem(player.getInventory().getHelmet())) {
            player.getInventory().setHelmet(null);
            removedItems++;
        }
        if(isArenaItem(player.getInventory().getChestplate())) {
            player.getInventory().setChestplate(null);
            removedItems++;
        }
        if(isArenaItem(player.getInventory().getLeggings())) {
            player.getInventory().setLeggings(null);
            removedItems++;
        }
        if(isArenaItem(player.getInventory().getBoots())) {
            player.getInventory().setBoots(null);
            removedItems++;
        }

        for(int slot = 0; slot < 35; slot++) {
            if(isArenaItem(player.getInventory().getItem(slot))) {
                player.getInventory().setItem(slot, null);
                removedItems++;
            }
        }

        return removedItems;
    }
}