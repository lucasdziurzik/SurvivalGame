package me.lucas.myfirstplugin.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import me.lucas.myfirstplugin.SurvivalGamePlugin;
import me.lucas.myfirstplugin.methods.survivalGame;

public class onEntityDeathListener implements Listener {
    @EventHandler
    /* Triggers when an entity dies. Is used for when a player dies and when a monster dies */
    public void onEntityDeath(EntityDeathEvent e) {
        
        LivingEntity entitykilled = e.getEntity();
        survivalGame currentGame = SurvivalGamePlugin._currentGame;

        /* If a player is killed, you remove it from the game */
        /* TODO IMPROVEMENT
          Instead of using this event, check the player death with the playertakesdamage event and cancel the death
            => removes the respawn menu
               you can put the player in spectator mode */
        if (entitykilled instanceof Player) {
            if (currentGame.CheckPlayerIsInTheGameAndAlive((Player) entitykilled)) {
                currentGame.KillPlayer((Player) entitykilled);

            }
        } else {
            /* If an entity that was not summoned by the game is killed, you do not want to act */
            if (currentGame.CheckEntityIsInTheGame(entitykilled.getEntityId())) {
                currentGame.KillEntity(entitykilled.getEntityId());
                e.getDrops().clear();
                if (entitykilled.getKiller() != null)
                    currentGame.AddKillPoint(entitykilled.getKiller().getName());
            }
        }
    }
}
