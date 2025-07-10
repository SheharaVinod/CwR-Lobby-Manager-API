package lk.cwresports.LobbyManager.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LobbyGroup {
    private final List<Lobby> lobbies = new ArrayList<>();
    private final String name;
    private Lobby currentLobby;
    private LobbyRotationTypes lobbyRotationType = LobbyRotationTypes.CIRCULAR;
    private final Random random = new Random();

    private TimeUnits lobbyRotationTimeUnit = TimeUnits.MANUAL;
    private long nextRotationTime = -1; // Timestamp for next rotation


    public LobbyGroup(String name) {
        this.name = name;

        LobbyManager.getInstance().registerLobbyGroup(this);
    }

    public Lobby getCurrentLobby() {
        if (currentLobby == null) {
            if (lobbies.isEmpty()) return null;
            else changeCurrentLobby();
        }
        return currentLobby;
    }

    public void changeCurrentLobby() {
        // lobby change functionalities.
        if (lobbies.size() == 1) {
            this.currentLobby = lobbies.get(0);
            return;
        }

        if (lobbyRotationType == LobbyRotationTypes.RANDOM) {
            int nextInt = random.nextInt(lobbies.size());
            this.currentLobby = lobbies.get(nextInt);
        } else if (lobbyRotationType == LobbyRotationTypes.CIRCULAR) {
            int currentIndex = this.lobbies.indexOf(this.currentLobby);
            if (currentIndex == this.lobbies.size() - 1) {
                this.currentLobby = this.lobbies.get(0);
            } else {
                this.currentLobby = this.lobbies.get(currentIndex + 1);
            }
        }
    }

    public String getLobbyRotationType() {
        return this.lobbyRotationType.toString();
    }

    public boolean setLobbyRotationType(String lobbyRotationType) {
        try {
            this.lobbyRotationType = LobbyRotationTypes.valueOf(lobbyRotationType);
        } catch (Exception e) {
            return false;
        }
        return true;
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

        // If we're removing the current lobby, set it to null
        if (currentLobby == lobby) {
            currentLobby = null;
        }
    }

    public boolean hasLobby(Lobby lobby) {
        return this.lobbies.contains(lobby);
    }

    public void setCurrentLobby(Lobby currentLobby) {
        this.currentLobby = currentLobby;
    }

    public TimeUnits getLobbyRotationTimeUnit() {
        return lobbyRotationTimeUnit;
    }

    public void setLobbyRotationTimeUnit(TimeUnits lobbyRotationTimeUnit) {
        this.lobbyRotationTimeUnit = lobbyRotationTimeUnit;
    }

    public long getNextRotationTime() {
        return nextRotationTime;
    }

    public void setNextRotationTime(long nextRotationTime) {
        this.nextRotationTime = nextRotationTime;
    }
}
