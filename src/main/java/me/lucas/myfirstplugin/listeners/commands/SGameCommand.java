package me.lucas.myfirstplugin.listeners.commands;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lucas.myfirstplugin.methods.survivalGame;

public class SGameCommand implements CommandExecutor {

    private Server _server;
    public SGameCommand(Server server) {
        _server = server;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (sender instanceof Player) {
                survivalGame game = new survivalGame(_server.getWorlds().getFirst(), _server);
                try {
                    game.StartGame();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            else
                return false;

    }
}
