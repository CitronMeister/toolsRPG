package org.shyni.RPGTools.Listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.shyni.RPGTools.util.LevelManager;


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
}
