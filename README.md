# CwR Lobby Manager API

![GitHub release](https://img.shields.io/github/v/release/SheharaVinod/CwR-Lobby-Manager-API?style=for-the-badge) ![SpigotMC](https://img.shields.io/badge/Spigot-1.8.8%2B-orange?style=for-the-badge) ![License](https://img.shields.io/github/license/SheharaVinod/CwR-Lobby-Manager-API?style=for-the-badge)

> **CwR Lobby Manager API** is a powerful and lightweight **Spigot/Bukkit** plugin developed by **Team CwR** that revolutionizes lobby management for Minecraft servers. Create unlimited spawn lobbies, manage multiple spawn points, and control player routing with sophisticated rotation systems – all with zero complex configuration.

**Website:** [cwresports.lk](https://cwresports.lk)  
**Repository:** [GitHub - CwR Lobby Manager API](https://github.com/SheharaVinod/CwR-Lobby-Manager-API)

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| **🏢 Multi-Lobby Support** | Create unlimited lobbies and organize them into groups for systematic player management |
| **📍 Multiple Spawn Points** | Define multiple spawn locations per lobby with intelligent rotation (circular, random, or default) |
| **🎉 Timed Event Lobbies** | Schedule special event lobbies that activate/deactivate automatically (perfect for holidays, events) |
| **🔄 Auto-Rotation System** | Automatically rotate active lobbies on minute/hour/day/week/month basis or keep manual control |
| **💾 Flexible Storage** | Choose between MySQL (recommended for networks) or YAML files for data persistence |
| **🔐 Permission-Based Access** | Fine-grained permission system for admins, default players, and premium user groups |
| **⏰ Smart Spawn Cooldown** | Configurable AFK timer with movement cancellation to prevent teleport abuse |
| **🛡️ Void Protection** | Automatic safety teleport when players fall below configurable Y-level |
| **⚙️ Per-Lobby Configuration** | Individual control over hunger, damage, interactions, and gamemode for each lobby |
| **🔧 Developer API** | Simple one-liner API for developers: `LobbyManager.getInstance().sendToLobby(player)` |
| **🚫 Spawn Command Control** | Block/unblock `/spawn` command programmatically for minigames and custom mechanics |

---

## 📋 Requirements

- **Minecraft Version:** 1.8.8 or higher (tested up to 1.20.4)
- **Server Software:** Spigot, Paper, or any Bukkit-based server
- **Java Version:** Java 17 or higher
- **Optional:** MySQL database for network setups

---

## 🚀 Installation

1. **Download** the latest release from the [releases page](https://github.com/SheharaVinod/CwR-Lobby-Manager-API/releases)
2. **Place** the JAR file in your server's `plugins/` directory
3. **Restart** your server
4. **Configure** the plugin using the generated `config.yml`
5. **Start** creating lobbies with the admin commands!

---

## ⚙️ Configuration

The plugin generates a `config.yml` file with the following key options:

```yaml
# Command visibility
hide-lobby-manager-command: false

# Spawn command settings
spawn-command-cool-down-in-sec: 1
should-afk-for-teleport: true
should-afk-admins-for-teleport: false

# Auto-rotation settings
auto-rotate-lobbies-every: MANUAL  # MINUTE, HOUR, DAY, WEEK, MONTH, MANUAL
time-offset: 5.5  # Server timezone offset

# Database configuration
use-sql: false
sql-host: localhost
sql-port: 3306
sql-database: lobby_player_db
sql-username: user
sql-password: pass

# Safety features
auto-teleport-back-to-spawn-when-y-level: -300  # Void protection
change-game-mod-of-admins-after-teleport-to-spawn: false
```

---

## 🎮 Commands & Usage

### Player Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/spawn` | `cwr-core.lobby-manager.main` | Teleport to your assigned lobby (respects cooldown & AFK rules) |
| `/select-spawn <group>` | `cwr-core.lobby-manager.gui.spawn-selector` | Select a specific lobby group (default, premium, etc.) |

### Admin Commands

All admin commands require the permission `cwr-core.lobby-manager.admin` and admin mode to be enabled.

**🔧 Admin Mode**
```
/lobby-manager admin
```
Toggle admin mode (required for most configuration commands).

**👥 Group Management**
```
/lobby-manager create_group <name>        # Create a new lobby group
/lobby-manager delete_group <name>        # Delete an existing group
/lobby-manager change_group_of <lobby> <group>  # Move lobby to different group
```

**🏗️ Lobby Creation**
```
/lobby-manager create_lobby              # Create regular lobby at current location
/lobby-manager create_event_lobby        # Create event lobby at current location
/lobby-manager delete_lobby              # Delete current lobby
```

**📍 Spawn Point Management**
```
/lobby-manager add_a_new_spawn           # Add spawn point at current location
/lobby-manager set_default_spawn         # Set current location as default spawn
/lobby-manager remove_spawn_location_by_index <index>  # Remove spawn point (supports negative indexing)
/lobby-manager change_lobby_spawn_rotation <type>      # Set rotation: DEFAULT, RANDOM, CIRCULAR
```

**🎉 Event Lobby Management**
```
/lobby-manager set_period <start_date> <duration>     # Set event schedule
# Example: /lobby-manager set_period 12-25-00-00-00 1-0-0-0  (Christmas, 1 day)
```

**🔄 Rotation Management**
```
/lobby-manager change_lobby_rotation <type>           # Set group rotation: RANDOM, CIRCULAR
/lobby-manager set_group_lobby_rotation_time <group> <unit>  # Set auto-rotation schedule
/lobby-manager rotate_every_lobby_group              # Force rotate all groups immediately
```

**⚙️ Lobby Settings**
```
/lobby-manager disabled_hunger <true|false>          # Toggle hunger loss
/lobby-manager disabled_damage <true|false>          # Toggle damage
/lobby-manager set_game_mod <GAMEMODE>               # Set lobby gamemode
/lobby-manager cansel_player_interaction <true|false>  # Block interactions
```

**ℹ️ Information Commands**
```
/lobby-manager info                      # Show current lobby information
/lobby-manager info_of_all_groups        # List all groups and lobbies
/lobby-manager info_of_all_event_lobbies # List all event lobbies with schedules
/lobby-manager help                      # Show command help
```

**💾 System Commands**
```
/lobby-manager save                      # Manually save all data
```

---

## 🔐 Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `cwr-core.lobby-manager.admin` | op | Access to all admin commands |
| `cwr-core.lobby-manager.main` | op | Use `/spawn` command |
| `cwr-core.lobby-manager.gui.spawn-selector` | op | Use `/select-spawn` command |
| `cwr-core.lobby-manager.spawn.default` | true | Access to default lobby group |
| `cwr-core.lobby-manager.lobbies.default` | true | Default lobby access |
| `cwr-core.lobby-manager.lobbies.others` | op | Access to special lobbies |
| `cwr-core.lobby-manager.spawn.<groupname>` | custom | Access to specific group |

---

## 🔧 Developer API

### Basic Usage

CwR Lobby Manager API provides a simple yet powerful API for developers. Add the plugin to your `plugin.yml` dependencies:

```yaml
depend: [CwRLobbyManagerAPI]
# or
softdepend: [CwRLobbyManagerAPI]
```

### Core API Methods

**Teleporting Players**
```java
import lk.cwresports.LobbyManager.API.LobbyManager;

// Teleport player to their assigned lobby
LobbyManager.getInstance().sendToLobby(player);
```

**Spawn Command Control**
```java
// Block spawn command for a player (useful for minigames)
LobbyManager.blockSpawnCommand(player);

// Unblock spawn command
LobbyManager.unBlockSpawnCommand(player);

// Check if spawn command is blocked
boolean isBlocked = LobbyManager.isBlockedSpawnCommand(player);
```

**Lobby Information**
```java
// Check if player is in a lobby
boolean inLobby = LobbyManager.getInstance().isInALobby(player);

// Get lobby by name
Lobby lobby = LobbyManager.getInstance().getLobbyByName(worldName);

// Get all lobby groups
List<String> groups = LobbyManager.getInstance().getGroups();
```

### Important Notes for Developers

1. **Command Blocking Reset**: Spawn command blocking is automatically reset when players leave and rejoin the server. Your plugin must handle re-blocking if needed.

2. **Automatic Handling**: The API automatically handles:
   - Rotation logic
   - Permission checks
   - Event lobby overrides
   - Cooldown management
   - AFK requirements

3. **Thread Safety**: All API methods are thread-safe and can be called from any thread.

### Example: Minigame Integration

```java
public class MinigamePlugin extends JavaPlugin {
    
    @EventHandler
    public void onGameStart(GameStartEvent event) {
        for (Player player : event.getPlayers()) {
            // Block spawn command during game
            LobbyManager.blockSpawnCommand(player);
        }
    }
    
    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        for (Player player : event.getPlayers()) {
            // Unblock spawn command
            LobbyManager.unBlockSpawnCommand(player);
            
            // Send player back to lobby
            LobbyManager.getInstance().sendToLobby(player);
        }
    }
}
```

---

## 📊 Database Schema

When using SQL storage (`use-sql: true`), the plugin creates a simple table:

```sql
CREATE TABLE player_data (
    uuid VARCHAR(36) PRIMARY KEY,
    selected_group VARCHAR(255)
);
```

Only player group selections are stored in SQL; all lobby configurations remain in YAML files for easier management.

---

## 🏗️ Building from Source

```bash
git clone https://github.com/SheharaVinod/CwR-Lobby-Manager-API.git
cd CwR-Lobby-Manager-API
```

**Requirements:**
- Java 17 or higher

---

## 📖 Usage Examples

### Setting up a Basic Hub

1. **Create a default lobby:**
   ```
   /lobby-manager admin
   /lobby-manager create_lobby
   /lobby-manager add_a_new_spawn
   ```

2. **Add multiple spawn points:**
   ```
   /lobby-manager add_a_new_spawn  (repeat at different locations)
   /lobby-manager change_lobby_spawn_rotation RANDOM
   ```

3. **Create a premium group:**
   ```
   /lobby-manager create_group premium
   /lobby-manager change_group_of world_name premium
   ```

### Creating a Holiday Event Lobby

1. **Create event lobby:**
   ```
   /lobby-manager create_event_lobby
   /lobby-manager set_period 12-25-00-00-00 7-0-0-0  # Christmas week
   ```

2. **Configure event lobby:**
   ```
   /lobby-manager disabled_damage true
   /lobby-manager set_game_mod ADVENTURE
   ```

### Network Setup with Auto-Rotation

1. **Configure multiple lobbies:**
   ```
   /lobby-manager create_group morning
   /lobby-manager create_group evening
   ```

2. **Set up auto-rotation:**
   ```
   /lobby-manager set_group_lobby_rotation_time morning HOUR
   /lobby-manager change_lobby_rotation CIRCULAR
   ```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java 17 compatibility
- Maintain backward compatibility with existing configurations
- Add appropriate JavaDoc comments
- Test with multiple Spigot versions
- Update documentation for new features

---

## 📝 License

CwR Lobby Manager API is released under the GNU General Public License v3.0.  
You are free to fork, modify, and redistribute the plugin **as long as** the
source code of any distributed version remains available under the same
license. See the LICENSE file for full terms.

---

## 🆘 Support

- **Issues:** [GitHub Issues](https://github.com/SheharaVinod/CwR-Lobby-Manager-API/issues)
- **Website:** [cwresports.lk](https://cwresports.lk)
- **Developer:** Mr_Unknown, from Team CwR

---

## 🙏 Acknowledgments

- Built for the Minecraft server community
- Tested on various Spigot/Paper versions
- Inspired by the need for flexible lobby management
- Special thanks to all contributors and testers

---

*Made with ❤️ by Team CwR*