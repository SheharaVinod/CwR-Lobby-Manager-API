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

import org.bukkit.Location;
import java.util.Calendar;

public class EventLobbies extends Lobby {
    private String eventDate;
    private String expirePeriod;
    private long eventStartMillis = -1;
    private long eventEndMillis = -1;

    public EventLobbies(Location currentLocation) {
        super(currentLocation); // This adds the first spawn location
        // Don't add it again here
        LobbyManager.getInstance().getEventLobbies().add(this);
    }

    @Override
    public boolean isEventLobby() {
        return true;
    }

    private String convertOldExpireFormat(String expirePeriod) {
        // If it's a simple number (old format), convert to days-only format
        if (expirePeriod.matches("\\d+")) {
            return expirePeriod + "-0-0-0"; // Convert to DD-HH-mm-ss format
        }
        return expirePeriod;
    }

    public void setPeriod(String eventDate, String expirePeriod) {
        this.eventDate = eventDate;
        this.expirePeriod = expirePeriod;

        try {
            // Parse event date (supports both MM-DD-HH-mm-ss and DD-HH-mm-ss)
            String[] dateParts = eventDate.split("-");
            Calendar eventStart = Calendar.getInstance();

            if (dateParts.length == 5) {
                // MM-DD-HH-mm-ss format
                int month = Integer.parseInt(dateParts[0]) - 1; // Month is 0-based
                if (month < 0 || month > 11) {
                    throw new IllegalArgumentException("Month must be between 01-12");
                }
                eventStart.set(Calendar.MONTH, month);
                eventStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[1]));
                eventStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateParts[2]));
                eventStart.set(Calendar.MINUTE, Integer.parseInt(dateParts[3]));
                eventStart.set(Calendar.SECOND, Integer.parseInt(dateParts[4]));
            } else if (dateParts.length == 4) {
                // DD-HH-mm-ss format (uses current month)
                eventStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
                eventStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateParts[1]));
                eventStart.set(Calendar.MINUTE, Integer.parseInt(dateParts[2]));
                eventStart.set(Calendar.SECOND, Integer.parseInt(dateParts[3]));
            } else {
                throw new IllegalArgumentException("Date format must be MM-DD-HH-mm-ss or DD-HH-mm-ss");
            }

            // Validate time components
            if (eventStart.get(Calendar.HOUR_OF_DAY) > 23 ||
                    eventStart.get(Calendar.MINUTE) > 59 ||
                    eventStart.get(Calendar.SECOND) > 59) {
                throw new IllegalArgumentException("Invalid time (HH:mm:ss must be 00-23:00-59:00-59)");
            }

            eventStart.set(Calendar.MILLISECOND, 0);

            // Apply time offset (from config)
            double timeOffsetHours = 5.5;
            long offsetMillis = (long) (timeOffsetHours * 3600000);
            this.eventStartMillis = eventStart.getTimeInMillis() - offsetMillis;

            // Parse expiration period (supports both simple days and DD-HH-mm-ss format)
            String[] expireParts = expirePeriod.split("-");
            long expireMillis = 0;

            if (expireParts.length == 1) {
                // Simple days format
                expireMillis = Long.parseLong(expireParts[0]) * 86400000L;
            } else {
                // DD-HH-mm-ss format
                if (expireParts.length < 3) {
                    throw new IllegalArgumentException("Expire period must be days or DD-HH-mm-ss format");
                }

                long days = Long.parseLong(expireParts[0]);
                long hours = Long.parseLong(expireParts[1]);
                long minutes = Long.parseLong(expireParts[2]);
                long seconds = expireParts.length > 3 ? Long.parseLong(expireParts[3]) : 0;

                // Validate time components
                if (hours > 23 || minutes > 59 || seconds > 59) {
                    throw new IllegalArgumentException("Invalid duration time (HH:mm:ss must be 00-23:00-59:00-59)");
                }

                expireMillis = (days * 86400000L) +
                        (hours * 3600000L) +
                        (minutes * 60000L) +
                        (seconds * 1000L);
            }

            this.eventEndMillis = eventStartMillis + expireMillis;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("All date components must be numbers");
        } catch (Exception e) {
            this.eventStartMillis = -1;
            this.eventEndMillis = -1;
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public boolean isEvent() {
        if (eventStartMillis == -1 || eventEndMillis == -1) return false;
        long currentTime = System.currentTimeMillis();
        return currentTime >= eventStartMillis && currentTime < eventEndMillis;
    }

    public String getEventDate() { return eventDate; }
    public String getExpireDays() { return expirePeriod; }
}
