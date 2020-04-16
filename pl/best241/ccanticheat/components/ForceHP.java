// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

import com.comphenix.protocol.events.PacketListener;
import java.util.Iterator;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.entity.Wither;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import org.bukkit.event.EventHandler;
import java.util.List;
import org.bukkit.entity.Entity;
import java.util.Arrays;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import pl.best241.ccanticheat.CcAntiCheat;
import org.bukkit.event.Listener;

public class ForceHP implements Listener
{
    private static CcAntiCheat plugin;
    
    public ForceHP(final CcAntiCheat antiCheat) {
        ForceHP.plugin = antiCheat;
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)ForceHP.plugin);
        onMetadata();
    }
    
    @EventHandler
    public void onMount(final VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ForceHP.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    if (event.getVehicle().isValid() && event.getEntered().isValid()) {
                        ProtocolLibrary.getProtocolManager().updateEntity((Entity)event.getVehicle(), (List)Arrays.asList((Player)event.getEntered()));
                    }
                }
            });
        }
    }
    
    public static void onMetadata() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(ForceHP.plugin, new PacketType[] { PacketType.Play.Server.ENTITY_METADATA }) {
            public void onPacketSending(final PacketEvent event) {
                try {
                    final Player observer = event.getPlayer();
                    final StructureModifier entityModifer = event.getPacket().getEntityModifier(observer.getWorld());
                    final Entity entity = (Entity)entityModifer.read(0);
                    if (entity != null && observer != entity && entity instanceof LivingEntity && (!(entity instanceof EnderDragon) || !(entity instanceof Wither)) && (entity.getPassenger() == null || entity.getPassenger() != observer)) {
                        event.setPacket(event.getPacket().deepClone());
                        final StructureModifier watcher = event.getPacket().getWatchableCollectionModifier();
                        for (final Object watchObj : (List)watcher.read(0)) {
                            final WrappedWatchableObject watch = (WrappedWatchableObject)watchObj;
                            if (watch.getIndex() == 6) {
                                if ((float)watch.getValue() <= 0.0f) {
                                    continue;
                                }
                                watch.setValue((Object)1337.0f);
                            }
                        }
                    }
                }
                catch (FieldAccessException ex) {}
            }
        });
    }
}
