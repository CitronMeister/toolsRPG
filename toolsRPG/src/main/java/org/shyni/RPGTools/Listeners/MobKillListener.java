package org.shyni.RPGTools.Listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.shyni.RPGTools.WeaponLevelManager;
import org.shyni.RPGTools.util.ActionBarUtil;
import org.shyni.RPGTools.util.Keys;

public class MobKillListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null) {
            WeaponLevelManager.handleKill(killer, entity);

            // Do something when the player kills a mob
            killer.sendMessage("You killed a " + event.getEntity().getType().name());
        }
    }
}
