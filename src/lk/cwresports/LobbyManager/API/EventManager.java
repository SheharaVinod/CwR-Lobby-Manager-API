package lk.cwresports.LobbyManager.API;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventManager implements Listener {
    private static EventManager manager;
    private boolean isInEvent = false;
    private EventLobbies eventLobby;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreTeleportEvent(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            isInEvent();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreJoinEvent(PlayerLoginEvent event) {
        isInEvent();
    }

    public boolean isInEvent() {
        if (isInEvent) {
            if (!this.eventLobby.isEvent()) {
                this.isInEvent = false;
                this.eventLobby = null;
            } else {
                return isInEvent;
            }
        }

        for (EventLobbies eventLobby : LobbyManager.getInstance().getEventLobbies()) {
            if (eventLobby.isEvent()) {
                this.isInEvent = true;
                this.eventLobby = eventLobby;
                break;
            }
        }
        return this.isInEvent;
    }

    public EventLobbies getEventLobby() {
        return eventLobby;
    }

    public static EventManager getInstance() {
        if (manager == null) {
            manager = new EventManager();
            return manager;
        }
        return manager;
    }
}
