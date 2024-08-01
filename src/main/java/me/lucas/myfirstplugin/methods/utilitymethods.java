package me.lucas.myfirstplugin.methods;

import org.bukkit.ChatColor;
import me.lucas.myfirstplugin.SurvivalGamePlugin;

/* Utility methods that may be used for debugging */
public class utilitymethods {
    private final String _userName = "Panneracetamol";
    private final ChatColor _debugPrefixColor = ChatColor.BLUE;
    private final ChatColor _exceptionPrefixColor = ChatColor.RED;
    private final ChatColor _textColor = ChatColor.WHITE;


    public void SendDebugMessage(String message) {
        String finalMessage = _debugPrefixColor + "[DEBUG] " + _textColor + message;
        SurvivalGamePlugin.server.getPlayer(_userName).sendMessage(finalMessage);
    }

    public void SendExceptionMessage(String message) {
        String finalMessage = _exceptionPrefixColor + "[EXCEPTION] " + _textColor + message;
        SurvivalGamePlugin.server.getPlayer(_userName).sendMessage(finalMessage);
    }
}
