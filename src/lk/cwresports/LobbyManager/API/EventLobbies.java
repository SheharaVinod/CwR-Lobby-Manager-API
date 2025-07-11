package lk.cwresports.LobbyManager.API;

import org.bukkit.Location;
import java.util.Calendar;

public class EventLobbies extends Lobby {
    private String eventDate;
    private int expireDays;
    private long eventStartMillis = -1;
    private long eventEndMillis = -1;

    public EventLobbies(Location currentLocation) {
        super(currentLocation);
        LobbyManager.getInstance().getEventLobbies().add(this);
    }

    @Override
    public boolean isEventLobby() {
        return true;
    }

    public void setPeriod(String eventDate, int expireDays) {
        this.eventDate = eventDate;
        this.expireDays = expireDays;
        try {
            String[] parts = eventDate.split("-");
            int month = Integer.parseInt(parts[0]) - 1;
            int day = Integer.parseInt(parts[1]);
            int hour = Integer.parseInt(parts[2]);
            int minute = Integer.parseInt(parts[3]);
            int second = Integer.parseInt(parts[4]);
            double timeOffsetHours = 5.5; // or load from config
            long offsetMillis = (long) (timeOffsetHours * 3600000);
            Calendar eventStart = Calendar.getInstance();
            eventStart.set(Calendar.MONTH, month);
            eventStart.set(Calendar.DAY_OF_MONTH, day);
            eventStart.set(Calendar.HOUR_OF_DAY, hour);
            eventStart.set(Calendar.MINUTE, minute);
            eventStart.set(Calendar.SECOND, second);
            eventStart.set(Calendar.MILLISECOND, 0);
            this.eventStartMillis = eventStart.getTimeInMillis() - offsetMillis;
            this.eventEndMillis = eventStartMillis + (expireDays * 86400000L);
        } catch (Exception e) {
            this.eventStartMillis = -1;
            this.eventEndMillis = -1;
            throw new IllegalArgumentException("Invalid date format or values");
        }
    }

    public boolean isEvent() {
        if (eventStartMillis == -1 || eventEndMillis == -1) return false;
        long currentTime = System.currentTimeMillis();
        return currentTime >= eventStartMillis && currentTime < eventEndMillis;
    }

    public String getEventDate() { return eventDate; }
    public int getExpireDays() { return expireDays; }
}
