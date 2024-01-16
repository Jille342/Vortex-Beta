package client.ui.clicckgui2;

import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.*;
import client.utils.font.CFontRenderer;
import client.utils.font.Fonts;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static client.utils.RenderingUtils.drawRect;
import static client.utils.font.Fonts.default18;

public class ClickGuiWindow  {

    private final CFontRenderer font = default18;

    private NumberSetting doubleSetting;
    public Module module;
    private static Color accentColor = new Color(0xff2C508A);
    private static final Color backColor = new Color(0xff373737);
    private static final Color outlineColor1 = new Color(0xff202020);
    private static final Color outlineColor2 = new Color(0xff313131);
    private static final int settingTextColor = 0xffd0d0d0;

    private float x, y, lastX, lastY;
    private boolean dragging = false, expand = true;

    private final Module.Category category;
    private final List<Module> modules;
    private final boolean[] mExpand;
    int keyCode;
    boolean pressed = false;
    KeyBindSetting keyBindSetting = null;
    private static boolean clicked = false;
    public ClickGuiWindow(float x, float y, Module.Category category) {
        this.x = x;
        this.y = y;
        this.category = category;
        modules = ModuleManager.modules
                .stream()
                .filter(m -> m.getCategory() == category).collect(Collectors.toList());
        mExpand = new boolean[modules.size()];
    }

    public void init() {
    }


