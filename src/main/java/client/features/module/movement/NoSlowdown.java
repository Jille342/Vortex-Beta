package client.features.module.movement;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.utils.PlayerUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import static net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;

public class NoSlowdown extends Module {
    public NoSlowdown() {
        super("NoSlowdown", 0, Category.MOVEMENT);

    }
 public static ModeSetting mode;

    public void init() {
        super.init();
        mode = new ModeSetting("Mode", "NCP", new String[]{"NCP", "AAC"});
       addSetting(mode);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            setTag(mode.getMode());

            if (mode.getMode().equals("NCP")) {
                if (mc.thePlayer.isBlocking() && PlayerUtils.isMoving() ) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN
                        ));
                    } else if (e.isPost()){
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                                new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));

                    }
                }
            }
        }
    }
}