package client.mixin.client;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiChest.class)
public class MixinGuiChest {

    @Shadow IInventory lowerChestInventory;

    public IInventory getlowercaseChest() {
      return lowerChestInventory;
  }





}

