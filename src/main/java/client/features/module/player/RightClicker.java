package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.TimeHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

public class RightClicker  extends  Module{
    BooleanSetting blocksword;

    NumberSetting rightCPS;

    private boolean clicked;

    BooleanSetting blocksonly;
    TimeHelper timer = new TimeHelper();
    public RightClicker() {
        super("Right Clicker", Keyboard.KEY_NONE, Category.PLAYER);



    }

    public void init() {
        super.init();
        this.blocksword = new BooleanSetting("Block Sword", false);
        this.rightCPS = new NumberSetting("Right CPS", 7, 0, 20, 1f);
 this.blocksonly = new BooleanSetting("Blocks Only", true);
        addSetting(blocksword, rightCPS, blocksonly);
    }


    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if ( mc.gameSettings.keyBindUseItem.isKeyDown() && shouldClick(false)) {
                doRightClick();
            }
            }
        }

    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public boolean shouldClick(boolean left) {
        if (mc.isGamePaused() || !mc.inGameHasFocus) {
            return false;
        }
        if(blocksonly.enable &&!(mc.thePlayer.getHeldItem().getItem()  instanceof ItemBlock ))
        {
            return false;
        }
        if(!blocksword.enable && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword )
        {
            return false;
        }

        if (mc.thePlayer.getItemInUseCount() > 0) {
            return false;
        }

        return true;
    }
    private void doRightClick() {
        int cps = (int) rightCPS.getValue();
        if (clicked && mc.thePlayer.ticksExisted % RandomUtils.nextInt(1, 3) == 0) {
            PlayerHelper.holdState(1, false);
            clicked = false;
            return;
        }

        if (!timer.hasReached(calculateTime(cps))) {
            return;
        }

        PlayerHelper.holdState(1, true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
    }
}

