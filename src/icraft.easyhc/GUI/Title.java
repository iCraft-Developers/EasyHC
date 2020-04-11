package icraft.easyhc.GUI;

import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {
    private int fadeInTicks;
    private int displayTicks;
    private int fadeOutTicks;
    private PacketPlayOutTitle packet;
    private PacketPlayOutTitle timesPacket;



    public enum Type {
        TITLE(PacketPlayOutTitle.EnumTitleAction.TITLE),
        SUBTITLE(PacketPlayOutTitle.EnumTitleAction.SUBTITLE),
        ACTIONBAR(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR),
        CLEAR(PacketPlayOutTitle.EnumTitleAction.CLEAR);

        PacketPlayOutTitle.EnumTitleAction titleAction;

        private Type(PacketPlayOutTitle.EnumTitleAction titleType) {
            titleAction = titleType;
        }
    }


    public static PacketPlayOutTitle.EnumTitleAction getType(Type type){
        return type.titleAction;
    }



    public Title(String text, Type titleAction, int fadeInTicks, int displayTicks, int fadeOutTicks){
        IChatBaseComponent json = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
        packet = new PacketPlayOutTitle(getType(titleAction), json);
        this.fadeInTicks = fadeInTicks;
        this.displayTicks = displayTicks;
        this.fadeOutTicks = fadeOutTicks;
        timesPacket = new PacketPlayOutTitle(fadeInTicks, displayTicks, fadeOutTicks);
    }

    public void show(Player p){
        CraftPlayer cp = ((CraftPlayer) p);
        cp.getHandle().playerConnection.sendPacket(packet);
        cp.getHandle().playerConnection.sendPacket(timesPacket);
    }
}
