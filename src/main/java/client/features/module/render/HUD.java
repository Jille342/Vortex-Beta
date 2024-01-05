package client.features.module.render;

import client.Client;
import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;

import java.util.ArrayList;

public class HUD extends Module {
    public static BooleanSetting background;
    public static BooleanSetting info;
    public static BooleanSetting OUTLINE;
    public static BooleanSetting inversion;
    public static ModeSetting colormode;
    public static ModeSetting namecolormode;

    public static double lastPosX = Double.NaN;
    public static double lastPosZ = Double.NaN;
      public static ArrayList<Double> distances = new ArrayList<Double>();

    public HUD() {
        super("HUD", 0, Category.RENDER);

    }

    @Override
    public void init() {
        super.init();
        colormode = new ModeSetting("Color Mode ", "Pulsing", new String[]{"Default", "Rainbow", "Pulsing", "Category", "Test"});
        namecolormode = new ModeSetting("Name Color Mode ", "Default", new String[]{"Default", "Rainbow", "Pulsing", "Category", "Test"});
        background = new BooleanSetting("BackGround", true);
        info = new BooleanSetting("Info", true);
        OUTLINE = new BooleanSetting("Outline", true);
        inversion = new BooleanSetting("Outline", true);
        addSetting(info, OUTLINE, background, colormode, inversion, namecolormode);
    }

    @Override
    public void onEnable() {
        Client.hud2.draw();;
        super.onEnable();
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventRender2D){
            Client.hud2.draw();
        }
        if (e instanceof EventUpdate) {
            if (!Double.isNaN(lastPosX) && !Double.isNaN(lastPosZ)) {
                double differenceX = Math.abs(lastPosX - mc.thePlayer.posX);
                double differenceZ = Math.abs(lastPosZ - mc.thePlayer.posZ);
                double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ) * 2;

                distances.add(distance);
                if (distances.size() > 20)
                    distances.remove(0);
            }

            lastPosX = mc.thePlayer.posX;
            lastPosZ = mc.thePlayer.posZ;

        }
    }
}