package lk.cwresports.LobbyManager.API;


import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Lobby {
    private final World world;
    private final List<Location> spawn_locations = new ArrayList<>();
    private Location defaultSpawnLocation;

    private final Random random = new Random();
    private int currentSpawnIndex = 0; // Track current index for circular rotation

    private NextLocationTypes locationTypes = NextLocationTypes.DEFAULT;
    private boolean disabledHunger = true;
    private boolean disabledDamage = true;
    private boolean canselInteraction = false;

    private GameMode gameMode = GameMode.ADVENTURE;

    public Lobby(Location currentLocation) {
        this.world = currentLocation.getWorld();
        this.defaultSpawnLocation = currentLocation;

        this.spawn_locations.add(currentLocation); // Keep this line to ensure at least one spawn exists

        LobbyManager.getInstance().registerWorld(this.world);
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setDisabledHunger(boolean b) {
        this.disabledHunger = b;
    }

    public void setCanselInteraction(boolean b) {
        this.canselInteraction = b;
    }

    public void setDisabledDamage(boolean b) {
        this.disabledDamage = b;
    }

    public boolean isDisabledHunger() {
        return disabledHunger;
    }

    public boolean isCanselInteraction() {
        return canselInteraction;
    }

    public boolean isDisabledDamage() {
        return disabledDamage;
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

        if (player.hasPermission("cwr-core.lobby-manager.admin")) {
            FileConfiguration config = CwRLobbyAPI.getPlugin().getConfig();
            boolean admin_bypass = config.getBoolean("change-game-mod-of-admins-after-teleport-to-spawn", false);
            if (!admin_bypass) {
                return;
            }
        }

        player.setGameMode(getGameMode());
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
