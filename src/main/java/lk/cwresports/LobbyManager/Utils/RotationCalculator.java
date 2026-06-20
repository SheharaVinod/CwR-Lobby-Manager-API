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