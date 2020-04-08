package icraft.easyhc.GUI;


import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GUIPlayer {
    private Player p;

    public GUIPlayer(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public void showTitle(String title, String subtitle, int fadeInTicks, int displayTicks, int fadeOutTicks){

        IChatBaseComponent json = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        PacketPlayOutTitle t = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, json);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(t);

        if(subtitle != null) {
            IChatBaseComponent chatSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle st = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubtitle);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(st);
        } else {
            PacketPlayOutTitle st = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, null);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(st);
        }

        PacketPlayOutTitle packet = new PacketPlayOutTitle(fadeInTicks, displayTicks, fadeOutTicks);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }


    public void showTitle(String actionbar, int fadeInTicks, int displayTicks, int fadeOutTicks){
        IChatBaseComponent json = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + actionbar + "\"}");
        PacketPlayOutTitle t = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, json);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(t);

        PacketPlayOutTitle packet = new PacketPlayOutTitle(fadeInTicks, displayTicks, fadeOutTicks);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

    }


}
