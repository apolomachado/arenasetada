package com.github.nescaaallz.arenasetada;

import com.github.nescaaallz.arenasetada.commands.ArenaCommand;
import com.github.nescaaallz.arenasetada.listeners.ArenaListener;
import com.github.nescaaallz.arenasetada.model.Armor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaSetada extends JavaPlugin {

    private List<Player> arenaPlayers = new ArrayList<>();
    private Location entrada;
    private Location saida;
    private HashMap<Integer, ItemStack> inventory = new HashMap<>();
    private Armor armor;

    private Listener arenaListener;

    public enum LocationType { Entrada, Saida }

    private boolean unstable = false;

    public List<Player> getArenaPlayers() {
        return arenaPlayers;
    }

    public Location getEntrada() {
        return entrada;
    }

    public Location getSaida() {
        return saida;
    }

    public HashMap<Integer, ItemStack> getInventory() {
        return inventory;
    }

    public Armor getArmor() {
        return armor;
    }

    public boolean isUnstable() {
        return unstable;
    }

    public void onEnable() {
        saveDefaultConfig();
        initialLoad();
        this.arenaListener = new ArenaListener(this);
        getServer().getPluginManager().registerEvents(arenaListener, this); getServer().getConsoleSender().sendMessage("ArenaSetada | Eventos registrados com sucesso.");
        getCommand("arena").setExecutor(new ArenaCommand(this)); getServer().getConsoleSender().sendMessage("ArenaSetada | Comando registrado com sucesso.");
    }

    public void onDisable() {
        for(Player player : getArenaPlayers()) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.teleport(getSaida());
            player.sendMessage(getConfig().getString("Messages.Left").replace('&', '§'));
        }
        getArenaPlayers().clear();
    }

    public void initialLoad() {
        getServer().getConsoleSender().sendMessage(String.format("ArenaSetada | Inventário carregado com %s erro(s).", String.valueOf(loadInventory())));
        if(loadLocation(LocationType.Entrada) && loadLocation(LocationType.Saida)) {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Localização carregada com sucesso.");
        } else {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Houve um erro ao carregar a localização da arena.");
        }
        if(unstable) {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Houveram erros ao carregar a configuração do sistema e algumas funcionalidades do plugin foram desabilitadas.");
        }
    }

    public int loadInventory() {
        int errors = 0;

        this.armor = new Armor(getConfig().getItemStack("Inventory.Armor.Helmet"), getConfig().getItemStack("Inventory.Armor.Chestplate"), getConfig().getItemStack("Inventory.Armor.Leggings"), getConfig().getItemStack("Inventory.Armor.Boots"), this);

        for(String key : getConfig().getConfigurationSection("Inventory.Items").getKeys(false)) {
            try {
                Integer slot = Integer.parseInt(key);
                ItemStack item = getConfig().getItemStack("Inventory.Items." + key);
                if(item != null) {
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    for(String str : getConfig().getStringList("Inventory.Fingerprint")) {
                        lore.add(str.replace('&', '§'));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
                getInventory().put(slot, item);
            } catch (NumberFormatException e) {
                unstable = true;
                errors++;
            }
        }
        return errors;
    }

    public void setInventory(Player player, boolean adminAction) {
        if(adminAction) {

            this.armor = new Armor(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots(), this);
            getArmor().save();

            getConfig().set("Inventory.Items", null);
            saveConfig();

            for(int slot = 0; slot < 35; slot++) {
                ItemStack itemStack = player.getInventory().getItem(slot);
                if(itemStack != null) {
                    getConfig().set("Inventory.Items." + slot, itemStack);
                    saveConfig();
                    getInventory().put(slot, itemStack);
                }
            }

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

        } else {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            for(int slot : getInventory().keySet()) {
                ItemStack itemStack = getInventory().get(slot);
                if(itemStack != null) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    for(String str : getConfig().getStringList("Inventory.Fingerprint")) {
                        lore.add(str.replace('&', '§'));
                    }
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    player.getInventory().setItem(slot, itemStack);
                }
            }
            getArmor().equip(player);
        }
    }

    public boolean loadLocation(LocationType locationType) {
        try {
            double x, y, z;
            float yaw, pitch;
            String world = getConfig().getString("Location." + locationType.name() + ".World");
            x = getConfig().getDouble("Location." + locationType.name() + ".X");
            y = getConfig().getDouble("Location." + locationType.name() + ".Y");
            z = getConfig().getDouble("Location." + locationType.name() + ".Z");
            yaw = (float) getConfig().getDouble("Location." + locationType.name() + ".Yaw");
            pitch = (float) getConfig().getDouble("Location." + locationType.name() + ".Pitch");

            if(locationType == LocationType.Entrada) {
                this.entrada = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            } else {
                this.saida = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            }
            return true;
        } catch(NullPointerException e) {
            unstable = true;
            return false;
        }
    }

    public void setLocation(Location location, LocationType locationType) {
        double x, y, z;
        float yaw, pitch;
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        getConfig().set("Location." + locationType.name() + ".World", location.getWorld().getName());
        getConfig().set("Location." + locationType.name() + ".X", x);
        getConfig().set("Location." + locationType.name() + ".Y", y);
        getConfig().set("Location." + locationType.name() + ".Z", z);
        getConfig().set("Location." + locationType.name() + ".Yaw", yaw);
        getConfig().set("Location." + locationType.name() + ".Pitch", pitch);

        saveConfig();

        if(locationType == LocationType.Entrada) {
            this.entrada = location;
        } else {
            this.saida = location;
        }
    }

    public void clearConfigurationSectionKeys(String configurationSection) {
        for(String str : getConfig().getConfigurationSection(configurationSection).getKeys(false)) {
            getConfig().set(str, null);
        }
    }
}