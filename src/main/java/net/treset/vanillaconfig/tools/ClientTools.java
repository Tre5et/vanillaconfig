package net.treset.vanillaconfig.tools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;

import java.util.Locale;

public class ClientTools {
    public static String getWorldId() {
        MinecraftClient cli = MinecraftClient.getInstance();

        if (cli.isIntegratedServerRunning()) { //is player in singleplayer?
            IntegratedServer server = cli.getServer();

            if (server != null) {
                String name = server.getSaveProperties().getLevelName(); //get world name
                return name.toLowerCase(Locale.US).replaceAll("\\W", "_"); //lowercase and replace space with _
            }
        } else {
            ServerInfo server = cli.getCurrentServerEntry();
            if(server == null) {
                return null;
            }

            if (server.isRealm()) { //is player in realms?
                ClientPlayNetworkHandler handler = cli.getNetworkHandler();
                ClientConnection connection = handler != null ? handler.getConnection() : null;

                if (connection != null) {
                    String str = "realms_" + connection.getAddress().toString(); //get realms connection adress

                    if (str.contains("/")) { //split string after /
                        str = str.substring(str.indexOf('/') + 1);
                    }

                    return str.replace(':', '_'); //replace : with _
                }
            }

            return server.address.replace(':', '_'); //get server address; replace : with _
        }
        return null;
    }

    public static boolean isInGame() {
        return getWorldId() != null && MinecraftClient.getInstance().currentScreen == null;
    }
}
