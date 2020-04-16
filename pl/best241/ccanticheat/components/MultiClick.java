// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

import com.comphenix.protocol.events.PacketListener;
import pl.best241.ccanticheat.pubsub.PubSubManager;
import pl.best241.ccanticheat.logs.LogManager;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.comphenix.protocol.wrappers.EnumWrappers;
import pl.best241.ccanticheat.wrappers.WrapperPlayClientUseEntity;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import pl.best241.ccanticheat.CcAntiCheat;

public class MultiClick
{
    public static CcAntiCheat plugin;
    private static HashMap<UUID, Integer> hits;
    private static ArrayList<UUID> attackBlocked;
    
    public MultiClick(final CcAntiCheat plugin) {
        MultiClick.plugin = plugin;
        handleDamage();
        removeClicksTicker();
    }
    
    public static void handleDamage() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(MultiClick.plugin, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
            public void onPacketReceiving(final PacketEvent event) {
                final WrapperPlayClientUseEntity use = new WrapperPlayClientUseEntity(event.getPacket());
                final Player sender = event.getPlayer();
                final int targetID = use.getTargetID();
                final EnumWrappers.EntityUseAction mouse = use.getMouse();
                if (mouse == EnumWrappers.EntityUseAction.ATTACK) {
                    if (MultiClick.attackBlocked.contains(sender.getUniqueId())) {
                        event.setCancelled(true);
                    }
                    final int hits = MultiClick.addHit(sender.getUniqueId());
                    if (hits >= 15) {
                        if (sender.getName().equals("kendal2001")) {
                            System.out.println("Canceling kendal2001(fucking children) who is attacking so fast...");
                            return;
                        }
                        if (!MultiClick.attackBlocked.contains(sender.getUniqueId())) {
                            sender.sendMessage(AntiAura.prefix + ChatColor.RED + "Uzyles multi clicka! Nie mozesz atakowac przez 10 sekund!");
                            MultiClick.attackBlocked.add(sender.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(this.plugin, (Runnable)new Runnable() {
                                @Override
                                public void run() {
                                    MultiClick.attackBlocked.remove(sender.getUniqueId());
                                }
                            }, 200L);
                        }
                        LogManager.logCheat(sender.getUniqueId(), "Multi click(" + hits + ")");
                        PubSubManager.sendReport(AntiAura.prefix + ChatColor.RED + sender.getName() + " uzyl multi klicka(" + hits + ">=11 klikniec/s)!");
                    }
                }
            }
        });
    }
    
    public static int getHits(final UUID uuid) {
        if (MultiClick.hits.containsKey(uuid)) {
            return MultiClick.hits.get(uuid);
        }
        return 0;
    }
    
    public static int addHit(final UUID uuid) {
        final int hitsNumber = getHits(uuid) + 1;
        MultiClick.hits.put(uuid, hitsNumber);
        return hitsNumber;
    }
    
    public static void removeClicksTicker() {
        Bukkit.getScheduler().runTaskTimer((Plugin)MultiClick.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    MultiClick.hits.put(player.getUniqueId(), 0);
                }
            }
        }, 20L, 20L);
    }
    
    static {
        MultiClick.hits = new HashMap<UUID, Integer>();
        MultiClick.attackBlocked = new ArrayList<UUID>();
    }
}
