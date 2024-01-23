package client.event.listeners;

import client.event.Event;
import javafx.scene.chart.Axis;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventEntityHitbox extends Event {

    private final Entity entity;
    private AxisAlignedBB box;
    private float size;

    public EventEntityHitbox(Entity entity, AxisAlignedBB box) {
        this.entity = entity;
        this.box = box;
    }

    public Entity getEntity() {
        return entity;
    }

    public AxisAlignedBB getBox() {
        return box;
    }
    public void  setSize(float size1){
        size = size1;
    }
    public float getSize(){
        return size;
    }

    public void setBox(AxisAlignedBB box) {
        this.box = box;
    }
}
