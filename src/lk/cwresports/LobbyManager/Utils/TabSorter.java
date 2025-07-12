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
