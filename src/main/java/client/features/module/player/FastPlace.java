package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventTick;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class FastPlace extends Module {
  NumberSetting delay1;
 BooleanSetting blockonly;
    public final static Field rightClickDelayTimerField;

    static {
        rightClickDelayTimerField = ReflectionHelper.findField(Minecraft.class, "field_71467_ac", "rightClickDelayTimer");

        if (rightClickDelayTimerField != null) {
            rightClickDelayTimerField.setAccessible(true);
        }
    }

    public FastPlace() {
        super("FastPlace", 0, Category.PLAYER);

    }
    public void init() {
        this.delay1 = new NumberSetting("Delay", 0, 0, 4, 1f);
        addSetting(delay1, blockonly); super.init();
        this.blockonly = new BooleanSetting("Block Only", true);

    }

    public boolean canBeEnabled() {
        return rightClickDelayTimerField != null;
    }

    public void onEvent(Event event) {
        if (event instanceof EventTick) {
            if ( mc.inGameHasFocus && rightClickDelayTimerField != null) {
                if (blockonly.enable) {
                    ItemStack item = mc.thePlayer.getHeldItem();
                    if (item == null || !(item.getItem() instanceof ItemBlock)) {
                        return;
                    }
                }

                try {
                    int c = (int) delay1.value;
                    if (c == 0) {
                        rightClickDelayTimerField.set(mc, 0);
                    } else {
                        if (c == 4) {
                            return;
                        }

                        int d = rightClickDelayTimerField.getInt(mc);
                        if (d == 4) {
                            rightClickDelayTimerField.set(mc, c);
                        }
                    }
                } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {}
            }
        }
    }
}
