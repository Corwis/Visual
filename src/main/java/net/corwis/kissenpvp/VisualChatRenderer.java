package net.corwis.kissenpvp;

import io.papermc.paper.chat.ChatRenderer;
import net.corwis.kissenpvp.suffix.PlayerSuffixData;
import net.corwis.kissenpvp.suffix.SuffixManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class VisualChatRenderer implements ChatRenderer {

    private final VisualManager visualManager;
    private final SuffixManager suffixManager;
    private final Visual plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public VisualChatRenderer(VisualManager visualManager,
                              SuffixManager suffixManager,
                              Visual plugin) {
        this.visualManager = visualManager;
        this.suffixManager = suffixManager;
        this.plugin = plugin;
    }

    @Override
    public @NotNull Component render(@NotNull Player source,
                                     @NotNull Component sourceDisplayName,
                                     @NotNull Component message,
                                     @NotNull Audience viewer) {

        VisualData visual = visualManager.get(source);
        Component prefix = visual != null && visual.prefix() != null
                ? visual.prefix()
                : Component.empty();

        Component suffix = Component.empty();
        PlayerSuffixData suffixData = suffixManager.get(source.getUniqueId());
        if (suffixData.isChatEnabled() && suffixData.getSelectedSuffix() != null) {
            String raw = suffixData.getSuffixes().get(suffixData.getSelectedSuffix());
            if (raw != null) {
                suffix = mm.deserialize(raw);
            }
        }

        Component splitter = mm.deserialize(plugin.getConfig().getString("chat.splitter", "»"));
        Component name = mm.deserialize(plugin.getConfig().getString("chat.name-color", "<aqua>"))
                .append(Component.text(source.getName()));

        Component msg = mm.deserialize(plugin.getConfig().getString("chat.message-color", "<white>"))
                .append(message);

        Component line = Component.empty()
                .append(prefix)
                .append(name)
                .append(suffix)
                .appendSpace()
                .append(splitter)
                .appendSpace()
                .append(msg);

        if (viewer instanceof Player target && !target.equals(source)) {
            Component altered = line.replaceText(builder -> {
                builder.match("@" + target.getName());
                builder.replacement(mm.deserialize(
                        plugin.getConfig().getString("chat.mention-color", "<yellow>")
                                + "@" + target.getName()
                ));
            });
            if (!altered.equals(line)) {
                line = altered;
                if (plugin.getConfig().getBoolean("chat.mention-sound", true)) {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            target.playSound(target, Sound.ENTITY_ARROW_HIT_PLAYER, .5f, 1f)
                    );
                }
            }
        }
        return line;
    }
}
