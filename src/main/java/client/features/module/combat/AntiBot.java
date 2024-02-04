package client.features.module.combat;

import java.util.*;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.world.WorldSettings;

import static client.utils.PlayerUtils.isInsideBlock;

public final class AntiBot extends Module {

    public AntiBot() {
        super("AntiBot", 0, Category.COMBAT);
    }

    static ModeSetting mode;
    Entity currentEntity;
    Entity[] playerList;
    int index;
    boolean next;
   public static NumberSetting matrixflyingmotiony;
    private static List<EntityPlayer> bots, removed;
    public static List<EntityPlayer> invalid = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        mode = new ModeSetting("Mode ", "Shotbow", new String[]{"Hypixel", "Mineplex", "Shotbow", "ShotbowTeams", "MatrixFlying"});
        addSetting(mode);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
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

                    break;
            }
        }
        if (e instanceof EventPacket) {
            if (e instanceof EventPacket) {
                EventPacket event = ((EventPacket) e);
                if (event.getPacket() instanceof S38PacketPlayerListItem) {
                    S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
                    S38PacketPlayerListItem.AddPlayerData data = packet.getEntries().get(0);
                    if (data.getGameMode() == WorldSettings.GameType.NOT_SET)
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

            info = (NetworkPlayerInfo) var2.next();
        } while (!info.getGameProfile().getName().equals(entity.getName()));

        return true;
    }


    private static boolean isNoArmor(final EntityPlayer entity) {
        for (int i = 0; i < 4; ++i) {
            if (entity.getEquipmentInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBot(EntityPlayer entityPlayer) {
        if (!(ModuleManager.getModulebyClass(AntiBot.class).isEnable()))
            return false;
        switch (mode.getMode()) {
            case "Shotbow":
                return entityPlayer.getHealth() - entityPlayer.getAbsorptionAmount() != 0.1f || mc.getNetHandler().getPlayerInfo(entityPlayer.getName()) == null;
            case "Hypixel":
                return entityPlayer.isInvisible();
            case"ShotbowTeams":
                return entityPlayer.getTeam()== null;
            case"MatrixFlying":
                return isFlying(entityPlayer);
        }
        return bots.contains(entityPlayer);
    }
    private static boolean isFlying(EntityPlayer entity) {
        if (mc.theWorld.getCollidingBoundingBoxes((Entity)entity, entity.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D)).isEmpty()) {
                return true;
        } else if (isInsideBlock(entity)) {
            return true;
        }
        return false;
    }


}
