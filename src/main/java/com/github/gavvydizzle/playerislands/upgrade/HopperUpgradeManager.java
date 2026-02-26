package com.github.gavvydizzle.playerislands.upgrade;

import com.github.gavvydizzle.playerislands.configs.UpgradesConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class HopperUpgradeManager {

    private final ArrayList<HopperUpgrade> hopperUpgrades;

    public HopperUpgradeManager() {
        this.hopperUpgrades = new ArrayList<>();
        reload();
    }

    public void reload() {
        FileConfiguration config = UpgradesConfig.getConfig();
        config.addDefault("hopperUpgrades.default.price", 0);
        config.addDefault("hopperUpgrades.default.permission", "");
        config.addDefault("hopperUpgrades.default.maxHoppers", 0);

        hopperUpgrades.clear();

        if (!config.isConfigurationSection("hopperUpgrades")) {
            UpgradesConfig.save();
            return;
        }

        for (String s : config.getConfigurationSection("hopperUpgrades").getKeys(false)) {
            String path = "hopperUpgrades." + s + ".";
            long price = config.getLong(path + "price");
            String permission = config.getString(path + "permission", "");
            int max = config.getInt(path + "maxHoppers");

            hopperUpgrades.add(new HopperUpgrade(price, hopperUpgrades.size(), permission, max));
        }

        UpgradesConfig.save();
    }

    public HopperUpgrade getHopperUpgrade(int level) {
        if (level < 0 || level >= hopperUpgrades.size()) return null;
        return hopperUpgrades.get(level);
    }

    public int getNumHopperUpgrades() {
        return hopperUpgrades.size();
    }
}
