package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.utils.PacketUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AntiObbyTrap extends Module {
    public BooleanSetting antisandtrap ;
    public BooleanSetting noswing ;
    public static boolean Field2468 = false;

    public AntiObbyTrap() {
        super("AntiObbyTrap", 0, Category.PLAYER);
    }
    public void init(){
        super.init();
       noswing = new BooleanSetting("No Swing", false);
       antisandtrap = new BooleanSetting("Anti Sand Trap", true);
       addSetting(noswing, antisandtrap);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventMotion) {
            if (!e.isPost()) {
                if (!ModuleManager.getModulebyClass(Freecam.class).isEnable()) {
                    Field2468 = false;
                    Block var3 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)).getBlock();
                    Block var4 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock();
                    Block var5 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)).getBlock();
                    Block var6 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
                    BlockPos var7 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
                    BlockPos var8 = new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ);
                    BlockPos var9 = new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY, mc.thePlayer.posZ);
                    if (var4 != Blocks.air && var4 != Blocks.bedrock && var4 != Blocks.obsidian && var3 == Blocks.obsidian && mc.thePlayer.hurtTime > 8) {
                        Field2468 = true;
                        this.sendBlockPos(var7, EnumFacing.DOWN);
                    }

                    if (var4 != Blocks.air && (var4 == Blocks.bedrock || var4 == Blocks.obsidian) && var3 == Blocks.obsidian && mc.thePlayer.hurtTime > 8) {
                        if (var6 != Blocks.air) {
                            Field2468 = true;
                            this.sendBlockPos(var9, EnumFacing.EAST);
                        }

                        if (var5 != Blocks.air) {
                            Field2468 = true;
                            EventMotion event = (EventMotion) e;
                            event.setPitch(90.0F);
                            this.sendBlockPos(var8, EnumFacing.UP);
                        }
                    }

                }
            }
        }
        if(e instanceof  EventMotion) {
            if (!e.isPost()) {
                if (!ModuleManager.getModulebyClass(Freecam.class).isEnable()) {
                    Field2468 = false;
                    BlockPos var2 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Block var3 = mc.theWorld.getBlockState(var2.up()).getBlock();
                    if (var3 == Blocks.gravel || var3 == Blocks.sand) {
                        var2 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ);
                    }

                    Block var4 = mc.theWorld.getBlockState(var2).getBlock();
                    if (var4 == Blocks.gravel || var4 == Blocks.sand) {
                        Field2468 = false;
                        if (this.antisandtrap.isEnable()) {
                            EventMotion event = (EventMotion) e;
                            event.setPitch(90.0F);
                            this.sendBlockPos(var2, EnumFacing.UP);
                        }
                    }

                }
            }
        }
    }


    public void sendBlockPos(BlockPos blockPos, EnumFacing enumFacing) {
        if (this.noswing.isEnable()) {
           PacketUtils.sendPacket(new C0APacketAnimation());
        }

        mc.thePlayer.swingItem();
        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, blockPos, enumFacing));
        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK,blockPos, enumFacing));
        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, blockPos, enumFacing));
    }
}
