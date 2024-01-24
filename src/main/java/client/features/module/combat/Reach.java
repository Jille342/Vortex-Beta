package client.features.module.combat;


import client.event.Event;
import client.event.listeners.EventTick;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class Reach  extends Module {

    static NumberSetting extendedReach;
   static BooleanSetting hitThroughWalls;

        public Reach() {
        super("Reach", 0, Category.COMBAT);


    }
    @Override
    public void init(){
        super.init();
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
extendedReach = new NumberSetting("Extended Reach", 3,0, 4.0 ,0.1);
    addSetting(extendedReach, hitThroughWalls);

    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventTick) {
            if(mc.theWorld != null  && mc.thePlayer != null) {
                return;
            }
            call();
        }
    }
    public static void call() {
        if (!hitThroughWalls.enable && mc.objectMouseOver != null) {
            if (mc.objectMouseOver != null) {
                BlockPos p = mc.objectMouseOver.getBlockPos();
                if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
                    return;
                }
            }
        }

        double r = extendedReach.getValue();
        Object[] o = zz(r, 0.0D);
        if (o == null) {
        } else {
            Entity en = (Entity)o[0];
            mc.objectMouseOver = new MovingObjectPosition(en, (Vec3)o[1]);
            mc.pointedEntity = en;
        }
    }
    private static Object[] zz(double zzD, double zzE) {
        if (!ModuleManager.getModulebyClass(Reach.class).enable) {
            zzD = mc.playerController.extendedReach() ? 6.0D : 3.0D;
        }

        Entity entity1 = mc.getRenderViewEntity();
        Entity entity = null;
        if (entity1 == null) {
            return null;
        } else {
            mc.mcProfiler.startSection("pick");
            Vec3 eyes_positions = entity1.getPositionEyes(1.0F);
            Vec3 look = entity1.getLook(1.0F);
            Vec3 new_eyes_pos = eyes_positions.addVector(look.xCoord * zzD, look.yCoord * zzD, look.zCoord * zzD);
            Vec3 zz6 = null;
            List<Entity> zz8 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(entity1, entity1.getEntityBoundingBox().addCoord(look.xCoord * zzD, look.yCoord * zzD, look.zCoord * zzD).expand(1.0D, 1.0D, 1.0D));
            double zz9 = zzD;

            for (Entity o : zz8) {
                if (o.canBeCollidedWith()) {
                    float ex = (float) ((double) o.getCollisionBorderSize() * HitBoxes.size.getValue());
                    AxisAlignedBB zz13 = o.getEntityBoundingBox().expand(ex, ex, ex);
                    zz13 = zz13.expand(zzE, zzE, zzE);
                    MovingObjectPosition zz14 = zz13.calculateIntercept(eyes_positions, new_eyes_pos);
                    if (zz13.isVecInside(eyes_positions)) {
                        if (0.0D < zz9 || zz9 == 0.0D) {
                            entity = o;
                            zz6 = zz14 == null ? eyes_positions : zz14.hitVec;
                            zz9 = 0.0D;
                        }
                    } else if (zz14 != null) {
                        double zz15 = eyes_positions.distanceTo(zz14.hitVec);
                        if (zz15 < zz9 || zz9 == 0.0D) {
                            if (o == entity1.ridingEntity) {
                                if (zz9 == 0.0D) {
                                    entity = o;
                                    zz6 = zz14.hitVec;
                                }
                            } else {
                                entity = o;
                                zz6 = zz14.hitVec;
                                zz9 = zz15;
                            }
                        }
                    }
                }
            }

            if (zz9 < zzD && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
                entity = null;
            }

            mc.mcProfiler.endSection();
            if (entity != null && zz6 != null) {
                return new Object[]{entity, zz6};
            } else {
                return null;
            }
        }
    }


}
