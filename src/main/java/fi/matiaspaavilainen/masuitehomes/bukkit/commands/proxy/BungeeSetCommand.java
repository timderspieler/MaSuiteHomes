package fi.matiaspaavilainen.masuitehomes.bukkit.commands.proxy;

import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitehomes.bukkit.MaSuiteHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BungeeSetCommand implements CommandExecutor {

    private MaSuiteHomes plugin;

    public BungeeSetCommand(MaSuiteHomes p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                plugin.formator.sendMessage(cs, plugin.config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            Location loc = p.getLocation();
            int max = plugin.getMaxHomes(p);
            String l = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
            switch (args.length) {
                case (0):
                    new BukkitPluginChannel(plugin, p, new Object[]{"SetHomeCommand", p.getName(), l, "home", max}).send();
                    break;
                case (1):
                    new BukkitPluginChannel(plugin, p, new Object[]{"SetHomeCommand", p.getName(), l, args[0], max}).send();
                    break;
                case (2):
                    if (p.hasPermission("masuitehomes.home.set.other")) {
                        new BukkitPluginChannel(plugin, p, new Object[]{"SetHomeOtherCommand", p.getName(), args[0], l, args[1], -1}).send();
                    } else {
                        plugin.formator.sendMessage(p, plugin.config.load(null, "messages.yml").getString("no-permission"));
                    }
                    break;
                default:
                    plugin.formator.sendMessage(p, plugin.config.load("homes", "syntax.yml").getString("home.set"));
                    break;
            }

            plugin.in_command.remove(cs);
        });

        return true;
    }
}