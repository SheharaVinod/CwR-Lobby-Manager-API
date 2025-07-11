package lk.cwresports.LobbyManager.API;

import org.bukkit.Location;

public class GroupLobbies extends Lobby {
    public GroupLobbies(Location currentLocation) {
        super(currentLocation);
        this.addSpawnLocation(currentLocation);
        this.setDefaultSpawnLocation(currentLocation);
        if (!LobbyManager.getInstance().isLoading) {
            LobbyManager.getInstance().getDefaultGroup().addLobby(this);
        }
    }
}
