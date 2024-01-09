package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.TimeHelper;
import net.minecraft.block.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.lang.reflect.Field;

public class FastBreak2 extends Module {

    public FastBreak2() {
        super("FastBreak2", 0, Category.PLAYER);
    }

    ModeSetting mode;
    NumberSetting speed;
    NumberSetting delay;
    BooleanSetting nexusProtection;
    TimeHelper time = new TimeHelper();

    private boolean bzs = false;
    private float bzx = 0.0f;
    public BlockPos blockPos;
    public EnumFacing facing;
    NumberSetting packetspeed;


    public void init() {
        mode = new ModeSetting("Mode ", "Packet", new String[]{ "Packet", "FastPacket"});
        this.delay = new NumberSetting("Vanilla Delay", 100, 100, 1000, 100F);
        packetspeed = new NumberSetting("Packet Speed", 1.4, 1.0, 3.0, 0.1);
        this.speed = new NumberSetting("Potion Speed", 1, 0, 4, 1f);
        this.nexusProtection = new BooleanSetting("Nexus Protection", true);
        addSetting(speed, mode, delay, nexusProtection, packetspeed);
        super.init();

    }

    public void onEvent(Event<?> event) {
        if (event instanceof EventUpdate) {
            setTag(mode.getMode());
            if (nexusProtection.enable) {
                if (mc.playerController.getIsHittingBlock()) {
                    if (mc.objectMouseOver != null) {
                        BlockPos p = mc.objectMouseOver.getBlockPos();
                        if (p != null) {
                            Block bl = mc.theWorld.getBlockState(p).getBlock();
                            if (bl == Blocks.end_stone) {
                                removePotionEffect();
                                return;
                            }

                        }
                    }
                }
            }
            if (mode.getMode().equals("Potion")) {
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 9810, (int) speed.value));
            } else {
                this.removePotionEffect();
            }
            if (mode.getMode().equals("Packet")) {
                if (this.bzs) {
                    Block block = mc.theWorld.getBlockState(this.blockPos).getBlock();
                    this.bzx += (float) ((double) block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) * packetspeed.getValue());
                    if (this.bzx >= 1.0F) {
                        mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                        this.bzx = 0.0f;
                        this.bzs = false;
                    }
                }
            }
            if (mode.getMode().equals("FastPacket")) {
                if (bzs) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing));
                      }
            }
        }


        if(event instanceof EventPacket) {
            EventPacket eventPacket = ((EventPacket) event);
            if (eventPacket.getPacket() instanceof C07PacketPlayerDigging) {
                C07PacketPlayerDigging  c07PacketPlayerDigging = (C07PacketPlayerDigging) eventPacket.getPacket();
                if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    this.bzs = true;
                    this.blockPos = c07PacketPlayerDigging.getPosition();
                    this.facing = c07PacketPlayerDigging.getFacing();
                    this.bzx = 0.0f;
                } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    this.bzs = false;
                    this.blockPos = null;
                    this.facing = null;
                }
            }
        }

    }

    public void removePotionEffect(){
        mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
    }

    public void onDisable() {
        super.onDisable();
        this.removePotionEffect();
    }
}