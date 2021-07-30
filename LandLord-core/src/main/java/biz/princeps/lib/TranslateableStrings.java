package biz.princeps.lib;

import java.util.HashMap;
import java.util.Map;

public class TranslateableStrings {

    private final Map<String, String> strings;

    public TranslateableStrings() {
        strings = new HashMap<>();
        strings.put("Confirmation.accept", "§bAccept!");
        strings.put("Confirmation.decline", "§eDecline!");
        strings.put("noPermissionsCmd", "§cYou don't have the permission to execute /%cmd%!");
    }

    public void setString(String path, String string) {
        strings.replace(path, string);
    }

    public String get(String path) {
        return strings.get(path);
    }
}
