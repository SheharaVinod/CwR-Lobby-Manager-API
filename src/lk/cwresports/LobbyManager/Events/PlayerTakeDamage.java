package lk.cwresports.LobbyManager.Events;


import lk.cwresports.LobbyManager.API.Lobby;
import lk.cwresports.LobbyManager.API.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class PlayerTakeDamage implements Listener {

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!LobbyManager.getInstance().isInALobby(player)) {
                return;
            }

            String worldName = player.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);
            if (lobby == null) {
                return;
            }

            if (lobby.isDisabledDamage()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!LobbyManager.getInstance().isInALobby(player)) {
                return;
            }

            String worldName = player.getWorld().getName();
            Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);
            if (lobby == null) {
                return;
            }

            if (lobby.isDisabledDamage()) {
                event.setCancelled(true);
            }
        }
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerTakeDamage(), plugin);
    }

}
