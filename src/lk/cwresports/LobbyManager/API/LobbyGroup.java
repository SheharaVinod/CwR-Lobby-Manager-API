package lk.cwresports.LobbyManager.API;

import java.util.ArrayList;
import java.util.List;

public class LobbyGroup {
    private final List<Lobby> lobbies = new ArrayList<>();
    private final String name;
    private Lobby currentLobby;

    public LobbyGroup(String name) {
        this.name = name;

        LobbyManager.getInstance().registerLobbyGroup(this);
    }

    public void saveToConfig() {
        // TODO: save this in config file.
    }

    public Lobby getCurrentLobby() {
        if (currentLobby == null) {
            // TODO:
        }
        return currentLobby;
    }

    public void changeCurrentLobby() {
        // lobby change functionalities.

    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public String getName() {
        return name;
    }

    public void addLobby(Lobby lobby) {
        this.lobbies.add(lobby);
    }

    public void removeLobby(Lobby lobby) {
        this.lobbies.remove(lobby);
    }

    public boolean hasLobby(Lobby lobby) {
        return this.lobbies.contains(lobby);
    }

    public void setCurrentLobby(Lobby currentLobby) {

    }
}
