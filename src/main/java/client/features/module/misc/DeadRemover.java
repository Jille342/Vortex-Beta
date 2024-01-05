package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class DeadRemover extends Module{
    public DeadRemover() {
        super("DeadRemover", Keyboard.KEY_NONE, Module.Category.MISC);
    }


    public void onEvent(Event<?> event){
        if(event instanceof EventUpdate) {
            for(Entity entity: mc.theWorld.getLoadedEntityList()) {
                if(entity instanceof EntityPlayer) {
                    if (entity == mc.thePlayer)
                        return;
                  if(((EntityPlayer) entity).getHealth() == 0)
                      mc.theWorld.removeEntity(entity);
                }

                if(entity instanceof EntityMob) {
                    if(((EntityMob) entity).getHealth() == 0)
                        mc.theWorld.removeEntity(entity);
                }
            }

        }
    }
}
