// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.wrappers;

import java.util.Collection;
import com.google.common.primitives.Ints;
import java.util.List;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType;

public class WrapperPlayServerEntityDestroy extends AbstractPacket
{
    public static final PacketType TYPE;
    
    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(WrapperPlayServerEntityDestroy.TYPE), WrapperPlayServerEntityDestroy.TYPE);
        this.handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityDestroy(final PacketContainer packet) {
        super(packet, WrapperPlayServerEntityDestroy.TYPE);
    }
    
    public List<Integer> getEntities() {
        return (List<Integer>)Ints.asList((int[])this.handle.getIntegerArrays().read(0));
    }
    
    public void setEntities(final int[] entities) {
        this.handle.getIntegerArrays().write(0, (Object)entities);
    }
    
    public void setEntities(final List<Integer> entities) {
        this.setEntities(Ints.toArray((Collection)entities));
    }
    
    static {
        TYPE = PacketType.Play.Server.ENTITY_DESTROY;
    }
}
