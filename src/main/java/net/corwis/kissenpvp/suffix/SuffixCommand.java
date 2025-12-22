package net.corwis.kissenpvp.suffix;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class SuffixCommand implements CommandExecutor {

    private static final Set<String> RESERVED =
            Set.of("grant", "revoke", "list", "select", "chat");
    private final SuffixManager suffixManager;
    private final MiniMessage mm = MiniMessage.miniMessage();
    public SuffixCommand(SuffixManager suffixManager) {
        this.suffixManager = suffixManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize("<red>Usage: /suffix <grant|revoke|list|select|chat></red>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "grant" -> {
                if (!sender.hasPermission("kissen.command.suffix.grant")) return true;
                if (args.length < 4) return true;
                Player target = Bukkit.getPlayer(args[1]);if (target == null) return true;

                String name = args[2].toLowerCase();
                if (RESERVED.contains(name)) {
                    sender.sendMessage(mm.deserialize("<red>Dieser Suffixname ist reserviert.</red>"));
                    return true;
                }
                String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                PlayerSuffixData data = suffixManager.get(target.getUniqueId());
                data.addSuffix(name, value);
                sender.sendMessage(mm.deserialize("<green>Suffix vergeben.</green>"));
                return true;
            }

            case "revoke" -> {
                if (!sender.hasPermission("kissen.command.suffix.revoke")) return true;
                if (args.length != 3) return true;
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) return true;
                suffixManager.get(target.getUniqueId()).removeSuffix(args[2]);
                sender.sendMessage(mm.deserialize("<yellow>Suffix entfernt.</yellow>"));
                return true;
            }

            case "list" -> {
                if (!sender.hasPermission("kissen.command.suffix.list")) return true;
                if (args.length != 2) return true;

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) return true;
                PlayerSuffixData data = suffixManager.get(target.getUniqueId());
                sender.sendMessage(mm.deserialize("<gray>Suffixes von <white>" + target.getName() + "</white>:</gray>"));
                if (data.getSuffixes().isEmpty()) {
                    sender.sendMessage(mm.deserialize("<dark_gray>- keine</dark_gray>"));
                    return true;
                }
                data.getSuffixes().forEach((k, v) ->
                        sender.sendMessage(mm.deserialize("<white>- " + k + "</white> <gray>=</gray> " + v))
                );
                return true;
            }
            case "select" -> {
                if (!(sender instanceof Player player)) return true;
                if (!sender.hasPermission("kissen.suffix.command.select")) return true;
                if (args.length != 2) return true;

                PlayerSuffixData data = suffixManager.get(player.getUniqueId());
                if (!data.hasSuffix(args[1])) {player.sendMessage(mm.deserialize("<red>Du besitzt dieses Suffix nicht.</red>"));
                    return true;
                }
                data.setSelectedSuffix(args[1]);
                player.sendMessage(mm.deserialize("<green>Suffix ausgewählt.</green>"));
                return true;
            }

            case "chat" -> {
                if (!(sender instanceof Player player)) return true;
                if (!sender.hasPermission("kissen.suffix.command.chat.switch")) return true;
                PlayerSuffixData data = suffixManager.get(player.getUniqueId());
                data.toggleChat();
                player.sendMessage(mm.deserialize(data.isChatEnabled() ? "<green>Suffix im Chat aktiviert.</green>" : "<red>Suffix im Chat deaktiviert.</red>"
                ));
                return true;
            }
        }
        return true;
    }
}
