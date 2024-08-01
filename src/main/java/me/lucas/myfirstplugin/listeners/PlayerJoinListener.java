package me.lucas.myfirstplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void setJoinMessage(PlayerJoinEvent event) {
        /* Setting a random welcome message when the player joins the server. */
        /* TODO : add the messages in config file */
        String playerName = event.getPlayer().getName();
        ChatColor messageColor = ChatColor.YELLOW;
        ChatColor playerColor = ChatColor.RED;
        List<String> joinMessages = new ArrayList<String>();
        joinMessages.add(messageColor + "Hello " + playerColor + playerName + messageColor + ". I hope you brought cookies.");
        joinMessages.add(messageColor + "What's up " + playerColor + playerName + messageColor + "? Welcome back and have fun !");
        joinMessages.add(playerColor + playerName + messageColor + ", get back to work ! Train hard to become a hypixel dev.");
        joinMessages.add(messageColor + "Welcome back " + playerColor + playerName + messageColor + ". Wonderful minigames are waiting for you.");
        Random r = new Random();
        int n = r.nextInt(joinMessages.size());
        event.setJoinMessage(ChatColor.YELLOW + joinMessages.get(n));
    }

    @EventHandler
    public void setMOTD(PlayerJoinEvent event) {
        /* Sending a message to the player with the latest news */
        /* TODO : add the news in the config file */
        Player player = event.getPlayer();
        ChatColor dashcolors = ChatColor.AQUA;
        ChatColor mainmessagecolor = ChatColor.RED;
        player.sendMessage(dashcolors + "_________________________________");
        player.sendMessage(dashcolors + "UPDATE OF THE DAY");
        player.sendMessage(mainmessagecolor + "BALANCED SOME DAMAGE VALUES");
        player.sendMessage(mainmessagecolor + "FINISHED THE HIGH QUALITY MAP");
        player.sendMessage(dashcolors + "_________________________________");

    }


}
