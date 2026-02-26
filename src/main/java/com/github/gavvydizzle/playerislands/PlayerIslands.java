package com.github.gavvydizzle.playerislands;

import com.github.gavvydizzle.playerislands.commands.AdminCommandManager;
import com.github.gavvydizzle.playerislands.commands.IslandSelectionManager;
import com.github.gavvydizzle.playerislands.commands.PlayerCommandManager;
import com.github.gavvydizzle.playerislands.gui.InventoryManager;
import com.github.gavvydizzle.playerislands.gui.InventoryUI;
import com.github.gavvydizzle.playerislands.gui.IslandStatus;
import com.github.gavvydizzle.playerislands.gui.PlayerMenu;
import com.github.gavvydizzle.playerislands.gui.UpgradeUI;
import com.github.gavvydizzle.playerislands.island.IslandManager;
import com.github.gavvydizzle.playerislands.papi.IslandExpansion;
import com.github.gavvydizzle.playerislands.storage.Database;
import com.github.gavvydizzle.playerislands.storage.SQLite;
import com.github.gavvydizzle.playerislands.upgrade.UpgradeManager;
import com.github.gavvydizzle.playerislands.utils.Messages;
import com.github.gavvydizzle.playerislands.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.io.File;
import java.util.Objects;

public final class PlayerIslands extends JavaPlugin {

    private static final String TOKEN_CURRENCY_ID = "tokens";

    private static PlayerIslands instance;
    private static Currency tokenCurrency;

    private Database database;
    private IslandManager islandManager;
    private UpgradeManager upgradeManager;
    private InventoryManager inventoryManager;
    private PlayerCommandManager playerCommandManager;
    private AdminCommandManager adminCommandManager;
    private IslandSelectionManager islandSelectionManager;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled because CoinsEngine or currency '%s' was not found!", getDescription().getName(), TOKEN_CURRENCY_ID));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        createSchematicFolder();

        instance = this;
        database = new SQLite(this);
        database.createTables();

        upgradeManager = new UpgradeManager(); // Must be created before islandManager
        islandManager = new IslandManager();
        inventoryManager = new InventoryManager();
        inventoryManager.reload();

        getServer().getPluginManager().registerEvents(inventoryManager, this);

        try {
            adminCommandManager = new AdminCommandManager(Objects.requireNonNull(getCommand("islandAdmin")), islandManager);
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"islandAdmin\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            playerCommandManager = new PlayerCommandManager(Objects.requireNonNull(getCommand("island")), islandManager);
        } catch (NullPointerException e) {
            getLogger().severe("The player command name was changed in the plugin.yml file. Please make it \"island\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        islandSelectionManager = new IslandSelectionManager();

        Messages.reloadMessages();
        Sounds.reload();

        try {
            new IslandExpansion(islandManager).register();
        }
        catch (Exception ignored) {}
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("CoinsEngine") == null) {
            return false;
        }

        tokenCurrency = CoinsEngineAPI.getCurrency(TOKEN_CURRENCY_ID);
        return tokenCurrency != null;
    }

    private void createSchematicFolder() {
        try {
            File folder = new File(getDataFolder(), "schematics");
            if (!folder.exists()) folder.mkdirs();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isPluginAdmin(Player player) {
        return player.hasPermission("playerislands.islandAdmin");
    }

    public static boolean hasPrivateIslandBypassPermission(Player player) {
        return player.hasPermission("playerislands.islandAdmin.privatebypass");
    }


    public static PlayerIslands getInstance() {
        return instance;
    }

    public static Currency getTokenCurrency() {
        return tokenCurrency;
    }

    public static boolean hasTokens(Player player, double amount) {
        if (tokenCurrency == null) return false;
        return CoinsEngineAPI.getBalance(player, tokenCurrency) >= amount;
    }

    public static boolean withdrawTokens(Player player, double amount) {
        if (!hasTokens(player, amount)) return false;
        CoinsEngineAPI.removeBalance(player, tokenCurrency, amount);
        return true;
    }

    public Database getDatabase() {
        return database;
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public PlayerCommandManager getPlayerCommandManager() {
        return playerCommandManager;
    }

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }

    public IslandSelectionManager getIslandSelectionManager() {
        return islandSelectionManager;
    }
}
