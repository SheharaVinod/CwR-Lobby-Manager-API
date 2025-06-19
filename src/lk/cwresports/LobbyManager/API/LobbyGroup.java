package lk.cwresports.LobbyManager.API;

import java.util.ArrayList;
import java.util.List;

public class LobbyGroup {
    private final List<Lobby> lobbies = new ArrayList<>();
    private final String name;
    private Lobby currentLobby;

    public LobbyGroup(String name) {
        this.name = name;


    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void changeLobby() {
        // lobby change functianalities.
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public String getName() {
        return name;
    }

    public void addLobby(Lobby lobby) {
        this.lobbies.add(lobby);
        // TODO: save in config.
    }

    public void removeLobby(Lobby lobby) {
        this.lobbies.remove(lobby);
    }

    public boolean hasLobby(Lobby lobby) {
        return this.lobbies.contains(lobby);
    }

}