    public void render(int mouseX, int mouseY, float delta) {
        if (doubleSetting != null) {
            doubleSetting.setValue(x, 120, mouseX);
        }

        if (dragging) {
            x = mouseX + lastX;
            y = mouseY + lastY;
        }
        drawRect( x - 2, y - 2, x + 122, y + 20, accentColor.getRGB());
        drawRect(x - 1, y - 1, x + 121, y + 19, outlineColor1.getRGB());
        drawRect( x, y, x + 120, y + 18,0xff262626);
        drawRect(  x - 1, y + 17, x + 121, y + 18,outlineColor1.getRGB());
        font.drawString( category.name(), x + 4, y + 4, -1);

        if (!expand) {
            return;
        }

        float currentY = y + 18;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            drawRect( x - 2, currentY, x + 122, currentY + 20,accentColor.getRGB() );
            drawRect(  x - 1, currentY, x + 121, currentY + 19,outlineColor1.getRGB());
            drawRect(  x, currentY, x + 120, currentY + 18,m.isEnable() ? accentColor.getRGB() : backColor.getRGB());
            if (m.getKeyCode() == 0 || Keyboard.getKeyName(m.getKeyCode()) == null) {
                font.drawString( m.getName(), x + 116 - font.getStringWidth(m.getName()), currentY + 4, -1);
               } else{
                   String displayKeyCode = String.format("%s [%s]", m.getName(), Keyboard.getKeyName(m.getKeyCode()).toUpperCase());
                font.drawString(displayKeyCode, x + 116 - font.getStringWidth(displayKeyCode), currentY + 4, -1);
               }


            currentY += 18;

            if (!mExpand[i]) {
                continue;
            }

            for (int j = 0; j < m.settings.size(); j++) {
                final Setting s = m.settings.get(j);
                drawRect(x - 2, currentY, x + 122, currentY + 20,accentColor.getRGB());
                drawRect(x - 2, currentY, x + 122, currentY + 20,accentColor.getRGB() );
                drawRect( x - 1, currentY, x + 121, currentY + 19,outlineColor1.getRGB());
                if (s instanceof NumberSetting) {
                    final NumberSetting ds = (NumberSetting) s;
                    final String v = String.valueOf(ds.getValue());
                    drawRect(  x, currentY, x + 120, currentY + 18,outlineColor2.getRGB());
                    drawRect( x, currentY + 2, (float) (x + ds.getPercentage() * 120), currentY + 16, accentColor.getRGB());
                    font.drawString( s.name, x + 4, currentY + 4, settingTextColor);
                    font.drawString(v, x + 116 - font.getStringWidth(v), currentY + 4, -1);
                } else if (s instanceof ModeSetting) {
                    final ModeSetting ms = (ModeSetting) s;
                    drawRect(  x, currentY, x + 120, currentY + 18,outlineColor2.getRGB());
                    font.drawString( ((ModeSetting) s).getMode(), x + 4, currentY + 4, settingTextColor);
                    font.drawString(ms.name, x + 116 - font.getStringWidth(ms.getMode()), currentY + 4, -1);
                    if (ms.expand) {
                        for (String o : ms.modes) {
                            currentY += 18;
                            drawRect(  x - 2, currentY, x + 122, currentY + 20,accentColor.getRGB());
                            drawRect(  x - 1, currentY, x + 121, currentY + 19,outlineColor1.getRGB());
                            drawRect(  x, currentY, x + 120, currentY + 18,outlineColor2.getRGB());
                            font.drawString( o, x + 4, currentY + 4, settingTextColor);
                            //currentY += 18;
                        }
                    }
                } else if (s instanceof BooleanSetting) {
                    final BooleanSetting bs = (BooleanSetting) s;
                    drawRect(  x, currentY, x + 120, currentY + 18,bs.isEnable() ? accentColor.getRGB() : outlineColor2.getRGB());
                    font.drawString(s.name, x + 4, currentY + 4, settingTextColor);
                } else if(s instanceof KeyBindSetting) {
                    KeyBindSetting setting = (KeyBindSetting)s;
                    font.drawString(setting.name+": "+(clicked?"inputwaiting...": Keyboard.getKeyName(((KeyBindSetting) s).getKeyCode()).toUpperCase()), (int)(x + 4), (int)(currentY + 4), settingTextColor);

                }
                currentY += 18;
            }
        }
    }



    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (ClickUtil.isHovered(x, y, 140, 18, mouseX, mouseY)) {
            if (button == 0) {
                lastX = (float) (x - mouseX);
                lastY = (float) (y - mouseY);
                dragging = true;
            } else if(button == 1){
                expand = !expand;
            }
            return;
        }

        if (!expand) {
            return;
        }

        double currentY = y + 18;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            if (ClickUtil.isHovered2(x - 2, currentY, x + 122, currentY + 20, mouseX, mouseY)) {
                if (button == 0) {
                    m.toggle();
                } else if(button == 1){
                    mExpand[i] = !mExpand[i];
                }
                return;
            }
            currentY += 18;

            if (!mExpand[i]) {
                continue;
            }

            for (int j = 0; j < m.settings.size(); j++) {
                final Setting s = m.settings.get(j);
                if (s instanceof NumberSetting) {
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        doubleSetting = (NumberSetting) s;
                        return;
                    }
                } else if (s instanceof ModeSetting) {
                    final ModeSetting ms = (ModeSetting) s;
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        if (button == 0) {
                            ms.cycle();
                        } else if(button == 1){
                            ms.expand = !ms.expand;
                        }
                        return;
                    }
                    if (ms.expand) {
                        for (String o : ms.modes) {
                            currentY += 18;
                            if (ClickUtil.isHovered2(x - 2, currentY, x + 122, currentY + 20, mouseX, mouseY)) {
                                ms.setModes(o);
                                return;
                            }
                        }
                    }
                } else if (s instanceof BooleanSetting) {
                    final BooleanSetting bs = (BooleanSetting) s;
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        bs.toggle();
                        return;
                    }
                }
                else if(s instanceof  KeyBindSetting) {
               final KeyBindSetting ks = (KeyBindSetting) s;
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY) ) {
                        if (button == 0) {
                            keyBindSetting = ks;
                         clicked = true;
                        } else if(button == 1) {
                         ks.setKeyCode(0);
                            keyCode = 0;
                            clicked = false;
                        }

                        return;
                    }
                }
                currentY += 18;
            }
        }
    }
    public void keyPressed(char typedChar, int keyCode) {
           if(clicked && keyBindSetting != null) {
               keyBindSetting.setKeyCode(keyCode);
               clicked= false;
           }

    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        doubleSetting = null;
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {

    }
    public void onClose() {
        clicked = false;
    }
}
