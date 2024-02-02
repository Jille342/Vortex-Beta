package client.features.module.combat;


import client.event.Event;
import client.event.listeners.EventClick;
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
            if(mc.theWorld == null  && mc.thePlayer == null) {
                return;
            }
            call();
        }
        if(e instanceof EventClick) {
            if(mc.theWorld == null  && mc.thePlayer == null) {
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
        Object[] o = getEntity(r, 0,0);
        if (o == null) {
        } else {
            Entity en = (Entity)o[0];
            mc.objectMouseOver = new MovingObjectPosition(en, (Vec3)o[1]);
            mc.pointedEntity = en;
        }
    }
    /*     */
    /*     */
    /*     */   public static Object[] getEntity(double distance, double expand, float partialTicks) {
        /*  30 */     Entity var2 = mc.getRenderViewEntity();
        /*  31 */     Entity entity = null;
        /*  32 */     if (var2 != null && mc.theWorld != null) {
            /*     */
            /*  34 */       mc.mcProfiler.startSection("pick");
            /*  35 */       double var3 = distance;
            /*  36 */       double var5 = var3;
            /*  37 */       Vec3 var7 = var2.getPositionEyes(0.0F);
            /*     */
            /*  39 */       Vec3 var8 = var2.getLook(0.0F);
            /*  40 */       Vec3 var9 = var7.addVector(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3);
            /*  41 */       Vec3 var10 = null;
            /*  42 */       float var11 = 1.0F;
            /*  43 */       List<Entity> var12 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox().addCoord(var8.xCoord * var3, var8.yCoord * var3, var8.zCoord * var3).expand(var11, var11, var11));
            /*  44 */       double var13 = var5;
            /*  45 */       for (int var15 = 0; var15 < var12.size(); var15++) {
                /*     */
                /*  47 */         Entity var16 = var12.get(var15);
                /*  48 */         if (var16.canBeCollidedWith()) {
                    /*     */
                    /*  50 */           float var17 = var16.getCollisionBorderSize();
                    /*     */
                    /*  52 */           AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17, var17);
                    /*     */
                    /*  54 */           var18 = var18.expand(expand, expand, expand);
                    /*     */
                    /*  56 */           MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
                    /*     */
                    /*  58 */           if (var18.isVecInside(var7)) {
                        /*     */
                        /*  60 */             if (0.0D < var13 || var13 == 0.0D)
                            /*     */             {
                            /*  62 */               entity = var16;
                            /*  63 */               var10 = (var19 == null) ? var7 : var19.hitVec;
                            /*  64 */               var13 = 0.0D;
                            /*     */             }
                        /*     */
                        /*  67 */           } else if (var19 != null) {
                        /*     */
                        /*  69 */             double var20 = var7.distanceTo(var19.hitVec);
                        /*  70 */             if (var20 < var13 || var13 == 0.0D) {
                            /*     */
                            /*  72 */
                            /*  76 */               if (var16 == var2.ridingEntity && !var2.canRiderInteract()) {
                                /*     */
                                /*  78 */                 if (var13 == 0.0D)
                                    /*     */                 {
                                    /*  80 */                   entity = var16;
                                    /*  81 */                   var10 = var19.hitVec;
                                    /*     */                 }
                                /*     */
                                /*     */               } else {
                                /*     */
                                /*  86 */                 entity = var16;
                                /*  87 */                 var10 = var19.hitVec;
                                /*  88 */                 var13 = var20;
                                /*     */               }
                            /*     */             }
                        /*     */           }
                    /*     */         }
                /*     */       }
            /*  94 */       if (var13 < var5 &&
                    /*  95 */         !(entity instanceof net.minecraft.entity.EntityLivingBase) && !(entity instanceof net.minecraft.entity.item.EntityItemFrame)) {
                /*  96 */         entity = null;
                /*     */       }
            /*     */
            /*  99 */       mc.mcProfiler.endSection();
            /*     */
            /* 101 */       if (entity == null || var10 == null) {
                /* 102 */         return null;
                /*     */       }
            /* 104 */       return new Object[] { entity, var10 };
            /*     */     }
        /*     */
        /* 107 */     return null;
        /*     */   }


}
