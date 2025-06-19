package lk.cwresports.LobbyManager;

import org.bukkit.plugin.java.JavaPlugin;

public class CwRLobbyAPI extends JavaPlugin {
    private static CwRLobbyAPI plugin;

    @Override
    public void onLoad() {
        CwRLobbyAPI.plugin = this;
    }

    @Override
    public void onEnable() {


    }


    public static CwRLobbyAPI getPlugin() {
        return plugin;
    }



}
