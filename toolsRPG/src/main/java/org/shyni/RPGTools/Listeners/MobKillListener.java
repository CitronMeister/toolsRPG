package org.shyni.RPGTools.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.shyni.RPGTools.util.LevelManager;
import org.shyni.RPGTools.util.ToolType;
import org.shyni.RPGTools.util.TridentTracker;


public class MobKillListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // Check if the killer is a tamed wolf (friendly wolf)
        if (entity.getKiller() instanceof Wolf wolf) {
            if (wolf.isTamed() && wolf.getOwner() instanceof Player) {
                Player owner = (Player) wolf.getOwner();
                // The wolf is friendly, and it killed a mob
                LevelManager.updateWeaponItem(owner, entity);
                // Do something when the friendly wolf kills a mob
            }
            return;
        }

        // Check if the killer is a player
        Player killerPlayer = entity.getKiller();
        if (killerPlayer != null) {
            LevelManager.updateWeaponItem(killerPlayer, entity);
            // Do something when the player kills a mob
        }


    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.TRIDENT) return;
        
        ToolType type = ToolType.fromMaterial(item.getType());
        if (type == null || !type.isWeapon()) return;
        
        TridentTracker.trackTrident(trident, item);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Trident trident)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(trident.getShooter() instanceof Player player)) return;
        
        // Check if this damage will kill the entity
        if (victim.getHealth() - event.getFinalDamage() > 0) return;
        
        ItemStack trackedItem = TridentTracker.getTridentItem(trident);
        if (trackedItem == null) return;
        
        // Update the tracked item
        LevelManager.updateThrownWeaponItem(player, victim, trackedItem);
        
        // Store the updated item back so it can be applied when picked up
        TridentTracker.trackTrident(trident, trackedItem);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        
        // Apply the updated metadata when the trident lands
        ItemStack updatedItem = TridentTracker.getTridentItem(trident);
        if (updatedItem != null) {
            trident.setItem(updatedItem);
        }
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (!(event.getArrow() instanceof Trident trident)) return;
        
        ItemStack updatedItem = TridentTracker.getTridentItem(trident);
        if (updatedItem != null) {
            event.getItem().setItemStack(updatedItem);
            TridentTracker.removeTrident(trident);
        }
    }
}
