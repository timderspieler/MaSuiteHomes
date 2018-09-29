package fi.matiaspaavilainen.masuitehomes;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fi.matiaspaavilainen.masuitecore.Updator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import fi.matiaspaavilainen.masuitecore.database.Database;
import fi.matiaspaavilainen.masuitehomes.commands.Teleport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MaSuiteHomes extends Plugin implements Listener {

    static Database db = new Database();
    @Override
    public void onEnable() {
        super.onEnable();

        //Configs
        Configuration config = new Configuration();
        config.create(this, "homes", "messages.yml");
        config.create(this, "homes", "syntax.yml");
        getProxy().getPluginManager().registerListener(this, this);
        //Commands
        /*getProxy().getPluginManager().registerCommand(this, new Teleport());
        getProxy().getPluginManager().registerCommand(this, new Set());
        getProxy().getPluginManager().registerCommand(this, new Delete());
        getProxy().getPluginManager().registerCommand(this, new List());*/

        db.connect();
        db.createTable("homes",
                "(id INT(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, owner VARCHAR(36) NOT NULL, server VARCHAR(100) NOT NULL, world VARCHAR(100) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        new Updator().checkVersion(this.getDescription(), "60632");
    }

    @Override
    public void onDisable(){
        db.hikari.close();
    }
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) throws IOException {
        if(!e.getTag().equals("BungeeCord")){
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String subchannel = in.readUTF();
        if(subchannel.equals("HomeCommand")){
            Teleport teleport = new Teleport();
            ProxiedPlayer p = getProxy().getPlayer(in.readUTF());
            if(p == null){
                return;
            }
            teleport.teleport(p, in.readUTF());
            sendCooldown(p);
        }
    }

    private void sendCooldown(ProxiedPlayer p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("HomeCooldown");
        out.writeUTF(String.valueOf(p.getUniqueId()));
        try {
            Thread.sleep(200);
            out.writeLong(System.currentTimeMillis());
            p.getServer().sendData("BungeeCord", out.toByteArray());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
