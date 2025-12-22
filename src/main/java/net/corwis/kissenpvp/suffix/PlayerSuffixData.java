package net.corwis.kissenpvp.suffix;

import java.util.HashMap;
import java.util.Map;

public class PlayerSuffixData {

    private final Map<String, String> suffixes = new HashMap<>();
    private String selectedSuffix;
    private boolean chatEnabled = true;

    public Map<String, String> getSuffixes() {
        return suffixes;
    }

    public boolean hasSuffix(String name) {
        return suffixes.containsKey(name.toLowerCase());
    }

    public void addSuffix(String name, String value) {
        suffixes.put(name.toLowerCase(), value);
    }

    public void removeSuffix(String name) {
        suffixes.remove(name.toLowerCase());
        if (name.equalsIgnoreCase(selectedSuffix)) {
            selectedSuffix = null;
        }
    }

    public String getSelectedSuffix() {
        return selectedSuffix;
    }

    public void setSelectedSuffix(String name) {
        this.selectedSuffix = name.toLowerCase();
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void toggleChat() {
        chatEnabled = !chatEnabled;
    }
}