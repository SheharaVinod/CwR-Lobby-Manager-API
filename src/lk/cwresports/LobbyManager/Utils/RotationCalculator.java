package lk.cwresports.LobbyManager.Utils;

import java.util.Calendar;
import lk.cwresports.LobbyManager.API.TimeUnits;
import lk.cwresports.LobbyManager.CwRLobbyAPI;

public class RotationCalculator {
    private static double timeOffsetHours = 5.5;

    public static void init(CwRLobbyAPI plugin) {
        timeOffsetHours = plugin.getConfig().getDouble("time-offset", 5.5);
    }

    public static long calculateNextRotation(TimeUnits unit) {
        Calendar calendar = Calendar.getInstance();

        // Apply time offset
        calendar.add(Calendar.MILLISECOND, (int)(timeOffsetHours * 3600000));

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (unit) {
            case MINUTE:
                calendar.add(Calendar.MINUTE, 1);
                break;

            case HOUR:
                calendar.add(Calendar.HOUR, 1);
                calendar.set(Calendar.MINUTE, 0);
                break;

            case DAY:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                break;

            case WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                break;

            case MONTH:
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                break;

            default:
                return -1;
        }

        // Remove time offset
        calendar.add(Calendar.MILLISECOND, (int)(-timeOffsetHours * 3600000));

        return calendar.getTimeInMillis();
    }
}