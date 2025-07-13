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

public class GroupLobbies extends Lobby {
    public GroupLobbies(Location currentLocation) {
        super(currentLocation); // This adds the first spawn location
        // Don't add it again here - the parent constructor already did it
        this.setDefaultSpawnLocation(currentLocation);

        if (!LobbyManager.getInstance().isLoading) {
            LobbyManager.getInstance().getDefaultGroup().addLobby(this);
        }
    }
}
