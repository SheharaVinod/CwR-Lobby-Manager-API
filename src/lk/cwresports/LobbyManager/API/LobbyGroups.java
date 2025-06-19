package lk.cwresports.LobbyManager.API;

public enum LobbyGroups {
    DEFAULT("default"),
    SPECIAL("special");
    
    private final String name;
    
    LobbyGroups(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static LobbyGroups fromString(String name) {
        for (LobbyGroups group : values()) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return DEFAULT; // fallback to default
    }
}