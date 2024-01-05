package client.features.module.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.BlockPos;

public class AdminChecker extends Module {
    private int lastAdmins;

    private final ArrayList<String> admins;

    private final TimeHelper timer;
    private final CFontRenderer font = Fonts.default18;
    private final TimeHelper timer2 = new TimeHelper();
    NumberSetting delay;
    private String adminname;

    public AdminChecker() {
        super("AdminChecker",  0, Category.MISC);
        this.admins = new ArrayList<>();
        this.timer = new TimeHelper();
    }

    public void init() {
        this.delay = new NumberSetting("Chat Delay", 1000, 1000, 5000, 1000F);
        addSetting(delay); super.init();

    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if (!this.admins.isEmpty()) {
                font.drawStringWithShadow("" + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth("" + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            } else {
                font.drawStringWithShadow("Admins: " + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth("Admins: " + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            }
        }
        if (e instanceof EventUpdate) {
            if (this.timer.hasReached(5000.0F)) {
                this.timer.reset();
                mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/vanishnopacket:vanish "));
                mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/rank "));
            }
            setTag(String.valueOf(admins.size()));
            if (!this.admins.isEmpty()) {
                displayAdmins();
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

    public void displayAdmins() {

            if (timer2.hasReached(delay.value)) {
                ChatUtils.printChat(String.valueOf("Admin INC " + admins + " " + admins.size()));
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
