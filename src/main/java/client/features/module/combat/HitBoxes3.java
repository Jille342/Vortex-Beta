//Deobfuscated By Mouath#2221 | ????#2221 D:\Game\private 2\False"!

package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventEntityHitbox;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.*;

import java.util.*;
import net.minecraft.util.*;

public class HitBoxes3 extends Module
{
    private MovingObjectPosition moving;
    private Entity pointedEntity;
    private NumberSetting expand;
    public static float hitBoxMultiplier;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;
    NumberSetting fov;
    private static MovingObjectPosition mv;
    NumberSetting rangeSetting;

    ModeSetting sortmode;
    static NumberSetting size;
    private final List<EntityLivingBase> validated = new ArrayList<>();


    public HitBoxes3() {
        super("HitBoxes3", 0, Category.COMBAT);
    }

    public void init(){
        super.init();
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","Distance"});
        size = new NumberSetting("HitBox", 0.08 , 0, 1,0.01F);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.rangeSetting = new NumberSetting("Range", 5.0, 1, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
        addSetting(rangeSetting,size, sortmode, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, fov);

    }



    @Override
    public void onDisable() {
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)  {
            setTag(sortmode.getMode());
           Entity entity = findTarget();
           if(entity != null)  {
               float width = entity.width;
               float height = entity.height;
               float expandValue = (float) size.getValue();
               entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - width - expandValue, entity.posY, entity.posZ + width + expandValue, entity.posX + width + expandValue, entity.posY + height + expandValue, entity.posZ - width - expandValue));
           }
           }

        }


    private EntityLivingBase findTarget() {
        validated.clear();
        float f1 = 1.0F;
        double d0 = 3.0D;
        Vec3 vec4 = mc.getRenderViewEntity().getLook(1);
        for (Entity entity : mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f1, f1, f1))) {

            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }

                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }
                    if(  ((EntityPlayer) entity).getHealth() ==0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    if( ((EntityAnimal) entity).getHealth() ==0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    if(((EntityMob) entity).getHealth() == 0)
                        continue;
                    validated.add((EntityLivingBase) entity);
                }
            }
        }

        if (validated.isEmpty()) return null;
        switch (sortmode.getMode()) {
            case "Angle":
                validated.sort(Comparator.comparingDouble(RotationUtils::getYawChangeToEntity));
                break;
            case "Distance":
                validated.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
                break;
        }
        this.validated.sort(Comparator.comparingInt(o -> o.hurtTime));

        return validated.get(0);
    }

}
