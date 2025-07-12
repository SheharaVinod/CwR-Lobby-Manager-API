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
