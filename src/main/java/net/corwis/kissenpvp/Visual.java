package net.corwis.kissenpvp;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.corwis.kissenpvp.suffix.SuffixCommand;
import net.corwis.kissenpvp.suffix.SuffixManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Visual extends JavaPlugin implements Listener {

    private VisualManager visualManager;
    private SuffixManager suffixManager;
    private VisualChatRenderer chatRenderer;
    private MiniMessage mm;

    @Override
    public void onEnable() {
        this.mm = MiniMessage.miniMessage();
        saveDefaultConfig();
        this.visualManager = new VisualManager();
        this.suffixManager = new SuffixManager();

        this.chatRenderer = new VisualChatRenderer(
                visualManager,
                suffixManager,
                this
        );
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("suffix").setExecutor(new SuffixCommand(suffixManager));
        applyVisualsToOnlinePlayers();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.remove(player);
            suffixManager.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        visualManager.update(event.getPlayer(), buildVisualDataFromConfig(getConfig()));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (getConfig().getBoolean("chat.enabled", true)) {
            event.renderer(chatRenderer);
        }
    }

    public VisualManager getVisualManager() {
        return visualManager;
    }

    public VisualData buildVisualDataFromConfig(FileConfiguration cfg) {
        String prefix = cfg.getString("visuals.prefix",
                "<gray>[<gradient:gray:dark_gray>Spieler</gradient>] ");
        String suffix = cfg.getString("visuals.suffix", "");
        int priority = cfg.getInt("visuals.priority", 100);
        String header = cfg.getString("visuals.header",
                "<green>Willkommen auf KissenPvP!");
        String footer = cfg.getString("visuals.footer",
                "<gray>Du bist <b>Spieler</b>.");

        return new VisualData(
                mm.deserialize(prefix),
                mm.deserialize(suffix),
                priority,
                mm.deserialize(header),
                mm.deserialize(footer)
        );
    }

    public void applyVisualsToOnlinePlayers() {
        FileConfiguration cfg = getConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            visualManager.update(player, buildVisualDataFromConfig(cfg));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("visual")) return false;
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("visual.reload")) {
                sender.sendMessage(mm.deserialize(
                        getConfig().getString("messages.no-permission",
                                "<red>Keine Berechtigung.</red>")
                ));
                return true;
            }

            reloadConfig();
            applyVisualsToOnlinePlayers();
            sender.sendMessage(mm.deserialize(
                    getConfig().getString("messages.reload-success",
                            "<green>Visual Konfiguration neu geladen.</green>")
            ));
            return true;
        }
        return false;
    }
}
