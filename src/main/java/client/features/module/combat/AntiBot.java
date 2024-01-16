package client.features.module.combat;

import java.util.*;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.utils.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.world.WorldSettings;

public final class AntiBot extends Module {

    public AntiBot() {
super("AntiBot",0, Category.COMBAT);
    }
ModeSetting mode;
    Entity currentEntity;
    Entity[] playerList;
    int index;
    boolean next;
       public static List<EntityPlayer> invalid = new ArrayList<>();
    @Override
    public void init() {
        super.init();
        mode = new ModeSetting("Mode ", "Shotbow", new String[]{"Hypixel","Mineplex", "Shotbow","Matrix"});
        addSetting(mode);
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(mode.getMode());
            switch (mode.getMode()) {
                case "Hypixel":

                    break;
                case "Mineplex":
                    Iterator var2 = mc.theWorld.getLoadedEntityList().iterator();

                    while (var2.hasNext()) {
                        Entity entity = (Entity) var2.next();
                        if (entity instanceof EntityPlayer) {
                            EntityPlayer bot = (EntityPlayer) entity;
                            if (entity.ticksExisted < 2 && bot.getHealth() < 20.0F && bot.getHealth() > 0.0F && entity != mc.thePlayer) {
                                mc.theWorld.removeEntity(entity);
                                invalid.add((EntityPlayer) entity);
                            }
                        }
                    }
                    break;
                case "Shotbow":
                    for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                        if (entity instanceof EntityPlayer) {
                            if (entity == mc.thePlayer)
                                return;
                            if (entity.ticksExisted < 100 && isNoArmor((EntityPlayer) entity)) {
                                mc.theWorld.removeEntity(entity);
                                ChatUtils.printChat("[AntiBot] Remove " + currentEntity.getName());
                            }


                        }

                    }
                    break;
            }
        }
        if(e instanceof EventPacket) {
            if (e instanceof EventPacket) {
                EventPacket event = ((EventPacket) e);
                if(event.getPacket() instanceof S38PacketPlayerListItem) {
                    S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
                    S38PacketPlayerListItem.AddPlayerData data = packet.getEntries().get(0);
                if(data.getGameMode() == WorldSettings.GameType.NOT_SET)
                    event.cancel();
                }
            }
        }

    }

    private boolean isEntityBot(Entity entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        } else if (mc.getCurrentServerData() == null) {
            return false;
        } else {
            return mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel") && entity.getDisplayName().getFormattedText().startsWith("ยง") || !this.isOnTab(entity) && mc.thePlayer.ticksExisted > 100;
        }
    }

    private boolean isOnTab(Entity entity) {
        Iterator var2 = mc.getNetHandler().getPlayerInfoMap().iterator();

        NetworkPlayerInfo info;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            info = (NetworkPlayerInfo)var2.next();
        } while(!info.getGameProfile().getName().equals(entity.getName()));

        return true;
    }
    public static List<EntityPlayer> getInvalid() {
        return invalid;
    }
    private static boolean isNoArmor(final EntityPlayer entity) {
        for (int i = 0; i < 4; ++i) {
            if (entity.getEquipmentInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

}
