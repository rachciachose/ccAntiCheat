// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.wrappers;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.PacketType;

public class WrapperPlayServerKeepAlive extends AbstractPacket
{
    public static final PacketType TYPE;
    
    public WrapperPlayServerKeepAlive() {
        super(new PacketContainer(WrapperPlayServerKeepAlive.TYPE), WrapperPlayServerKeepAlive.TYPE);
        this.handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerKeepAlive(final PacketContainer packet) {
        super(packet, WrapperPlayServerKeepAlive.TYPE);
    }
    
    public int getKeepAliveId() {
        return (int)this.handle.getIntegers().read(0);
    }
    
    public void setKeepAliveId(final int value) {
        this.handle.getIntegers().write(0, (Object)value);
    }
    
    static {
        TYPE = PacketType.Play.Server.KEEP_ALIVE;
    }
}
