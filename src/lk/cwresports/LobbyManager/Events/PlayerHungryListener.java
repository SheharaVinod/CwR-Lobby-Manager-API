package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.Plugin;

public class PlayerHungryListener implements Listener {

    @EventHandler
    public void onPlayerHungry(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!LobbyManager.getInstance().isInALobby(player)) {
                return;
            }

            String worldName = player.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);
            if (lobby == null) {
                return;
            }
            if (lobby.isDisabledHunger()) {
                event.setCancelled(true);
            }
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerHungryListener(), plugin);
    }
}
