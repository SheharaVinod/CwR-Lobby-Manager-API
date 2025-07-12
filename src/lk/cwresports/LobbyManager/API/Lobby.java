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
    private Location defaultSpawnLocation;
    private Location nextLocation;
    private NextLocationTypes locationTypes = NextLocationTypes.DEFAULT;
    private final Random random = new Random();
    private int currentSpawnIndex = 0; // Track current index for circular rotation

    public Lobby(Location currentLocation) {
        this.world = currentLocation.getWorld();
        this.defaultSpawnLocation = currentLocation;
        this.nextLocation = currentLocation;
        this.spawn_locations.add(currentLocation); // Keep this line to ensure at least one spawn exists
    }

    public void addSpawnLocation(Location location) {
        if (location.getWorld() == this.world && !spawn_locations.contains(location)) {
            spawn_locations.add(location);
        }
    }
    public void setLocationTypes(NextLocationTypes locationTypes) {
        this.locationTypes = locationTypes;
    }

    public void setDefaultSpawnLocation(Location location) {
        if (location == null || location.getWorld() != this.world) return;
        this.defaultSpawnLocation = location;
        // Ensure it exists in spawn locations
        if (!spawn_locations.contains(location)) {
            spawn_locations.add(0, location);
        }
        currentSpawnIndex = 0; // Reset rotation index
    }

    public World getWorld() {
        return world;
    }

    public void removeSpawnLocation(int index) {
        if (index == 0) return;
        spawn_locations.remove(index);
    }

    public boolean isEventLobby() {
        return false;
    }

    public void send(Player player) {
        player.teleport(getNextLocation());
    }

    private Location getNextLocation() {
        if (this.spawn_locations.isEmpty()) {
            return defaultSpawnLocation;
        }

        if (this.locationTypes == NextLocationTypes.DEFAULT) {
            return defaultSpawnLocation;
        } else if (this.locationTypes == NextLocationTypes.RANDOM) {
            return spawn_locations.get(random.nextInt(spawn_locations.size()));
        } else if (this.locationTypes == NextLocationTypes.CIRCULAR) {
            // Circular rotation logic
            currentSpawnIndex = (currentSpawnIndex + 1) % spawn_locations.size();
            return spawn_locations.get(currentSpawnIndex);
        }
        return defaultSpawnLocation;
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
