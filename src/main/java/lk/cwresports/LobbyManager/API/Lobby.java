/*
    CwR Lobby Manager API - Minecraft plugin for managing multiple spawn lobbies
    Copyright (C) 2025 SheharaVinod(AKN Mr_Unknown), Team CwR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package lk.cwresports.LobbyManager.API;


import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    private boolean doNaturalMobSpawning = false;
    private boolean doDayNightCircle = false;
    private boolean entityExplosion = false;
    private boolean blockExplosion = false;
    private boolean realWorldSync = false;
    private String timeZoneId = null;
    private long customCycleLengthMs = -1;
    private long cycleReferenceTime = 0;

    public Lobby(Location currentLocation) {
        this.world = currentLocation.getWorld();
        this.defaultSpawnLocation = currentLocation;

        this.spawn_locations.add(currentLocation); // Keep this line to ensure at least one spawn exists

        LobbyManager manager = LobbyManager.getInstance();
        manager.registerWorld(this.world);
        manager.registerNameFor(this);
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

    public boolean isDoNaturalMobSpawning() {
        return doNaturalMobSpawning;
    }

    public void setDoNaturalMobSpawning(boolean doNaturalMobSpawning) {
        this.doNaturalMobSpawning = doNaturalMobSpawning;
        if (!doNaturalMobSpawning) {
            world.setGameRuleValue("doMobSpawning", "false");
        } else {
            world.setGameRuleValue("doMobSpawning", "true");
        }
    }

    public boolean isDoDayNightCircle() {
        return doDayNightCircle;
    }

    public void setDoDayNightCircle(boolean doDayNightCircle) {
        this.doDayNightCircle = doDayNightCircle;
        if (isCustomTimeActive()) return;
        if (!doDayNightCircle) {
            world.setGameRuleValue("doDaylightCycle", "false");
        } else {
            world.setGameRuleValue("doDaylightCycle", "true");
        }
    }

    public boolean isEntityExplosion() {
        return entityExplosion;
    }

    public void setEntityExplosion(boolean entityExplosion) {
        this.entityExplosion = entityExplosion;
    }

    public boolean isBlockExplosion() {
        return blockExplosion;
    }

    public void setBlockExplosion(boolean blockExplosion) {
        this.blockExplosion = blockExplosion;
    }

    public boolean isRealWorldSync() {
        return realWorldSync;
    }

    public void setRealWorldSync(boolean realWorldSync) {
        this.realWorldSync = realWorldSync;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public long getCustomCycleLengthMs() {
        return customCycleLengthMs;
    }

    public void setCustomCycleLengthMs(long customCycleLengthMs) {
        this.customCycleLengthMs = customCycleLengthMs;
        if (customCycleLengthMs > 0) {
            this.cycleReferenceTime = System.currentTimeMillis();
        }
    }

    public boolean isCustomTimeActive() {
        return realWorldSync || customCycleLengthMs > 0;
    }

    public void resetTimeSettings() {
        this.realWorldSync = false;
        this.timeZoneId = null;
        this.customCycleLengthMs = -1;
        this.cycleReferenceTime = 0;
        world.setGameRuleValue("doDaylightCycle", "true");
    }

    public long calculateTargetTime() {
        if (realWorldSync && timeZoneId != null) {
            try {
                ZoneId zone = ZoneId.of(timeZoneId);
                ZonedDateTime now = ZonedDateTime.now(zone);
                int secondsSinceMidnight = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
                int offsetSeconds = (secondsSinceMidnight - 21600 + 86400) % 86400;
                return (offsetSeconds * 24000L / 86400) % 24000;
            } catch (Exception e) {
                return world.getTime();
            }
        } else if (customCycleLengthMs > 0) {
            long elapsed = (System.currentTimeMillis() - cycleReferenceTime) % customCycleLengthMs;
            return (elapsed * 24000L / customCycleLengthMs) % 24000;
        }
        return world.getTime();
    }

    public void applyWorldSettings() {
        setDoNaturalMobSpawning(doNaturalMobSpawning);
        if (!isCustomTimeActive()) {
            setDoDayNightCircle(doDayNightCircle);
        }
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
