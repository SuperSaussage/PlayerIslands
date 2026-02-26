package com.github.gavvydizzle.playerislands.upgrade;

public class HopperUpgrade extends Upgrade {

    private final int maxHoppers;

    public HopperUpgrade(String permission, int price, int maxHoppers) {
        super(permission, price);
        this.maxHoppers = maxHoppers;
    }

    public int getMaxHoppers() {
        return maxHoppers;
    }
}
