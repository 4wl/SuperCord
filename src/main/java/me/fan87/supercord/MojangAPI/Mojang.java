package me.fan87.supercord.MojangAPI;

import me.fan87.Table;
import me.fan87.supercord.SuperCord;
import me.kbrewster.mojangapi.MojangAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Mojang {

    public static UUID getUUID(String playerName) {
        try {
            List<Table.Row> query = SuperCord.mojangAPITable.getQuery("LOWER(`name`) = LOWER('" + playerName + "')");
            if (query.size() == 1) {
                return UUID.fromString((String) query.get(0).getValues().get("uuid"));
            }
            if (query.size() == 0) {
                Map map = new HashMap();
                UUID uuid = MojangAPI.getUUID(playerName);
                map.put("uuid", uuid.toString());
                map.put("name", MojangAPI.getName(uuid));
                SuperCord.mojangAPITable.setQuery(map);
                return uuid;
            }
            SuperCord.mojangAPITable.deleteQuery("LOWER(`name`)=LOWER('" + playerName + "')");
            return getUUID(playerName);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static String getPlayerName(UUID uuid) {
        try {
            List<Table.Row> query = SuperCord.mojangAPITable.getQuery("LOWER(`uuid`) = LOWER('" + uuid + "')");
            if (query.size() == 1) {
                return (String) query.get(0).getValues().get("name");
            }
            if (query.size() == 0) {
                Map map = new HashMap();
                String name = MojangAPI.getName(uuid);
                map.put("uuid", uuid.toString());
                map.put("name", name);
                SuperCord.mojangAPITable.setQuery(map);
                return name;
            }
            SuperCord.mojangAPITable.deleteQuery("LOWER(`uuid`)=LOWER('" + uuid.toString() + "')");
            return getPlayerName(uuid);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
