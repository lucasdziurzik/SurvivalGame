package me.lucas.myfirstplugin;

import me.lucas.myfirstplugin.listeners.*;
import me.lucas.myfirstplugin.listeners.commands.SGameCommand;
import me.lucas.myfirstplugin.methods.survivalGame;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalGamePlugin extends JavaPlugin implements Listener {
    public static survivalGame _currentGame;
    public static Server server;
    @Override
    public void onEnable() {
        System.out.println("The plugin has started.");
        server = this.getServer();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new onTakeDamageListener(), this);
        getServer().getPluginManager().registerEvents(new onEntityDeathListener(), this);
        getCommand("sgame").setExecutor((new SGameCommand(this.getServer())));
    }
}
