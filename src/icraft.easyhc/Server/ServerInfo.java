package icraft.easyhc.Server;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import icraft.easyhc.Main;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class ServerInfo {

    public ServerInfo() {
        Plugin plugin = Main.getPlugin(Main.class);
        ProtocolManager protocolManager = Main.protocolManager;

        protocolManager.addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        PacketType.Status.Server.SERVER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent e) {
                        if(e.getPacketType() == PacketType.Status.Server.SERVER_INFO){
                            WrappedServerPing ping = (WrappedServerPing) e.getPacket().getServerPings().read(0);
                            ArrayList<WrappedGameProfile> desc = new ArrayList<>();
                            desc.add(new WrappedGameProfile(UUID.randomUUID(), "§b§m-------------[§r§6iCraft.com.pl§b§m]-------------"));
                            desc.add(new WrappedGameProfile(UUID.randomUUID(), "§ePolska sieć serwerów Minecraft"));
                            desc.add(new WrappedGameProfile(UUID.randomUUID(), "§aSurvival + EasyHC §l✔"));
                            desc.add(new WrappedGameProfile(UUID.randomUUID(), "§cS§mky-Block §l❌"));
                            desc.add(new WrappedGameProfile(UUID.randomUUID(), "§o§8Sky-Block zostanie dodany w przyszlosci"));
                            ping.setPlayers(desc);
                            String[] motd = new String[2];
                            motd[0] = "   §b§m-------------[§r§6iCraft.com.pl§b§m]-------------§r";
                            motd[1] = "       §e§nStart pierwszej edycji:  §l25.05.2020";
                            ping.setMotD(String.join("\n", motd));


                            try {
                                URL url = new URL("https://minotar.net/helm/xdaanielx/64.png");
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ImageIO.write(ImageIO.read(url), "png", bos);
                                byte [] data = bos.toByteArray();
                                WrappedServerPing.CompressedImage compressedImage = new WrappedServerPing.CompressedImage("image/png", data);
                                ping.setFavicon(compressedImage);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

}
