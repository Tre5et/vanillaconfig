package net.treset.vanillaconfig.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;

import java.util.Locale;

public class ClientTools {
    public static String getWorldId() {
        Minecraft cli = Minecraft.getInstance();

        if (cli.hasSingleplayerServer()) { //is player in singleplayer?
            IntegratedServer server = cli.getSingleplayerServer();

            if (server != null) {
                String name = server.getWorldData().getLevelName(); //get world name
                return name.toLowerCase(Locale.US).replaceAll("\\W", "_"); //lowercase and replace space with _
            }
        } else {
            ServerData server = cli.getCurrentServer();
            if(server == null) {
                return null;
            }

            if (server.isRealm()) {
                ClientPacketListener listener = cli.getConnection();
                if(listener == null) return null;
                Connection connection = listener.getConnection();

                String str = "realms_" + connection.getLoggableAddress(true); //get realms connection adress

                if (str.contains("/")) { //split string after /
                    str = str.substring(str.indexOf('/') + 1);
                }

                return str.replace(':', '_'); //replace : with _
            }

            return server.ip.replace(':', '_'); //get server address; replace : with _
        }
        return null;
    }

    public static boolean isInGame() {
        return getWorldId() != null && Minecraft.getInstance().screen == null;
    }
}
