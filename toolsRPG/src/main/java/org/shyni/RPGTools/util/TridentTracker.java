package org.shyni.RPGTools.util;

import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TridentTracker {
    private static final Map<UUID, ItemStack> thrownTridents = new HashMap<>();
    
    public static void trackTrident(Trident trident, ItemStack item) {
        thrownTridents.put(trident.getUniqueId(), item.clone());
    }
    
    public static ItemStack getTridentItem(Trident trident) {
        return thrownTridents.get(trident.getUniqueId());
    }
    
    public static void removeTrident(Trident trident) {
        thrownTridents.remove(trident.getUniqueId());
    }
    
    public static void clear() {
        thrownTridents.clear();
    }
}
