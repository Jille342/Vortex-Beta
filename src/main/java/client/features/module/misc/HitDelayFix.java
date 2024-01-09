package client.features.module.misc;

import client.features.module.Module;
import org.lwjgl.input.Keyboard;

public class HitDelayFix extends Module {
    public HitDelayFix() {
        super("HitDelayFix", Keyboard.KEY_NONE, Module.Category.MISC);
    }
}
