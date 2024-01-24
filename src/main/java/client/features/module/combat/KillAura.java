package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RandomUtils;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import net.minecraft.client.entity.EntityPlayerSP;
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

public class KillAura extends Module {

    NumberSetting CPS;
    BooleanSetting targetMonstersSetting;
    BooleanSetting targetAnimalsSetting;
    BooleanSetting ignoreTeamsSetting;

    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
    ModeSetting rotationmode;
    Entity lastTarget;
    NumberSetting minrotationspeed;
    NumberSetting maxrotationspeed;
    public KillAura() {
        super("KillAura", 0,	Category.COMBAT);
    }

    @Override
    public void init() {
        this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
        this.targetMonstersSetting = new BooleanSetting("Target Monsters", true);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", true);
        this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        rotationmode = new ModeSetting("Rotation Mode", "Normal", new String[]{"Normal", "RotationSpeed"});
        minrotationspeed = new NumberSetting("Min Rotation Speed", 50.0D, 1.0D, 180.0D, 1.0D);
        maxrotationspeed = new NumberSetting("Max Rotation Speed", 60.0D, 1.0D, 180.0D, 1.0D);
        this.CPS = new NumberSetting("CPS", 10, 0, 20, 1f);
        sortmode = new ModeSetting("SortMode", "Distance", new String[]{"Distance", "Angle", "HurtTime", "Armor"});
        addSetting(CPS, targetAnimalsSetting, targetMonstersSetting, ignoreTeamsSetting, sortmode, targetInvisibles,rangeSetting,rotationmode, minrotationspeed,maxrotationspeed);
        super.init();
    }

    ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    private final TimeHelper attackTimer = new TimeHelper();

    @Override
    public void onEvent(Event<?> e) {

        if (e instanceof EventUpdate) {
            setTag(sortmode.getMode() + " " + targets.size());
            if (e.isPre()) {

                Entity target = findTarget();
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

            super.onEvent(e);
        }
        if (e instanceof EventMotion) {
            Entity target = findTarget();
            EventMotion event = (EventMotion) e;
            if (!targets.isEmpty()   && target != null) {
                if (target.isDead || !target.isEntityAlive() || target.ticksExisted < 10 && target ==null)
                    return;
                if(rotationmode.getMode().equals("RotationSpeed")) {
                    float[] neededRotations = RotationUtils.getRotationsAAC((EntityLivingBase) target);
                    float[] limited = RotationUtils.limitAngleChange(RotationUtils.serverRotations, neededRotations, RandomUtils.nextFloat((float) minrotationspeed.getValue(), (float) maxrotationspeed.getValue()));
                    if (lastTarget != target) {
                        limited[0] = limited[0] + RandomUtils.nextFloat(-7.0F, 7.0F);
                        lastTarget = target;
                    }
                    RotationUtils.fixedSensitivity(limited, 0.1F);
                    event.yaw = limited[0];
                    event.pitch = limited[1];
                }

                float[] angles = RotationUtils.getRotationsEntity((EntityLivingBase) target);
                event.setYaw(angles[0]);
                event.setPitch(angles[1]);

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
                if (entity.isInvisible() && !targetInvisibles.enable)
                    continue;
                double focusRange = mc.thePlayer.canEntityBeSeen(entity) ? rangeSetting.value : 3.5;
                if (mc.thePlayer.getDistanceToEntity(entity) > focusRange) continue;
                if (entity instanceof EntityPlayer) {


                    if(AntiBot.isBot((EntityPlayer) entity))
                        continue;
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
                this.targets.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
                break;
            case"HurtTime":
                this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
break;
            case"Armor":
                this.targets.sort(Comparator.comparingInt(o -> o.getTotalArmorValue()));
break;
        }
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
