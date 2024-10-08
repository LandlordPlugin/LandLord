package biz.princeps.landlord.util;

import biz.princeps.landlord.api.ILandLord;
import com.google.gson.Gson;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public enum Skulls {

    FORWARD1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19"),
    FORWARD5("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI0YzFkYmQ4NTQ0Y2ZhZWQ5NjNkZjRkMTM5YmM4YmFjNGI3NmFjNGJiYjMwYWI4NmY3NmZiYzMxYWI5YTcwIn19fQ=="),
    FORWARD10("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19"),
    BACK1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRmNWMyZjg5M2JkM2Y4OWNhNDA3MDNkZWQzZTQyZGQwZmJkYmE2ZjY3NjhjODc4OWFmZGZmMWZhNzhiZjYifX19"),
    BACK5("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ5ZDI3MWM1ZGY4NGY4YTNjOGFhNWQxNTQyN2Y2MjgzOTM0MWRhYjUyYzYxOWE1OTg3ZDM4ZmJlMThlNDY0In19fQ=="),
    BACK10("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0="),
    ABORT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="),
    CONFIRM("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0="),
    BLACK0("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ2ODM0M2JkMGIxMjlkZTkzY2M4ZDNiYmEzYjk3YTJmYWE3YWRlMzhkOGE2ZTJiODY0Y2Q4NjhjZmFiIn19fQ=="),
    BLACK1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ=="),
    BLACK2("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZmYWI5OTFkMDgzOTkzY2I4M2U0YmNmNDRhMGI2Y2VmYWM2NDdkNDE4OWVlOWNiODIzZTljYzE1NzFlMzgifX19"),
    BLACK3("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2QzMTliOTM0M2YxN2EzNTYzNmJjYmMyNmI4MTk2MjVhOTMzM2RlMzczNjExMWYyZTkzMjgyN2M4ZTc0OSJ9fX0="),
    BLACK4("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE5OGQ1NjIxNjE1NjExNDI2NTk3M2MyNThmNTdmYzc5ZDI0NmJiNjVlM2M3N2JiZTgzMTJlZTM1ZGI2In19fQ=="),
    BLACK5("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ZiOTFiYjk3NzQ5ZDZhNmVlZDQ0NDlkMjNhZWEyODRkYzRkZTZjMzgxOGVlYTVjN2UxNDlkZGRhNmY3YzkifX19"),
    BLACK6("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM2MTNmODBhNTU0OTE4YzdhYjJjZDRhMjc4NzUyZjE1MTQxMmE0NGE3M2Q3YTI4NmQ2MWQ0NWJlNGVhYWUxIn19fQ=="),
    BLACK7("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUxOThmZDgzMWNiNjFmMzkyN2YyMWNmOGE3NDYzYWY1ZWEzYzdlNDNiZDNlOGVjN2QyOTQ4NjMxY2NlODc5In19fQ=="),
    BLACK8("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhZDEyYzJmMjFhMTk3MmYzZDJmMzgxZWQwNWE2Y2MwODg0ODlmY2ZkZjY4YTcxM2IzODc0ODJmZTkxZTIifX19"),
    BLACK9("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3YWEwZDk3OTgzY2Q2N2RmYjY3YjdkOWQ5YzY0MWJjOWFhMzRkOTY2MzJmMzcyZDI2ZmVlMTlmNzFmOGI3In19fQ=="),
    BLACKMINUS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM1ZTRlMjZlYWZjMTFiNTJjMTE2NjhlMWQ2NjM0ZTdkMWQwZDIxYzQxMWNiMDg1ZjkzOTQyNjhlYjRjZGZiYSJ9fX0="),
    CASH("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRkNjI1Y2E3NmRjODliNjc4YWFjYWRkZTJiNjI2ZTQ3NmRjYjE0YmU2NGM4ZDk1ZGM1MWU4OTA3NWZhY2MzOCJ9fX0="),
    REDEXCLAMATIONMARK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRlMWRhODgyZTQzNDgyOWI5NmVjOGVmMjQyYTM4NGE1M2Q4OTAxOGZhNjVmZWU1YjM3ZGViMDRlY2NiZjEwZSJ9fX0="),
    ;

    final String texture;
    final Profile profile;

    Skulls(String texture) {
        this.texture = texture;
        profile = new Gson().fromJson(new String(Base64.getDecoder().decode(texture)), Profile.class);
    }

    public static List<Skulls> numToSkull(int num) {
        List<Skulls> list = new ArrayList<>();
        String numstring = String.valueOf(num);
        for (char c : numstring.toCharArray()) {
            switch (c) {
                case '1':
                    list.add(BLACK1);
                    break;
                case '2':
                    list.add(BLACK2);
                    break;
                case '3':
                    list.add(BLACK3);
                    break;
                case '4':
                    list.add(BLACK4);
                    break;
                case '5':
                    list.add(BLACK5);
                    break;
                case '6':
                    list.add(BLACK6);
                    break;
                case '7':
                    list.add(BLACK7);
                    break;
                case '8':
                    list.add(BLACK8);
                    break;
                case '9':
                    list.add(BLACK9);
                    break;
                case '-':
                    list.add(BLACKMINUS);
                    break;
                default:
                    list.add(BLACK0);
            }
        }

        return list;
    }

    public ItemStack getSkull(ILandLord plugin) {
        UUID uuid = plugin.getServer().getOnlinePlayers().stream().findAny().get().getUniqueId();
        return plugin.getMaterialsManager().getPlayerHead(uuid, profile.textures.SKIN.url);
    }

    @Override
    public String toString() {
        return "Skulls{" +
                this.name() +
                '}';
    }

    public record Profile(Textures textures) {
    }

    public record Textures(Skin SKIN) {
    }

    public record Skin(String url) {
    }
}
