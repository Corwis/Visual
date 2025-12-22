package net.corwis.kissenpvp.suffix;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuffixManager {

    private final Map<UUID, net.corwis.kissenpvp.suffix.PlayerSuffixData> data = new HashMap<>();

    public PlayerSuffixData get(UUID uuid) {
        return data.computeIfAbsent(uuid, k -> new PlayerSuffixData());
    }

    public void remove(UUID uuid) {
        data.remove(uuid);
    }
}
