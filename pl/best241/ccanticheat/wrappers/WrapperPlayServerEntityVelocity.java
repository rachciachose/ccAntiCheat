// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.wrappers;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Entity;
import org.bukkit.World;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType;

public class WrapperPlayServerEntityVelocity extends AbstractPacket
{
    public static final PacketType TYPE;
    
    public WrapperPlayServerEntityVelocity() {
        super(new PacketContainer(WrapperPlayServerEntityVelocity.TYPE), WrapperPlayServerEntityVelocity.TYPE);
        this.handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityVelocity(final PacketContainer packet) {
        super(packet, WrapperPlayServerEntityVelocity.TYPE);
    }
    
    public int getEntityId() {
        return (int)this.handle.getIntegers().read(0);
    }
    
    public Entity getEntity(final World world) {
        return (Entity)this.handle.getEntityModifier(world).read(0);
    }
    
    public Entity getEntity(final PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }
    
    public void setEntityId(final int value) {
        this.handle.getIntegers().write(0, (Object)value);
    }
    
    public double getVelocityX() {
        return (int)this.handle.getIntegers().read(1) / 8000.0;
    }
    
    public void setVelocityX(final double value) {
        this.handle.getIntegers().write(1, (Object)(int)(value * 8000.0));
    }
    
    public double getVelocityY() {
        return (int)this.handle.getIntegers().read(2) / 8000.0;
    }
    
    public void setVelocityY(final double value) {
        this.handle.getIntegers().write(2, (Object)(int)(value * 8000.0));
    }
    
    public double getVelocityZ() {
        return (int)this.handle.getIntegers().read(3) / 8000.0;
    }
    
    public void setVelocityZ(final double value) {
        this.handle.getIntegers().write(3, (Object)(int)(value * 8000.0));
    }
    
    static {
        TYPE = PacketType.Play.Server.ENTITY_VELOCITY;
    }
}
