package lk.cwresports.LobbyManager.Utils;

import lk.cwresports.LobbyManager.CwRLobbyAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeZoneHelper {
    private static final Map<String, Double> offsetMap = new HashMap<>();

    public static void load(CwRLobbyAPI plugin) {
        try (InputStream is = plugin.getResource("timezones.json")) {
            if (is == null) return;

            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray timezones = new JSONArray(json);

            for (int i = 0; i < timezones.length(); i++) {
                JSONObject tz = timezones.getJSONObject(i);
                offsetMap.put(tz.getString("abbr").toUpperCase(), tz.getDouble("offset"));
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load timezones.json: " + e.getMessage());
        }
    }

    public static double getOffsetForAbbr(String abbr) {
        return offsetMap.getOrDefault(abbr.toUpperCase(), 0.0);
    }

    public static List<String> getZones() {
        return List.copyOf(offsetMap.keySet());
    }
}