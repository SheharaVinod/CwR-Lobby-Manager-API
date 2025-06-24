package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.API.LobbyGroup;
import lk.cwresports.LobbyManager.API.LobbyManager;
import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataManager {

    private final CwRLobbyAPI plugin;
    private final File playerDataFolder;

    public PlayerDataManager(CwRLobbyAPI plugin) {
        playerDataFolder = new File(plugin.getDataFolder(), "player_data");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        this.plugin = plugin;
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playerDataFolder, uuid.toString() + ".yml");
    }


    public FileConfiguration getPlayerData(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create player data file for " + uuid + ": " + e.getMessage());
            }
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayerData(UUID uuid, FileConfiguration config) {
        File playerFile = getPlayerFile(uuid);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data file for " + uuid + ": " + e.getMessage());
        }
    }

    public void setAndSave(Player player, String configSection, Objects value) {
        FileConfiguration playerData = getPlayerData(player.getUniqueId());
        playerData.set(configSection, value);
        savePlayerData(player.getUniqueId(), playerData);
    }

    public void savePlayer(Player player) {
        LobbyManager manager = LobbyManager.getInstance();
        LobbyGroup group = manager.getLobbyGroup("default");
    }
}
