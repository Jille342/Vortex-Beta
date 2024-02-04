package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.ChatUtils;
import org.lwjgl.input.Keyboard;

public class Debug extends Module {
    public Debug() {
        super("Debug", Keyboard.KEY_NONE, Module.Category.MISC);
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            ChatUtils.printChat("MotionY:"+String.valueOf(mc.thePlayer.motionY));
        }
    }
}
