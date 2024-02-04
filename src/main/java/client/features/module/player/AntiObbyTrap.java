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
    public static boolean isTrapped = false;

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
                    isTrapped = false;
                    Block var3 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)).getBlock();
                    Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock();
                    Block var5 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)).getBlock();
                    Block var6 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock();
                    BlockPos var7 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
                    BlockPos var8 = new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ);
                    BlockPos var9 = new BlockPos(mc.thePlayer.posX + 1.0, mc.thePlayer.posY, mc.thePlayer.posZ);
                    if (block != Blocks.air && block != Blocks.bedrock && block != Blocks.obsidian && var3 == Blocks.obsidian && mc.thePlayer.hurtTime > 8) {
                        isTrapped = true;
                        this.sendPacket(var7, EnumFacing.DOWN);
                    }

                    if (block != Blocks.air && (block == Blocks.bedrock || block == Blocks.obsidian) && var3 == Blocks.obsidian && mc.thePlayer.hurtTime > 8) {
                        if (var6 != Blocks.air) {
                            isTrapped = true;
                            this.sendPacket(var9, EnumFacing.EAST);
                        }

                        if (var5 != Blocks.air) {
                            isTrapped = true;
                            EventMotion event = (EventMotion) e;
                            event.setPitch(90.0F);
                            this.sendPacket(var8, EnumFacing.UP);
                        }
                    }

                }
            }
        }
        if(e instanceof  EventMotion) {
            if (!e.isPost()) {
                if (!ModuleManager.getModulebyClass(Freecam.class).isEnable()) {
                    isTrapped = false;
                    BlockPos blockpos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Block var3 = mc.theWorld.getBlockState(blockpos.up()).getBlock();
                    if (var3 == Blocks.gravel || var3 == Blocks.sand) {
                        blockpos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ);
                    }

                    Block block = mc.theWorld.getBlockState(blockpos).getBlock();
                    if (block == Blocks.gravel || block == Blocks.sand) {
                        isTrapped = false;
                        if (this.antisandtrap.isEnable()) {
                            EventMotion event = (EventMotion) e;
                            event.setPitch(90.0F);
                            this.sendPacket(blockpos, EnumFacing.UP);
                        }
                    }

                }
            }
        }
    }


    public void sendPacket(BlockPos blockPos, EnumFacing enumFacing) {
        if (this.noswing.isEnable()) {
           PacketUtils.sendPacket(new C0APacketAnimation());
        } else {
            mc.thePlayer.swingItem();
        }


        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, blockPos, enumFacing));
        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK,blockPos, enumFacing));
        PacketUtils.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, blockPos, enumFacing));
    }
}
