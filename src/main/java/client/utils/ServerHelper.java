package client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerHelper {
    private static Map<String, UUID> uuidCache = new HashMap<>();
    private String name;

    private static Map<UUID, String> nameCache = new HashMap<>();
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    public static Color getTeamColor(EntityLivingBase entityIn) {
        Color color = Color.WHITE;
        ScorePlayerTeam scorethePlayerteam = (ScorePlayerTeam) entityIn.getTeam();
        if (scorethePlayerteam != null) {
            String s = FontRenderer.getFormatFromString(scorethePlayerteam.getColorPrefix());
            if (!Strings.isNullOrEmpty(s) && s.length() >= 2) {
                color = new Color(Minecraft.getMinecraft().fontRendererObj.getColorCode(s.charAt(1)));
            }
        }
        return color;
    }

    public static String getName(UUID uuid) {
        if (nameCache.containsKey(uuid))
            return nameCache.get(uuid);
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", new Object[] { UUIDTypeAdapter.fromUUID(uuid) }))).openConnection();
            httpURLConnection.setReadTimeout(5000);
            ServerHelper[] array = (ServerHelper[])gson.fromJson(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())), ServerHelper[].class);
            ServerHelper uuidFetcher = array[array.length - 1];
            uuidCache.put(uuidFetcher.name.toLowerCase(), uuid);
            nameCache.put(uuid, uuidFetcher.name);
            return uuidFetcher.name;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isTeammate(EntityLivingBase thePlayer) {
        Minecraft mc = Minecraft.getMinecraft();
        boolean result = false;


           if (mc.thePlayer.getTeam() != null) {
                result = mc.thePlayer.isOnSameTeam((EntityLivingBase) thePlayer);
            } else if (mc.thePlayer.inventoryContainer.getSlot(3).inventory instanceof ItemBlock) {
                result = ItemStack.areItemStacksEqual(mc.thePlayer.inventory.getStackInSlot(3), mc.thePlayer.inventory.getStackInSlot(3));
            }

        return result;
    }

   // public static boolean isFriend(EntitythePlayer thePlayer) {
     //   return FriendRegistry.getFriends().stream().anyMatch(ign -> ign.equals(thePlayer.getName()));
    //}
}
