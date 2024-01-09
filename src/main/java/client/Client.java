package client;

import client.command.CommandManager;
import client.event.listeners.EventChat;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.ModuleManager;
import client.mixin.client.MixinGuiChest;
import client.ui.HUD;
import client.ui.HUD2;
import client.ui.gui.clickGUI.GuiClickGUI;
import client.ui.theme.ThemeManager;
import client.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import scala.tools.nsc.doc.model.Public;

import static client.Client.MOD_ID;
import static client.Client.NAME;

@Mod(modid = MOD_ID, name = NAME, version = client.Client.VERSION)
public class Client
{
    public static final String MOD_ID = "moleculious";
    public static final String NAME = "Moleculious";
    public static final String VERSION = "0.1 Beta";
	public static HUD2 hud2 = new HUD2();

	public static HUD hud = new HUD();

	public static ThemeManager themeManager = new ThemeManager();
	public static CommandManager commandManager = new CommandManager();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static ResourceLocation background = new ResourceLocation("client/background.png");

    public static void init()
    {
	//	MinecraftForge.EVENT_BUS.register(this);
		commandManager.init();
		ModuleManager.registerModules();

		ModuleManager.loadModuleSetting();
		GuiClickGUI.loadModules();
	}

	public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof S03PacketTimeUpdate) {
				WorldUtils.onTime((S03PacketTimeUpdate) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
	}

	/*@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		if (mc.currentScreen == null) {
			try {
				if (Keyboard.isCreated()) {
					if (Keyboard.getEventKeyState()) {
						int i = Keyboard.getEventKey();
						if (i != 0) {
							ModuleManager.modules.stream().forEach(m -> {
								if(m.getKeyCode() == i) m.toggle();
							});
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
*/
	@SubscribeEvent
	public void chatEvent(ClientChatReceivedEvent event) {
		String message = String.valueOf(event.message);

		if (commandManager.handleCommand(message)) {
			event.setCanceled(true);
		}
		onEvent(new EventChat(message));
	}
}
