package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.MathUtils;
import client.utils.TimeHelper;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

public class ChestStealer extends Module {
    public ChestStealer() {
        super("ChestStealer",0, Category.PLAYER);
        this.blacklist = new String[] { "menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "quick", "travel", "cake", "war", "pvp" };
    }
    TimeHelper time = new TimeHelper();
    TimeHelper timer1 = new TimeHelper();
NumberSetting delay1;
    private final String[] blacklist;
    public void init() {
        this.delay1 = new NumberSetting("Delay", 22, 0, 100, 1f);
        addSetting(delay1); super.init();

    }
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            final  float delay = (float) (delay1.value *10);
            if((mc.thePlayer.openContainer != null) && ((mc.thePlayer.openContainer instanceof ContainerChest))) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                final String lowerCase = chest.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase();
                final String[] ys = this.blacklist;
                for (int length = ys.length, i = 0; i < length; ++i) {
                    if (lowerCase.contains(ys[i])) {
                        return;
                    }
                }
                for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                    if((chest.getLowerChestInventory().getStackInSlot(i) != null) && time.hasReached(delay)) {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        this.time.reset();
                    }
                }

                if (isChestEmpty(chest)) {
                    if (this.timer1.hasReached(MathUtils.randomNumber(150, 75))) {
                      mc.thePlayer.closeScreen();
                    }
                } else {
                    this.timer1.reset();
                }


            }


        }


    }
    private boolean isValidItem(ItemStack itemStack) {
        return (itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemPotion || itemStack.getItem() instanceof ItemBlock);
    }
    private boolean isChestEmpty(ContainerChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack != null && isValidItem(stack))
                return false;
        }
        return true;
    }
}