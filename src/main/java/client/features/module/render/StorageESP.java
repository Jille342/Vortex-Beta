package client.features.module.render;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.render.RenderUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StorageESP extends Module {
	public StorageESP() {
		super("StorageESP", Keyboard.KEY_NONE, Category.RENDER);
	}

	CopyOnWriteArrayList<BlockPos> Strages = new CopyOnWriteArrayList<BlockPos>();

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		for(BlockPos pos : Strages) {
        		if(!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockChest)) {
        			Strages.remove(Strages.indexOf(pos));
        		}
    		}
    	}
    	
    	if(e instanceof EventRenderWorld) {
    		for(TileEntity pos : mc.theWorld.loadedTileEntityList) {
    			if(pos.getBlockType() instanceof BlockChest)
    				RenderUtils.drawBlockBox(pos.getPos(), new Color(255, 255, 255, 0x40));
    			if(pos.getBlockType() instanceof BlockEnderChest)
    				RenderUtils.drawBlockBox(pos.getPos(), new Color(108, 0, 143, 0x40));
    		}
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
