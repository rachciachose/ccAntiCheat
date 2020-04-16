// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.wrappers;

import java.lang.reflect.InvocationTargetException;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;
import net.minecraft.util.com.google.common.base.Objects;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public abstract class AbstractPacket
{
    protected PacketContainer handle;
    
    protected AbstractPacket(final PacketContainer handle, final PacketType type) {
        if (handle == null) {
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        }
        if (!Objects.equal((Object)handle.getType(), (Object)type)) {
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        }
        this.handle = handle;
    }
    
    public PacketContainer getHandle() {
        return this.handle;
    }
    
    public void sendPacket(final Player receiver) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, this.getHandle());
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }
    
    public void recievePacket(final Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, this.getHandle());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot recieve packet.", e);
        }
    }
}
