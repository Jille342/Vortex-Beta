package client.features.module.render;

import client.features.module.Module;
import client.setting.BooleanSetting;

public class NoSwing extends Module {

    public static BooleanSetting enableServerSide;

    public NoSwing() {
        super("NoSwing", 0, Module.Category.RENDER);
    }

    @Override
    public void init(){
        enableServerSide = new BooleanSetting("DisableServerSide", false);
        addSetting(enableServerSide);
        super.init();
    }

}