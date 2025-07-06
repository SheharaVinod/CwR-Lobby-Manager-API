package lk.cwresports.LobbyManager.API;

import lk.cwresports.LobbyManager.Utils.TimeZoneHelper;
import org.bukkit.Location;

import java.util.Calendar;

public class EventLobbies extends Lobby {
    private String eventDate;
    private int expireDays;
    private String timeZone;

    public EventLobbies(Location currentLocation) {
        super(currentLocation);

        LobbyManager.getInstance().getEventLobbies().add(this);
    }

    @Override
    public boolean isEventLobby() {
        return true;
    }

    public void setPeriod(String eventDate, int expireDays, String timeZone) {
        this.eventDate = eventDate;
        this.expireDays = expireDays;
        this.timeZone = timeZone;
    }

    public boolean isEvent() {
        if (eventDate == null || timeZone == null) return true;

        try {
            String[] parts = eventDate.split("-");
            int month = Integer.parseInt(parts[0]) - 1; // Calendar uses 0-based months
            int day = Integer.parseInt(parts[1]);
            int hour = Integer.parseInt(parts[2]);
            int minute = Integer.parseInt(parts[3]);
            int second = Integer.parseInt(parts[4]);

            double offset = TimeZoneHelper.getOffsetForAbbr(timeZone);
            long offsetMillis = (long) (offset * 3600000);

            Calendar eventStart = Calendar.getInstance();
            eventStart.set(Calendar.MONTH, month);
            eventStart.set(Calendar.DAY_OF_MONTH, day);
            eventStart.set(Calendar.HOUR_OF_DAY, hour);
            eventStart.set(Calendar.MINUTE, minute);
            eventStart.set(Calendar.SECOND, second);
            eventStart.set(Calendar.MILLISECOND, 0);

            long eventStartMillis = eventStart.getTimeInMillis() - offsetMillis;
            long eventEndMillis = eventStartMillis + (expireDays * 86400000L);
            long currentTime = System.currentTimeMillis();

            return currentTime >= eventStartMillis && currentTime < eventEndMillis;
        } catch (Exception e) {
            return true;
        }
    }
}
