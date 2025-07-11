package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {

    private final CwRLobbyAPI plugin;
    private final File playerDataFolder;
    private PlayerDataSQLManager sqlManager;
    private final boolean useSQL;

    public PlayerDataManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        this.useSQL = plugin.getConfig().getBoolean("use-sql", false);
        if (useSQL) {
            sqlManager = new PlayerDataSQLManager(plugin);
        }
        playerDataFolder = new File(plugin.getDataFolder(), "player_data");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playerDataFolder, uuid.toString() + ".yml");
    }

    public FileConfiguration getPlayerData(UUID uuid) {
        if (useSQL) {
            // For SQL, we only store selected group, so create a YamlConfiguration with that data
            YamlConfiguration config = new YamlConfiguration();
            String group = sqlManager.getSelectedGroup(uuid);
            config.set("selected-group", group);
            return config;
        } else {
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
    }

    public void savePlayerData(UUID uuid, FileConfiguration config) {
        if (useSQL) {
            String group = config.getString("selected-group", "default");
            sqlManager.setSelectedGroup(uuid, group);
        } else {
            File playerFile = getPlayerFile(uuid);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save player data file for " + uuid + ": " + e.getMessage());
            }
        }
    }

    public void setAndSave(Player player, String configSection, Object value) {
        FileConfiguration playerData = getPlayerData(player.getUniqueId());
        playerData.set(configSection, value);
        savePlayerData(player.getUniqueId(), playerData);
    }

    public String getSelectedGroup(Player player) {
        if (useSQL) {
            return sqlManager.getSelectedGroup(player.getUniqueId());
        } else {
            File playerFile = getPlayerFile(player.getUniqueId());
            if (!playerFile.exists()) {
                return "default";
            }
            FileConfiguration config = getPlayerData(player.getUniqueId());
            return config.getString("selected-group", "default");
        }
    }

    public void deleteFileOf(Player player) {
        if (useSQL) {
            sqlManager.setSelectedGroup(player.getUniqueId(), "default");
        } else {
            File playerFile = getPlayerFile(player.getUniqueId());
            if (playerFile.exists()) {
                playerFile.delete();
            }
        }
    }

    public void savePlayer(Player player) {
        // This method can be used to save player data explicitly if needed
        FileConfiguration config = getPlayerData(player.getUniqueId());
        savePlayerData(player.getUniqueId(), config);
    }

    public void close() {
        if (useSQL && sqlManager != null) {
            sqlManager.close();
        }
    }
}
