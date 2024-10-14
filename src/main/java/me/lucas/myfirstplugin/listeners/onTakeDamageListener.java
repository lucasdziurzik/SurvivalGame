package me.lucas.myfirstplugin.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.lucas.myfirstplugin.SurvivalGamePlugin;
import me.lucas.myfirstplugin.methods.survivalGame;

public class onTakeDamageListener implements Listener {
    @EventHandler
    /* Triggered when an entity (monster or player) takes damage.
    Can be used to customize damage taken */
    public void onTakeDamage(EntityDamageByEntityEvent e) {
        survivalGame currentgame = SurvivalGamePlugin._currentGame;
        if (e.getEntity() instanceof Player) {
            e.setDamage(currentgame.CalculateDamageOnPlayer(e.getDamager().getType()));
            if (e.getDamager().getType() == EntityType.CAVE_SPIDER)
                currentgame.ApplySlowness((Player) e.getEntity());
        } else {
            if (e.getEntity().getType() == EntityType.BLAZE)
                e.setDamage(7);
            else e.setDamage(100);
        }
   }
}
