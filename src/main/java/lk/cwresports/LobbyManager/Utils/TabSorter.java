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

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabSorter {

    public static List<String> sort(List<String> unsorted_list) {
        // this command sort list with alphabetically and return all.
        final List<String> completions = new ArrayList<>(unsorted_list);
        Collections.sort(completions);
        return completions;
    }

    public static List<String> sort(List<String> unsorted_list, String match_with) {
        return sort(unsorted_list, match_with, false);
    }

    public static List<String> sort(List<String> unsorted_list, String match_with, boolean return_all) {
        // this function return copy of sorted list match with givenLetter.
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(match_with, unsorted_list, completions);
        return completions;
    }
}
