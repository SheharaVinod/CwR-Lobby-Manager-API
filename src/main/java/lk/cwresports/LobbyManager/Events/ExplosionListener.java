package lk.cwresports.LobbyManager.Events;

import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

public class ExplosionListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        World world = event.getEntity().getWorld();
        if (!LobbyManager.getInstance().isInALobby(world)) return;

        Lobby lobby = LobbyManager.getInstance().getLobbyByName(world.getName());
        if (lobby != null && !lobby.isEntityExplosion()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        World world = event.getBlock().getWorld();
        if (!LobbyManager.getInstance().isInALobby(world)) return;

        Lobby lobby = LobbyManager.getInstance().getLobbyByName(world.getName());
        if (lobby != null && !lobby.isBlockExplosion()) {
            event.setCancelled(true);
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ExplosionListener(), plugin);
    }
}
