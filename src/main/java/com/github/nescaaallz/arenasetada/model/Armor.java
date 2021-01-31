package com.github.nescaaallz.arenasetada.model;

import com.github.nescaaallz.arenasetada.ArenaSetada;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Armor {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ArenaSetada plugin;

    public Armor(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ArenaSetada plugin) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.plugin = plugin;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public ArenaSetada getPlugin() {
        return plugin;
    }

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    public void save() {
        if(getHelmet() != null)
            getConfig().set("Inventory.Armor.Helmet", getHelmet());
        if(getChestplate() != null)
            getConfig().set("Inventory.Armor.Chestplate", getChestplate());
        if(getLeggings() != null)
            getConfig().set("Inventory.Armor.Leggings", getLeggings());
        if(getBoots() != null)
            getConfig().set("Inventory.Armor.Boots", getBoots());
        getPlugin().saveConfig();
    }

    public void equip(Player target) {

        ItemStack cHelmet, cChestplate, cLeggings, cBoots;
        ItemMeta helmetMeta, chestplateMeta, leggingsMeta, bootsMeta;
        List<String> lore = new ArrayList<>();
        for(String str : getConfig().getStringList("Inventory.Fingerprint")) {
            lore.add(str.replace('&', '§'));
        }

        if(getHelmet() != null) {
            helmetMeta = getHelmet().getItemMeta().clone();
            cHelmet = getHelmet().clone();
            helmetMeta.setLore(lore);
            cHelmet.setItemMeta(helmetMeta);
            target.getInventory().setHelmet(cHelmet);
        }
        if(getChestplate() != null) {
            chestplateMeta = getChestplate().getItemMeta().clone();
            cChestplate = getChestplate().clone();
            chestplateMeta.setLore(lore);
            cChestplate.setItemMeta(chestplateMeta);
            target.getInventory().setChestplate(cChestplate);
        }
        if(getLeggings() != null) {
            leggingsMeta = getLeggings().getItemMeta().clone();
            cLeggings = getLeggings().clone();
            leggingsMeta.setLore(lore);
            cLeggings.setItemMeta(leggingsMeta);
            target.getInventory().setLeggings(cLeggings);
        }
        if(getBoots() != null) {
            bootsMeta = getBoots().getItemMeta().clone();
            cBoots = getBoots().clone();
            bootsMeta.setLore(lore);
            cBoots.setItemMeta(bootsMeta);
            target.getInventory().setBoots(cBoots);
        }

        lore.clear();
    }
}