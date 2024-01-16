package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class LegitAura extends Module {

    NumberSetting CPS;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;

    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
    NumberSetting fov;
    BooleanSetting hitThroughWalls;
    BooleanSetting clickOnly;
    BooleanSetting notAimingOnly;
    public LegitAura() {
        super("LegitAura", 0,	Category.COMBAT);
    }

    @Override
    public void init() {
        this.rangeSetting = new NumberSetting("Range", 3.0, 0, 4.2, 0.1);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", false);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.CPS = new NumberSetting("CPS", 10, 0, 20, 1f);
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Distance", "Angle"});
        this.fov = new NumberSetting("FOV", 20D, 0D, 30D, 1.0D);
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
        clickOnly = new BooleanSetting("Click Only", true);
        notAimingOnly = new BooleanSetting("Not Aiming Only", true);
        addSetting(CPS, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, sortmode, targetInvisibles,fov,hitThroughWalls,rangeSetting,clickOnly, notAimingOnly);
        super.init();
    }

    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private final TimeHelper attackTimer = new TimeHelper();

    @Override
    public void onEvent(Event<?> e) {

        if (e instanceof EventUpdate) {
            Entity target = findTarget();

            if(target != null){
                float diff = RotationUtils.calculateYawChangeToDst(target);
                if (!mc.thePlayer.isUsingItem() && !(mc.currentScreen instanceof GuiInventory)) {

                    setTag(sortmode.getMode() + " " + targets.size());
                    if (e.isPre()) {


                        if (!targets.isEmpty()) {
                            if (attackTimer.hasReached(calculateTime((int) CPS.value)) && !target.isDead && target.isEntityAlive()) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                                mc.thePlayer.swingItem();
                                attackTimer.reset();
                            }

                            if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10)
                                targets.remove(target);
                        }
                    }
                    if (e instanceof EventMotion) {
                        EventMotion event = (EventMotion) e;
                        if (!targets.isEmpty()) {
                            if (!(diff < -6 || diff > 6) && notAimingOnly.enable)
                                return;

                            if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10 && target == null)
                                return;
                            float[] angles = RotationUtils.getRotationsEntity((EntityLivingBase) target);
                            event.setYaw(angles[0]);
                            event.setPitch(angles[1]);

                        }
                    }
                    super.onEvent(e);
                }
            }
        }

    }
    private EntityLivingBase findTarget() {
        targets.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
                if (entity.isDead || !entity.isEntityAlive() || entity.ticksExisted < 10) {
                    continue;
                }
                if(clickOnly.enable && !mc.gameSettings.keyBindAttack.isKeyDown())
                    continue;
                if (entity.isInvisible() && !targetInvisibles.enable)
                    continue;

                if (!RotationUtils.fov(entity, fov.value))
                    continue;
                if(!mc.thePlayer.canEntityBeSeen(entity)&& !hitThroughWalls.isEnable())
                    continue;
                double focusRange = rangeSetting.value ;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {

                    if (ignoreTeamsSetting.enable && ServerHelper.isTeammate((EntityPlayer) entity)) {
                        continue;
                    }

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
                targets.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
        }
        this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
        return (EntityLivingBase) targets.get(0);
    }

    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }

    @Override
    public void onEnable() {
        targets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        super.onDisable();
    }
}
