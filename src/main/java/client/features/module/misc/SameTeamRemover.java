package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class SameTeamRemover extends Module{
    public SameTeamRemover() {
        super("SameTeamRemover", Keyboard.KEY_NONE, Module.Category.MISC);
    }


    public void onEvent(Event<?> event){
        if(event instanceof EventUpdate) {
        for(Entity entity: mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer) {
                if (entity == mc.thePlayer)
                    return;
                if (ServerHelper.isTeammate((EntityLivingBase) entity)) {
                    mc.theWorld.removeEntity(entity);
                }
            }
        }
        }
    }
}
