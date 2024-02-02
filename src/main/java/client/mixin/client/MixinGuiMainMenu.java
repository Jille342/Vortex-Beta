package client.mixin.client;

import client.Client;
import client.ui.gui.login.GuiAltLogin;
import client.utils.RenderingUtils;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.GuiModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.FileNotFoundException;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {
    @Shadow private String splashText;
    private final CFontRenderer font = Fonts.defaultTitle;
    private final CFontRenderer font2 = Fonts.default18;



    @Inject(method = "actionPerformed", at = @At("HEAD"),cancellable = true)
    private void actionPerformed(GuiButton button, CallbackInfo ci) {
        ci.cancel();
        if (button.id == 500)
        {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }
            if (button.id == 0)
                this.mc.displayGuiScreen((GuiScreen)new GuiOptions(this, this.mc.gameSettings));
            if (button.id == 5)
                this.mc.displayGuiScreen((GuiScreen)new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
            if (button.id == 1)
                this.mc.displayGuiScreen((GuiScreen)new GuiSelectWorld(this));
            if (button.id == 2)
                this.mc.displayGuiScreen((GuiScreen)new GuiMultiplayer(this));
            if (button.id == 4)
                this.mc.shutdown();
        if (button.id == 6) {
            this.mc.displayGuiScreen(new GuiModList(this));
        }
    }
    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    public void initGui(CallbackInfo ci){
        int var3 = this.height / 4 + 48;
        addButtons(var3, 24);
    }


  /**
   * @author Jill
   * @reason test
   */
  @Overwrite
  private void drawPanorama(int p_drawPanorama_1_, int p_drawPanorama_2_, float p_drawPanorama_3_) throws FileNotFoundException {

  }
  /**
   * @author aa
   * @reason aa
   */
  @Overwrite
  private void renderSkybox(int p_renderSkybox_1_, int p_renderSkybox_2_, float p_renderSkybox_3_) {

  }
  /**
   * @author Jill
   * @reason Jill
   */
  @Overwrite
  private void rotateAndBlurSkybox(float p_rotateAndBlurSkybox_1_) {
  }
/**
 * @author Jill
 * @reason Jill
 */
@Overwrite
public void updateScreen(){

}

    @Inject(method = "drawScreen",at = @At( "HEAD"), cancellable = true)
    private void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_,CallbackInfo ci) {
    ci.cancel();
    GuiMainMenu guiMainMenu = new GuiMainMenu();
        String name = client.Client.NAME;
        name = name.substring(0, 1).replaceAll(name.substring(0, 1),  name.substring(0, 1)) + name.substring(1).replaceAll(name.substring(1), "\247f" + name.substring(1));
        renderBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        font.drawCenteredString(name, this.width / 2, this.height / 4, new Color(50, 200, 255).getRGB());
       font2.drawString(name+ " v." + Client.VERSION, 2, this.height - font2.getHeight() * 2 - 1, -1);
        font2.drawString("Hello Jill Users", 2, this.height - font2.getHeight()- 1, new Color(50, 200, 255).getRGB());
        ForgeHooksClient.renderMainMenu(guiMainMenu , this.fontRendererObj, this.width, this.height);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
  }
    private void addButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, p_73969_1_ + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, p_73969_1_ + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
    }
    private static void renderBackground() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Client.background);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, scaledresolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(scaledresolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
