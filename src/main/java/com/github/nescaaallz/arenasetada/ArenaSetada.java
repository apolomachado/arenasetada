package com.github.nescaaallz.arenasetada;

import com.github.nescaaallz.arenasetada.commands.ArenaCommand;
import com.github.nescaaallz.arenasetada.listeners.ArenaListener;
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
        getServer().getConsoleSender().sendMessage(String.format("ArenaSetada | Inventário carregado com %i erro(s).", loadInventory()));
        if(loadLocation(LocationType.Entrada) && loadLocation(LocationType.Saida)) {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Localização carregada com sucesso.");
        } else {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Houve um erro ao carregar a localização da arena.");
        }
        if(unstable) {
            getServer().getConsoleSender().sendMessage("ArenaSetada | Houveram erros ao carregar a configuração do sistema e o plugin foi desabilitado.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public int loadInventory() {
        int errors = 0;
        for(String key : getConfig().getConfigurationSection("Inventory.Items").getKeys(false)) {
            try {
                Integer slot = Integer.parseInt(key);
                ItemStack item = getConfig().getItemStack(key);

                ItemMeta itemMeta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                for(String str : getConfig().getStringList("Inventory.Fingerprint")) {
                    lore.add(str.replace('&', '§'));
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

                getInventory().put(slot, item);
            } catch (Exception e) {
                unstable = true;
                errors++;
            }
        }
        return errors;
    }

    public void setInventory(Inventory inventory) {
        getInventory().clear();
        for(int slot = 0; slot < 35; slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if(itemStack != null) {

                getConfig().set("Inventory.Items." + slot, itemStack);
                saveConfig();

                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                for(String str : getConfig().getStringList("Inventory.Fingerprint")) {
                    lore.add(str.replace('&', '§'));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                getInventory().put(slot, itemStack);
            }
        }
        inventory.clear();
    }

    public void setInventory(Player player) {
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        for(int slot : getInventory().keySet()) {
            ItemStack itemStack = getInventory().get(slot);
            if(itemStack != null) {
                player.getInventory().setItem(slot, itemStack);
            }
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
}