package client.utils;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Mouse;

public class PlayerHelper {


    final Minecraft mc = Minecraft.getMinecraft();
    public static void holdState(int button, boolean state) {
        Mouse.poll();
        MouseEvent mouseEvent = new MouseEvent();
        ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, mouseEvent, button, "button");
        ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, mouseEvent, state, "buttonstate");
        MinecraftForge.EVENT_BUS.post(mouseEvent);
    }


    public static void legitAttack() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.objectMouseOver == null || mc.thePlayer.isRiding()) {
            return;
        }

        switch (mc.objectMouseOver.typeOfHit) {
            case ENTITY:
                mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                break;

            case BLOCK:
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                if (!mc.theWorld.isAirBlock(blockpos)) {
                    mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                    break;
                }

            case MISS:
                mc.thePlayer.swingItem();
        }

        mc.thePlayer.swingItem();
    }

}
