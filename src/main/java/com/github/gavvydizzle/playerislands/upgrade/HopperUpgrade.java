package com.github.gavvydizzle.playerislands.upgrade;

public class HopperUpgrade extends Upgrade {

    private final int maxHoppers;

    public HopperUpgrade(long price, int level, String permission, int maxHoppers) {
        super(price, level, permission);
        this.maxHoppers = maxHoppers;
    }

    public int getMaxHoppers() {
        return maxHoppers;
    }
}
