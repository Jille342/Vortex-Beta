//Deobfuscated By Mouath#2221 | ????#2221 D:\Game\private 2\False"!

package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventClick;
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

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.*;

public class HitBoxesTest extends Module
{
    private MovingObjectPosition moving;
    private Entity pointedEntity;
    private NumberSetting expand;
    public static float hitBoxMultiplier;
    static BooleanSetting targetMonstersSetting;
    static BooleanSetting targetAnimalsSetting;
    static BooleanSetting ignoreTeamsSetting;
    static NumberSetting fov;
    private static MovingObjectPosition mv;
    static NumberSetting rangeSetting;

    static ModeSetting sortmode;
    static NumberSetting size;
    static  NumberSetting maxTarget;
    static EntityLivingBase target;
    static NumberSetting hurttime;
    Entity target2;
    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private static final List<EntityLivingBase> validated = new ArrayList<>();


    public HitBoxesTest() {
        super("HitBoxesTest", 0, Category.COMBAT);
    }

    public void init(){
        super.init();
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","Distance"});
        size = new NumberSetting("Size ", 0.08 , 0, 1,0.01F);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.rangeSetting = new NumberSetting("Range", 5.0, 1, 8.0, 0.1);
        this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
     hurttime = new NumberSetting("Hurt Time", 5 ,0, 10 ,1);
        addSetting(rangeSetting,size, sortmode, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, fov,maxTarget, hurttime);

    }



    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)  {
            setTag(sortmode.getMode());
            target = findTarget();
            target2 = findTarget2();
        }

        if(e instanceof EventClick) {
            Object[] objects = getEntity(3.0D, this.size.getValue());
            if(target2 != null) {
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target2, C02PacketUseEntity.Action.ATTACK));
                mc.thePlayer.swingItem();
            }
              if (objects == null) {
               return;
              }
           this.mc.objectMouseOver = new MovingObjectPosition((Entity)objects[0], (Vec3)objects[1]);
               this.mc.pointedEntity = (Entity)objects[0];


        }


    }
    private EntityLivingBase findTarget2() {
        targets.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }

                if (!RotationUtils.fov(entity, 20))
                    continue;
                if(!mc.thePlayer.canEntityBeSeen(entity))
                    continue;
                double focusRange = rangeSetting.value ;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }
                    if(AntiBot.isBot((EntityPlayer) entity))
                        continue;

                    targets.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityAnimal && targetAnimalsSetting.enable) {
                    targets.add((EntityLivingBase) entity);
                } else if (entity instanceof EntityMob && targetMonstersSetting.enable) {
                    targets.add((EntityLivingBase) entity);
                }
            }
        }

        if (targets.isEmpty()) return null;
        switch(sortmode.getMode()) {
            case "Distance":
                this.targets.sort(Comparator.comparingDouble((entity) -> (double)mc.thePlayer.getDistanceToEntity((Entity) entity)));
                break;
            case"Angle":
                targets.sort(Comparator.comparingDouble(RotationUtils::calculateYawChangeToDst));
        }
        this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
        return (EntityLivingBase) targets.get(0);
    }
    private static EntityLivingBase findTarget() {
        validated.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {

            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                if(((EntityLivingBase) entity).hurtTime > hurttime.getValue())
                    continue;
if(!mc.thePlayer.canEntityBeSeen(entity))
    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }
                    if(AntiBot.isBot((EntityPlayer) entity))
                        continue;
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
        validated.sort(Comparator.comparingInt(o -> o.hurtTime));

        return validated.get(0);
    }


    public static Object[] getEntity(double distance, double expand) {
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
            /*  44 */       double var13 = var5;
            /*  45 */      if(target != null) {
                /*     */
                /*  47 */         Entity var16 = target;
                /*     */
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
                            /*  73 */
                            /*  76 */               if (var16 == var2.ridingEntity && !var16.canRiderInteract()) {
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
