package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.Field;

public class AntiForgeBypass extends Module {
    public AntiForgeBypass() {
        super("AntiForgeBypass",0, Module.Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket) {
            if (!mc.isSingleplayer()) {
                EventPacket event = ((EventPacket) e);
                if (event.isOutgoing()) {
                    Packet<?> p = event.getPacket();
                    if (p instanceof FMLProxyPacket) {
                        event.setCancelled(true);
                    } else if (p instanceof C17PacketCustomPayload) {
                        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
                        C17PacketCustomPayload payloadPacket = (C17PacketCustomPayload) event.getPacket();
                        if (!payloadPacket.getChannelName().equals("MC|Brand")) {
                            e.cancel();
                        } else if(payloadPacket.getChannelName().equalsIgnoreCase("MC|Brand")) {
                            try {
                                Field field = payloadPacket.getClass().getDeclaredField("data");
                                field.setAccessible(true);
                                field.set(packetBuffer, null);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        }
                    }
                }
            }
        }
    }

