package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HitBoxes2 extends Module {
    private final List<EntityLivingBase> validated = new ArrayList<>();

    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;
    NumberSetting fov;
    private static MovingObjectPosition mv;
    NumberSetting rangeSetting;
    BooleanSetting hurttime;

    ModeSetting sortmode;
    static NumberSetting size;
    public HitBoxes2() {
        super("HitBoxes2",0, Category.COMBAT);
    }

    @Override
    public void init(){
        super.init();
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","Distance"});
        size = new NumberSetting("HitBox", 0.08 , 0, 1,0.01F);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.rangeSetting = new NumberSetting("Range", 5.0, 1, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
        hurttime = new BooleanSetting("HurtTime", true);
        addSetting(hurttime,rangeSetting,size, sortmode, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, fov);

    }
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            setTag(sortmode.getMode());
            Entity entity = findTarget();
            if(entity != mc.thePlayer && entity != null){
                float width = entity.width;
                float height = entity.height;
                float expandValue = (float) size.getValue();
               }
        }
    }

    public static double exp(Entity entity) {
        return size.getValue();
    }
    public void gmo(float partialTicks) {
        if (mc.getRenderViewEntity() != null && mc.theWorld != null) {
            mc.pointedEntity = null;
            Entity pE = null;
            double d0 = 3.0D;
            mv = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
            double d2 = d0;
            Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);
            if (mv != null) {
                d2 = mv.hitVec.distanceTo(vec3);
            }

            Vec3 vec4 = mc.getRenderViewEntity().getLook(partialTicks);
            Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            Vec3 vec6 = null;
            float f1 = 1.0F;
            double d3 = d2;
            if(!sortmode.getMode().equals("MaxTarget")) {
                Entity entity = findTarget();
                if (entity != null) {
                    if (entity.canBeCollidedWith()) {
                        float ex = (float) ((double) entity.getCollisionBorderSize() * exp(entity));
                        AxisAlignedBB ax = entity.getEntityBoundingBox().expand(ex, ex, ex);
                        MovingObjectPosition mop = ax.calculateIntercept(vec3, vec5);
                        if (ax.isVecInside(vec3)) {
                            if (0.0D < d3 || d3 == 0.0D) {
                                pE = entity;
                                vec6 = mop == null ? vec3 : mop.hitVec;
                                d3 = 0.0D;
                            }
                        } else if (mop != null) {
                            double d4 = vec3.distanceTo(mop.hitVec);
                            if (d4 < d3 || d3 == 0.0D) {
                                if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                                    if (d3 == 0.0D) {
                                        pE = entity;
                                        vec6 = mop.hitVec;
                                    }
                                } else {
                                    pE = entity;
                                    vec6 = mop.hitVec;
                                    d3 = d4;
                                }
                            }
                        }
                    }
                }
            }
            if (pE != null && (d3 < d2 || mv == null)) {
                mv = new MovingObjectPosition(pE, vec6);
                mc.pointedEntity = pE;
                mc.objectMouseOver.entityHit = pE;
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
