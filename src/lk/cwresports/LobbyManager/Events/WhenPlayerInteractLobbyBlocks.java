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

package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class WhenPlayerInteractLobbyBlocks implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (LobbyManager.getInstance().isInALobby(event.getPlayer())) {
            World world = event.getPlayer().getWorld();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(world.getName());
            if (lobby.isCanselInteraction()) {
                event.setCancelled(true);
            }
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new WhenPlayerInteractLobbyBlocks(), plugin);
    }

}
