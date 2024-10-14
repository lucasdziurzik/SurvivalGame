package me.lucas.myfirstplugin.listeners;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;



public class onRightClickListener implements Listener {
    @EventHandler
    public void onRightClick (PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem().getType() == Material.STICK) {
                p.sendMessage("You used your healing wand and gained 2 hearts !");
                p.setHealth(Math.min(p.getHealth() + 4, 20));
            }
        }
    }
}
