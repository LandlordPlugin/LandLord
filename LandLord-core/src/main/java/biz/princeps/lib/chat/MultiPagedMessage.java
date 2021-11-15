package biz.princeps.lib.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 7/18/17
 */
public class MultiPagedMessage {

    private final String command;
    private final int perSite;
    private final List<String> elements;
    private final String previous;
    private final String next;
    private String header;
    private int pointer;

    /**
     * General Design idea:
     * this is the header, which can be custom crafted
     * obj1
     * obj2
     * obj3
     * ... perSite
     */
    public MultiPagedMessage(String command, String header, int perSite, List<String> elements, String previous, String next, int pointer) {
        this.command = command;
        this.header = header;
        this.perSite = perSite;
        this.elements = elements;
        this.previous = previous;
        this.next = next;
        this.pointer = pointer;
    }

    public BaseComponent[] create() {
        updateTitle();
        ComponentBuilder builder = new ComponentBuilder("");

        builder.append(format(header));
        builder.append("\n");

        int siteNumberToDisplay = pointer;

        for (int i = siteNumberToDisplay * perSite; i < (siteNumberToDisplay + 1) * perSite; i++) {
            if (i < elements.size()) {
                builder.append(TextComponent.fromLegacyText(format(elements.get(i))));
                builder.append("\n");
            }
        }

        pointer = siteNumberToDisplay;
        String cmd = command + " ";

        if (siteNumberToDisplay > 0) {
            builder.append(format(previous))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd + --pointer));
        }
        pointer = siteNumberToDisplay;

        if (siteNumberToDisplay < Math.ceil((double) elements.size() / (double) perSite) - 1) {
            builder.append(format(next))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd + ++pointer));
        }
        return builder.create();
    }

    private String format(String header) {
        return ChatColor.translateAlternateColorCodes('&', header);
    }


    /**
     * Updates the title accordingly to the site number
     */
    private void updateTitle() {
        this.header = this.header.replace("%site%", "" + (pointer + 1))
                .replace("%maxsite%", (int) Math.ceil((double) elements.size() / (double) perSite) + "");
    }

    public static class Builder {

        private String command;
        private String header;
        private int perSite;
        private List<String> elements;
        private String previous, next;

        private int pointer;

        public Builder(int perSite, List<String> elements) {
            this.perSite = perSite;
            this.elements = elements;
        }

        public Builder() {
        }

        public Builder setHeaderString(String header) {
            this.header = header;
            return this;
        }

        public Builder setPerSite(int perSite) {
            this.perSite = perSite;
            return this;
        }

        public Builder setElements(List<String> elements) {
            this.elements = elements;
            return this;
        }

        public Builder setPreviousString(String previous) {
            this.previous = previous;
            return this;
        }

        public Builder setNextString(String next) {
            this.next = next;
            return this;
        }

        public Builder setCommand(String command, String[] args) {
            this.command = "/" + command;
            if (args.length == 1) {
                try {
                    pointer = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {

                }
            }
            return this;
        }

        public MultiPagedMessage build() {
            if (command == null || header == null || perSite < 1 || elements == null || previous == null || next == null)
                throw new NullPointerException("Cant create a MultiPagedMessage with null parameters!");
            return new MultiPagedMessage(command, header, perSite, elements, previous, next, pointer);
        }
    }
}
