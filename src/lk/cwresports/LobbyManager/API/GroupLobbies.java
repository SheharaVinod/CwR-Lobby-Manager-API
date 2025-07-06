package lk.cwresports.LobbyManager.API;

import org.bukkit.Location;

public class GroupLobbies  extends Lobby{
    public GroupLobbies(Location currentLocation) {
        super(currentLocation);

        // add to default group threw Manager
        LobbyManager.getInstance().getDefaultGroup().addLobby(this);
    }
}
