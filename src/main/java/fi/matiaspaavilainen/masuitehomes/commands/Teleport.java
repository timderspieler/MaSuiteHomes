package fi.matiaspaavilainen.masuitehomes.commands;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitehomes.Home;
import fi.matiaspaavilainen.masuitehomes.MaSuiteHomes;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Teleport {
    public void teleport(ProxiedPlayer p, String hs) {
        Formator formator = new Formator();
        Configuration config = new Configuration();
        Home home = new Home();
        home = home.findLike(hs, p.getUniqueId());

        if (home.getServer() == null) {
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home-not-found"));
            return;
        }
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            if (!p.getServer().getInfo().getName().equals(home.getServer())) {
                p.connect(ProxyServer.getInstance().getServerInfo(home.getServer()));
            }
            out.writeUTF("HomePlayer");
            out.writeUTF(String.valueOf(p.getUniqueId()));
            out.writeUTF(home.getWorld());
            out.writeDouble(home.getX());
            out.writeDouble(home.getY());
            out.writeDouble(home.getZ());
            out.writeFloat(home.getYaw());
            out.writeFloat(home.getPitch());
            final Home h = home;
            ProxyServer.getInstance().getScheduler().schedule(new MaSuiteHomes(), () -> ProxyServer.getInstance().getServerInfo(h.getServer()).sendData("BungeeCord", b.toByteArray()), 50, TimeUnit.MILLISECONDS);
            formator.sendMessage(p, config.load("homes", "messages.yml").getString("home.teleported").replace("%home%", home.getName()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }
}
