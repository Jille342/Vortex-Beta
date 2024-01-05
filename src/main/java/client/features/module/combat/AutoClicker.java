package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoClicker extends Module {
//    private final BooleanSetting ignoreFriendsSetting = registerSetting(BooleanSetting.builder()
    //          .name("Ignore Friends")
    //          .value(true)
    //        .build()
    // );





    private final TimeHelper leftStopWatch = new TimeHelper();
    private final TimeHelper rightStopWatch = new TimeHelper();

    private boolean attacked;
    private boolean clicked;
    private int breakTick;

    BooleanSetting leftClickSetting;

    BooleanSetting ignoreTeamsSetting;
    NumberSetting leftCpsSetting;
    public AutoClicker() {
        super("Auto Clicker", Keyboard.KEY_NONE, Category.COMBAT);



    }
    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
         this.leftCpsSetting = new NumberSetting("LeftCPS", 7, 0, 20, 1f);


        addSetting(leftClickSetting, ignoreTeamsSetting, leftClickSetting, leftCpsSetting);
    }

    @Override
    public void onDisable() {
        attacked = false;
        clicked = false;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if (mc.gameSettings.keyBindAttack.isKeyDown() && shouldClick(true)) {
                doLeftClick();
            }

            }
        }
    


    private void doLeftClick() {
        int cps = (int) leftCpsSetting.getValue();
        if (attacked && mc.thePlayer.ticksExisted % RandomUtils.nextInt(1, 3) == 0) {
            PlayerHelper.holdState(0, false);
            attacked = false;
            return;
        }

        if (!leftStopWatch.hasReached(calculateTime(cps))) {
            return;
        }
PlayerHelper.legitAttack();
        PlayerHelper.holdState(0, true);
        attacked = true;
    }


    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public boolean shouldClick(boolean left) {
        if (mc.isGamePaused() || !mc.inGameHasFocus) {
            return false;
        }

        if (mc.thePlayer.getItemInUseCount() > 0) {
            return false;
        }

        if (mc.objectMouseOver != null && left) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if (p != null) {
                Block bl = mc.theWorld.getBlockState(p).getBlock();
                if (bl instanceof BlockAir || bl instanceof BlockLiquid) {
                    return true;
                }


                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    if (breakTick > 1) {
                        return false;
                    }
                    breakTick++;
                } else {
                    breakTick = 0;
                }
            } else {
                breakTick = 0;

                if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    Entity entity = mc.objectMouseOver.entityHit;
                    if (entity instanceof EntityPlayer && !entity.isDead) {
                        return (!ignoreTeamsSetting.enable || !ServerHelper.isTeammate((EntityPlayer) entity));
                    }
                }
            }
        }
        return true;
    }
}
