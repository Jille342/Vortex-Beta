package client.features.module.misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.ui.notifications.Notification;
import client.ui.notifications.NotificationPublisher;
import client.ui.notifications.NotificationType;
import client.utils.ChatUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3APacketTabComplete;

public class AdminChecker extends Module {
    private int lastAdmins;

    private final ArrayList<String> admins;

    private final TimeHelper timer;
    private final CFontRenderer font = Fonts.elliot18;

    private final TimeHelper timer2 = new TimeHelper();
    int color = -1;
    NumberSetting delay;
    ModeSetting noticeMode;
    ModeSetting checkMode;
    NumberSetting scaling;
    BooleanSetting sound;
    NumberSetting soundTime;
    private final TimeHelper timer3 = new TimeHelper();
    int i = 0;
    private String adminname;

    public AdminChecker() {
        super("AdminChecker",  0, Category.MISC);
        this.admins = new ArrayList<>();
        this.timer = new TimeHelper();
    }

    public void init() {
        this.delay = new NumberSetting("Chat Delay", 1000, 1000, 5000, 1000F);
        this.checkMode = new ModeSetting("Check Mode ", "Rank", new String[]{"Rank", "Tell"});
        this.noticeMode = new ModeSetting("NoticeMode", "Display", new String[]{"Chat", "Display"});
        this.sound = new BooleanSetting("Sound", true);
        this.soundTime = new NumberSetting("Sound Time", 50,10,200,1);
        this.scaling = new NumberSetting("Size", 1.0F,1.0, 4.0, 2.0);;
        addSetting(delay, checkMode,noticeMode,scaling, sound,soundTime); super.init();

    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if(noticeMode.getMode().equals("Display")){
                double scale = (0.0018 + (double) this.scaling.getValue());
                if (!this.admins.isEmpty() ) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale,scale,scale);
                    color = TwoColoreffect(Color.RED,Color.WHITE,Math.abs(System.currentTimeMillis() / 2L) / 100.0 + 3.0F * (1 * 2.55) / 60).getRGB();
                    font.drawStringWithShadow("Admin INC " + admins + " " + admins.size(), (3 / scale), 30 / scale, color);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale,scale,scale);
                    color = Color.WHITE.getRGB();
                    font.drawStringWithShadow("No Admins" + admins + " " + admins.size(), (3 / scale), 30 / scale, color);
                    GlStateManager.popMatrix();
                }

            }
        }
        if (e instanceof EventUpdate) {
            if (this.timer.hasReached(5000.0F)) {
                this.timer.reset();
                if(checkMode.getMode().equals("Rank")) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/vanishnopacket:vanish "));
                    mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/rank "));
                } else if (checkMode.getMode().equals("Tell")) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/tell "));
                }

            }
            setTag(String.valueOf(admins.size()));
            if (!this.admins.isEmpty()) {
                if(sound.enable) {
                    i++;
                    if (i < soundTime.getValue()) {
                                mc.thePlayer.playSound("random.orb", 0.2F, 1.0F);

                    }
                }
                displayAdmins();
            } else{
                i = 0;
            }
        }
        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
            if (event.getPacket()instanceof S3APacketTabComplete) {
                S3APacketTabComplete packet = (S3APacketTabComplete) event.getPacket();
                this.admins.clear();
                String[] matches;
                for (int length = (matches = packet.func_149630_c()).length, i = 0; i < length; i++) {
                    String user = matches[i];
                    String[] administrators;
                    for (int length2 = (administrators = getAdministrators()).length, j = 0; j < length2; j++) {
                        String admin = administrators[j];
                        if (user.equalsIgnoreCase(admin)) {
                            adminname = user;
                            displayAdmins();
                            this.admins.add(user);
                        }
                    }
                }
                this.lastAdmins = this.admins.size();
            } else if (event.getPacket() instanceof S38PacketPlayerListItem) {
                S38PacketPlayerListItem packetPlayInPlayerListItem = (S38PacketPlayerListItem) event.getPacket();
                if (packetPlayInPlayerListItem.getAction() == S38PacketPlayerListItem.Action.UPDATE_LATENCY)
                    for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packetPlayInPlayerListItem.getEntries()) {
                        if (mc.getNetHandler().getPlayerInfo(addPlayerData.getProfile().getId()) == null) {
                            String name = getName(addPlayerData.getProfile().getId());
                            if (Objects.isNull(name)) {
                                checkList("NullPlayer");
                                continue;
                            }
                            if (Arrays.toString((Object[]) getAdministrators()).contains(name))
                                checkList(name);
                        }
                    }
            }
        }
    }

    public static Color TwoColoreffect(final Color color, final Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }

    public void displayAdmins() {
        if (timer2.hasReached(delay.value)) {
            if(noticeMode.getMode().equals("Chat")) {
                ChatUtils.printChat(String.valueOf("Admin INC " + admins + " " + admins.size()));
            }
            timer2.reset();
        }
    }

    public String[] getAdministrators() {
        return new String[] {
                "ACrispyTortilla",
                "ArcticStorm141",
                "ArsMagia",
                "Captainbenedict",
                "Carrots386",
                "DJ_Pedro",
                "DocCodeSharp",
                "FullAdmin",
                "Galap",
                "HighlifeTTU",
                "ImbC",
                "InstantLightning",
                "JTGangsterLP6",
                "Kevin_is_Panda",
                "Kingey",
                "Marine_PvP",
                "MissHilevi",
                "Mistri",
                "Mosh_Von_Void",
                "Navarr",
                "PokeTheEye",
                "Rafiki2085",
                "Robertthegoat",
                "Sevy13",
                "andrew323",
                "dLeMoNb",
                "lazertester",
                "noobfan",
                "skillerfox3",
                "storm345",
                "windex_07",
                "AlecJ",
                "JACOBSMILE",
                "Wayvernia",
                "gunso_",
                "Hughzaz",
                "Murgatron",
                "SaxaphoneWalrus",
                "_Ahri",
                "SakuraWolfVeghetto",
                "SnowVi1liers",
                "jiren74",
                "Dange",
                "Tatre",
                "Pichu2002",
                "LegendaryAlex",
                "LaukNLoad",
                "M4bi",
                "HellionX2",
                "Ktrompfl",
                "Bupin",
                "Murgatron",
                "Outra",
                "CoastinJosh",
                "sabau",
                "Axyy",
                "lPirlo",
                "ImAbbyy",
                "Roquel",
                "Rinjani",
                "Agypagy",
                "wmn",
                "halowars91",
                "InstantLightning",
                "JACOBSMILE",
                "BasicAly",
                "MrJack",
                "Xhat",
                "kbsfe",
                "Selictove",
                "sellejz",
                "OrcaHedral",
                "EnderMCx",
                "LangScott",
                "Pyachi2002",
                "ro_cks",
                "BayanNoodle",
        };
    }

    public ArrayList<String> getAdmins() {
        ArrayList<String> admins = new ArrayList<>();
        if (mc.getNetHandler().getPlayerInfoMap() != null)
            for (NetworkPlayerInfo player : mc.getNetHandler().getPlayerInfoMap()) {
                String text = player.getGameProfile().getName();
                admins.add(text);
            }
        return admins;
    }

    public String getName(UUID uuid) {
        return ServerHelper.getName(uuid);
    }

    private void checkList(String uuid) {
        if (this.admins.contains(uuid))
            return;
        this.admins.add(uuid);
    }
}
