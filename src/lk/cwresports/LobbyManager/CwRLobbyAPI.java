package lk.cwresports.LobbyManager;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import lk.cwresports.LobbyManager.Commands.SpawnCommand;
import lk.cwresports.LobbyManager.ConfigAndData.PlayerDataManager;
import lk.cwresports.LobbyManager.Events.PlayerJoinToServer;
import lk.cwresports.LobbyManager.Events.PlayerLeaveListener;
import lk.cwresports.LobbyManager.Tabs.LobbyManagerTab;
import lk.cwresports.LobbyManager.Utils.TimeZoneHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class CwRLobbyAPI extends JavaPlugin {
    public static final String PREFIX = "&7[&6CwRLobbyAPI&7]&r ";

    private static CwRLobbyAPI plugin;
    private PlayerDataManager playerData;

    @Override
    public void onLoad() {
        CwRLobbyAPI.plugin = this;
        LobbyManager.getInstance();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerData = new PlayerDataManager(this);

        // Load timezone data
        TimeZoneHelper.load(this);

        // load lobby manager;
        LobbyManager.getInstance().load();
        LobbyManager.getInstance().startRotationScheduler();

        // commands
        getCommand(SpawnCommand.NAME).setExecutor(new SpawnCommand(this));
        SpawnCommand.set_cool_down(getConfig().getInt(SpawnCommand.CONFIG_SPAWN_COOL_DOWN, 5), false);

        boolean shouldHide = getConfig().getBoolean("hide-lobby-manager-command", false);
        if (!shouldHide) {
            getCommand(LobbyManagerCommand.NAME).setExecutor(new LobbyManagerCommand(this));
            getCommand(LobbyManagerCommand.NAME).setTabCompleter(new LobbyManagerTab());
        }



        // events
        PlayerJoinToServer.register(this);
        PlayerLeaveListener.register(this);
    }

    @Override
    public void onDisable() {
        LobbyManager.getInstance().stopRotationScheduler();
        LobbyManager.getInstance().save();
    }

    public static CwRLobbyAPI getPlugin() {
        return plugin;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerData;
    }
}
