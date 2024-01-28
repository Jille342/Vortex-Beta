package client.event.listeners;

import client.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventBlockBounds extends Event {
    private Block block;
    private BlockPos pos;
    private AxisAlignedBB bounds;

    public AxisAlignedBB getBounds() {
        return bounds;
    }
    public EventBlockBounds(Block block, BlockPos pos, AxisAlignedBB bounds) {
        this.block = block;
        this.pos = pos;
        this.bounds = bounds;
    }
    public void setBounds(AxisAlignedBB bounds) {
        this.bounds = bounds;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }
}
