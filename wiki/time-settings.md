# Time Settings

> Control the Minecraft day/night cycle — sync it with real-world time or set a custom length. Per-world configuration.

## Why?

By default, a Minecraft day lasts 20 minutes. This is great for survival gameplay, but for a hub or lobby server, you may want something different:

- **Real-world sync** — Make the in-game time match the real clock in your timezone. Sunrise happens at 6 AM real-time, noon at 12 PM, and sunset at 6 PM. This gives players a natural, immersive experience that matches their actual day.
- **Custom day length** — Want a day that lasts 1 hour? 3 hours? 100 hours? Set any duration you like. Perfect for roleplay servers, event lobbies, or building a specific atmosphere.

Both features work per-world, so you can have different time settings for different lobbies.

## Requirements

Before using any time commands, you must enable the day/night cycle for the lobby:

```
/lobby-manager flags do_day_night_circle true
```

Without this, Minecraft's daylight cycle is paused and the time commands won't take effect.

## Commands

### Setting a Time Zone

```
/lobby-manager time set_time_zone <offset|zone>
```

Sets the timezone used for real-world sync. Accepts two formats:

**UTC offsets** (use underscores instead of colons):
```
/lobby-manager time set_time_zone 5_30      # UTC+5:30 (Sri Lanka, India)
/lobby-manager time set_time_zone 5_00      # UTC+5:00
/lobby-manager time set_time_zone -5_00     # UTC-5:00 (Eastern US)
/lobby-manager time set_time_zone 0_00      # UTC+0:00
```

**IANA timezone IDs** (standard names):
```
/lobby-manager time set_time_zone Asia/Colombo
/lobby-manager time set_time_zone America/New_York
/lobby-manager time set_time_zone Europe/London
```

### Enabling Real-World Sync

```
/lobby-manager time real_world_sync true
```

Once enabled, the in-game time will match the real clock in the configured timezone:

| Real Time | In-Game Time |
|-----------|--------------|
| 6:00 AM   | Dawn (tick 0) |
| 12:00 PM  | Noon (tick 6000) |
| 6:00 PM   | Sunset (tick 12000) |
| 12:00 AM  | Midnight (tick 18000) |

**Note:** You must set a timezone first (`set_time_zone`) before enabling sync.

To disable:
```
/lobby-manager time real_world_sync false
```

### Setting a Custom Day Length

```
/lobby-manager time set_length <duration>
```

Set how long a full day/night cycle takes. The duration format uses space-separated tokens:

| Command | Cycle Length |
|---------|-------------|
| `/lobby-manager time set_length 20m` | 20 minutes (default) |
| `/lobby-manager time set_length 1h` | 1 hour |
| `/lobby-manager time set_length 3h` | 3 hours |
| `/lobby-manager time set_length 2h 30m 30s` | 2 hours, 30 minutes, 30 seconds |
| `/lobby-manager time set_length 100h` | 100 hours |

**Minimum:** 1 minute. **No maximum** (but very long cycles mean the sun barely moves).

Setting a custom length automatically disables real-world sync.

### Resetting to Default

```
/lobby-manager time reset_to_default
```

Resets all time settings on the lobby back to Minecraft's default 20-minute cycle and re-enables the natural daylight cycle game rule.

## Checking Current Settings

Use `/lobby-manager info` to see the current time settings for the lobby you're in:

```
Custom Time Active: true
  Sync Mode: Real World (Asia/Colombo)
```

or

```
Custom Time Active: true
  Cycle Length: 2h 30m 30s
```

or when using default settings:

```
Time Settings: Default (20 min cycle)
```

## How It Works

When a custom time setting is active, the plugin takes control of the world's daylight cycle:

1. The `doDaylightCycle` game rule is set to `false`
2. A background task runs every second, calculating what the Minecraft time should be
3. It sets the world time directly using `world.setTime()`

This means the time progression is smooth and drift-free — no matter how long your custom cycle is, the sun and moon will be exactly where they should be.

## Persistence

Time settings are saved to `lobby_data.yml` and survive server restarts:

```yaml
lobbies:
  world_name:
    real_world_sync: true
    time_zone: "Asia/Colombo"
    custom_cycle_length_ms: 3600000
    # ... other lobby settings
```

## Example Setup

**A hub that matches real-world Sri Lankan time:**

```
/lobby-manager admin
/lobby-manager flags do_day_night_circle true
/lobby-manager time set_time_zone 5_30
/lobby-manager time real_world_sync true
```

**An event lobby with a slow, 3-hour day cycle:**

```
/lobby-manager admin
/lobby-manager create_event_lobby
/lobby-manager flags do_day_night_circle true
/lobby-manager time set_length 3h
```

**Resetting a lobby to normal Minecraft time:**

```
/lobby-manager time reset_to_default
```
