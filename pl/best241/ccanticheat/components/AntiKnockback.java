// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

import org.bukkit.block.Block;
import org.bukkit.Material;
import pl.best241.ccanticheat.backend.RedisBackend;
import pl.best241.ccanticheat.logs.LogManager;
import pl.best241.ccanticheat.pubsub.PubSubManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.best241.ccanticheat.wrappers.WrapperPlayServerEntityVelocity;
import pl.best241.ccanticheat.wrappers.WrapperPlayServerKeepAlive;
import com.comphenix.protocol.events.PacketListener;
import pl.best241.ccanticheat.wrappers.WrapperPlayClientKeepAlive;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import java.util.UUID;
import java.util.HashMap;
import pl.best241.ccanticheat.CcAntiCheat;

public class AntiKnockback
{
    private CcAntiCheat plugin;
    private static final HashMap<UUID, Integer> playerPing;
    private static final HashMap<UUID, KeepAliveData> keepAliveTimeId;
    private static final int lagTime = 100;
    private static HashMap<UUID, Long> lastPacketSend;
    
    public AntiKnockback(final CcAntiCheat antiCheat) {
        this.plugin = antiCheat;
        this.addListener();
    }
    
    private void addListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.plugin, new PacketType[] { PacketType.Play.Client.KEEP_ALIVE }) {
            public void onPacketReceiving(final PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
                    final WrapperPlayClientKeepAlive keepAlive = new WrapperPlayClientKeepAlive(event.getPacket());
                    final int id = keepAlive.getKeepAliveId();
                    final UUID uuid = event.getPlayer().getUniqueId();
                    if (AntiKnockback.keepAliveTimeId.containsKey(uuid)) {
                        final KeepAliveData data = AntiKnockback.keepAliveTimeId.get(uuid);
                        AntiKnockback.playerPing.put(uuid, data.getPing());
                    }
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.plugin, new PacketType[] { PacketType.Play.Server.KEEP_ALIVE }) {
            public void onPacketSending(final PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.KEEP_ALIVE) {
                    final WrapperPlayServerKeepAlive keepAlive = new WrapperPlayServerKeepAlive(event.getPacket());
                    AntiKnockback.keepAliveTimeId.put(event.getPlayer().getUniqueId(), new KeepAliveData(keepAlive.getKeepAliveId()));
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.plugin, new PacketType[] { PacketType.Play.Server.ENTITY_VELOCITY }) {
            public void onPacketSending(final PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
                    final Player player = event.getPlayer();
                    final WrapperPlayServerEntityVelocity velocity = new WrapperPlayServerEntityVelocity(event.getPacket());
                    if (velocity.getEntityId() == player.getEntityId() && AntiKnockback.playerPing.containsKey(player.getUniqueId())) {
                        final double velocityX = velocity.getVelocityX();
                        final double velocityY = velocity.getVelocityY();
                        final double velocityZ = velocity.getVelocityZ();
                        if (velocityX != 0.0 && velocityZ != 0.0 && !AntiKnockback.this.isBlockOnHead(player.getLocation().add(0.0, 1.0, 0.0)) && !AntiKnockback.this.isBlockOnHead(player.getLocation()) && !AntiKnockback.this.isSorroundedByBlocks(player.getLocation()) && !AntiKnockback.this.isSorroundedByBlocks(player.getLocation().add(0.0, 1.0, 0.0)) && !AntiKnockback.hasLag(player.getUniqueId())) {
                            AntiKnockback.this.checkAntiKnock(player, AntiKnockback.playerPing.get(player.getUniqueId()));
                        }
                    }
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.plugin, new PacketType[] { PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK }) {
            public void onPacketReceiving(final PacketEvent event) {
                AntiKnockback.lastPacketSend.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            }
        });
    }
    
    public static boolean hasLag(final UUID uuid) {
        if (AntiKnockback.lastPacketSend.get(uuid) == null) {
            return true;
        }
        final boolean value = AntiKnockback.lastPacketSend.get(uuid) + 100L < System.currentTimeMillis();
        return AntiKnockback.lastPacketSend.get(uuid) + 100L < System.currentTimeMillis();
    }
    
    public static int getPing(final UUID uuid) {
        if (AntiKnockback.playerPing.get(uuid) == null) {
            return -1;
        }
        return AntiKnockback.playerPing.get(uuid);
    }
    
    private void checkAntiKnock(final Player player, final int ping) {
        final Location checkLocation = player.getLocation();
        final int ticks = (int)Math.ceil(ping / 1000.0 * 50.0);
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    final Location checkedLocation = player.getLocation();
                    if (player.getGameMode() == GameMode.SURVIVAL && checkLocation.getX() == checkedLocation.getX() && checkLocation.getY() == checkedLocation.getY() && checkLocation.getZ() == checkedLocation.getZ()) {
                        PubSubManager.sendReport(AntiAura.prefix + ChatColor.RED + player.getName() + " uzyl no knocka!");
                        LogManager.logCheat(player.getUniqueId(), "No knock");
                        if (RedisBackend.isBanModeEnabled() && CcAntiCheat.reports.contains(player.getUniqueId())) {
                            RedisBackend.banPlayer(player.getUniqueId(), 3600000L + System.currentTimeMillis());
                            player.kickPlayer(ChatColor.RED + "Cheater! Ban na godzine!");
                        }
                    }
                }
            }
        }, (long)ticks);
    }
    
    private boolean isBlockOnHead(final Location loc) {
        final Block block = loc.getBlock();
        final boolean value = block.getType() != Material.AIR;
        return value;
    }
    
    private boolean isSorroundedByBlocks(final Location loc) {
        final Block block1 = loc.add(1.0, 0.0, 0.0).getBlock();
        final Block block2 = loc.add(0.0, 0.0, 1.0).getBlock();
        final Block block3 = loc.add(-1.0, 0.0, 0.0).getBlock();
        final Block block4 = loc.add(0.0, 0.0, -1.0).getBlock();
        final Block block5 = loc.getBlock();
        final boolean value = this.areBlocksPassable(block1, block2, block3, block4, block5);
        return value;
    }
    
    private boolean areBlocksPassable(final Block... blocks) {
        for (final Block block : blocks) {
            if (block.getType() != Material.AIR && block.getType() != Material.DEAD_BUSH && block.getTypeId() != 31) {
                return true;
            }
        }
        return false;
    }
    
    static {
        playerPing = new HashMap<UUID, Integer>();
        keepAliveTimeId = new HashMap<UUID, KeepAliveData>();
        AntiKnockback.lastPacketSend = new HashMap<UUID, Long>();
    }
}
