package lk.cwresports.LobbyManager.API;

import org.bukkit.Location;

public class GroupLobbies extends Lobby {
    public GroupLobbies(Location currentLocation) {
        super(currentLocation); // This adds the first spawn location
        // Don't add it again here - the parent constructor already did it
        this.setDefaultSpawnLocation(currentLocation);

        if (!LobbyManager.getInstance().isLoading) {
            LobbyManager.getInstance().getDefaultGroup().addLobby(this);
        }
    }
}
