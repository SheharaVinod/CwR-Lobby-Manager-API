/*
    CwR Lobby Manager API - Minecraft plugin for managing multiple spawn lobbies
    Copyright (C) 2025 SheharaVinod(AKN Mr_Unknown), Team CwR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/


package lk.cwresports.LobbyManager;

import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.Commands.LobbyManagerCommand;
import lk.cwresports.LobbyManager.Commands.SelectSpawnCommand;
import lk.cwresports.LobbyManager.Commands.SpawnCommand;
import lk.cwresports.LobbyManager.ConfigAndData.PlayerDataManager;
import lk.cwresports.LobbyManager.Events.*;
import lk.cwresports.LobbyManager.Tabs.LobbyManagerTab;
import lk.cwresports.LobbyManager.Tabs.SelectSpawnTab;
import lk.cwresports.LobbyManager.Utils.RotationCalculator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
        RotationCalculator.init(this);

        new BukkitRunnable() {
            // this will run after all world are loaded.
            @Override
            public void run() {
                // load lobby manager;
                LobbyManager.getInstance().load();
                LobbyManager.getInstance().startRotationScheduler();
            }
        }.runTaskLater(this, 1);

        // commands
        getCommand(SpawnCommand.NAME).setExecutor(new SpawnCommand(this));
        SpawnCommand.set_cool_down(getConfig().getInt(SpawnCommand.CONFIG_SPAWN_COOL_DOWN, 5), false);

        boolean shouldHide = getConfig().getBoolean("hide-lobby-manager-command", false);
        if (!shouldHide) {
            getCommand(LobbyManagerCommand.NAME).setExecutor(new LobbyManagerCommand(this));
            getCommand(LobbyManagerCommand.NAME).setTabCompleter(new LobbyManagerTab());
        }

        getCommand(SelectSpawnCommand.NAME).setExecutor(new SelectSpawnCommand(playerData));
        getCommand(SelectSpawnCommand.NAME).setTabCompleter(new SelectSpawnTab());

        // events
        PlayerJoinToServer.register(this);
        PlayerLeaveListener.register(this);
        PlayerFallToVoidListener.register(this);
        PlayerHungryListener.register(this);
        PlayerTakeDamage.register(this);
        WhenPlayerInteractLobbyBlocks.register(this);
    }

    @Override
    public void onDisable() {
        LobbyManager.getInstance().stopRotationScheduler();
        LobbyManager.getInstance().save();
        getPlayerDataManager().close();
    }

    public static CwRLobbyAPI getPlugin() {
        return plugin;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerData;
    }
}
