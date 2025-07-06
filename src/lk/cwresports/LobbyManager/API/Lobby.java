package lk.cwresports.LobbyManager.API;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Lobby {
    private final World world;
    private final List<Location> spawn_locations = new ArrayList<>();
    private final Location defaultSpawnLocation;
    private Location nextLocation;
    private NextLocationTypes locationTypes = NextLocationTypes.DEFAULT;
    private final Random random = new Random();

    public Lobby(Location currentLocation) {
        this.world = currentLocation.getWorld();
        this.defaultSpawnLocation = currentLocation;
        spawn_locations.add(currentLocation);
        this.nextLocation = currentLocation;

        LobbyManager.getInstance().registerNameFor(this);
    }

    public void setLocationTypes(NextLocationTypes locationTypes) {
        this.locationTypes = locationTypes;
    }

    public void addSpawnLocation(Location location) {
        if (location.getWorld() == this.world) {
            spawn_locations.add(location);
        }
    }

    public World getWorld() {
        return world;
    }

    public void removeSpawnLocation(int index) {
        spawn_locations.remove(index);
    }

    public boolean isEventLobby() {
        return false;
    }

    public void send(Player player) {
        player.teleport(getNextLocation());
    }

    private Location getNextLocation() {
        if (this.spawn_locations.size() <= 1) {
            return defaultSpawnLocation;
        }

        if (this.locationTypes == NextLocationTypes.DEFAULT) {
            return defaultSpawnLocation;
        } else if (this.locationTypes == NextLocationTypes.RANDOM) {
            int nextInt = random.nextInt(spawn_locations.size());
            this.nextLocation = spawn_locations.get(nextInt);
        } else if (this.locationTypes == NextLocationTypes.CIRCULAR) {
            int currentIndex = this.spawn_locations.indexOf(this.nextLocation);
            if (currentIndex == this.spawn_locations.size() - 1) {
                this.nextLocation = this.spawn_locations.get(0);
            } else {
                this.nextLocation = this.spawn_locations.get(currentIndex + 1);
            }
        }
        return nextLocation;
    }

    public Location getDefaultSpawnLocation() {
        return defaultSpawnLocation;
    }

    public List<Location> getSpawnLocations() {
        return spawn_locations;
    }

    public NextLocationTypes getLocationTypes() {
        return locationTypes;
    }

    public UUID getWorldUID() {
        return world.getUID();
    }
}
