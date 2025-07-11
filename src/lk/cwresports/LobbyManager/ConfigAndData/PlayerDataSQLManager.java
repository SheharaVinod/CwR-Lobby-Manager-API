package lk.cwresports.LobbyManager.ConfigAndData;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class PlayerDataSQLManager {

    private final CwRLobbyAPI plugin;
    private Connection connection;

    public PlayerDataSQLManager(CwRLobbyAPI plugin) {
        this.plugin = plugin;
        if (plugin.getConfig().getBoolean("use-sql", false)) {
            connect();
            createTableIfNotExists();
        }
    }

    private void connect() {
        String host = plugin.getConfig().getString("sql-host", "localhost");
        int port = plugin.getConfig().getInt("sql-port", 3306);
        String database = plugin.getConfig().getString("sql-database", "lobby_player_db");
        String username = plugin.getConfig().getString("sql-username", "user");
        String password = plugin.getConfig().getString("sql-password", "pass");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("Connected to SQL database successfully.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to SQL database: " + e.getMessage());
            connection = null;
        }
    }

    private void createTableIfNotExists() {
        if (connection == null) return;
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "selected_group VARCHAR(255)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create player_data table: " + e.getMessage());
        }
    }

    public String getSelectedGroup(UUID uuid) {
        if (connection == null) return "default";
        String sql = "SELECT selected_group FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String group = rs.getString("selected_group");
                if (group == null || group.isEmpty()) return "default";
                return group;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get selected group from SQL: " + e.getMessage());
        }
        return "default";
    }

    public void setSelectedGroup(UUID uuid, String group) {
        if (connection == null) return;
        if (group.equalsIgnoreCase("default")) {
            // Delete row if exists
            String deleteSql = "DELETE FROM player_data WHERE uuid = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete player data for default group: " + e.getMessage());
            }
        } else {
            // Insert or update
            String sql = "INSERT INTO player_data (uuid, selected_group) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE selected_group = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, group);
                ps.setString(3, group);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to set selected group in SQL: " + e.getMessage());
            }
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to close SQL connection: " + e.getMessage());
            }
        }
    }

}
