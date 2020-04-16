// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

import com.comphenix.protocol.events.PacketListener;
import pl.best241.ccanticheat.logs.LogManager;
import pl.best241.ccanticheat.pubsub.PubSubManager;
import org.bukkit.ChatColor;
import pl.best241.ccanticheat.wrappers.WrapperPlayClientUseEntity;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import pl.best241.ccanticheat.wrappers.WrapperPlayServerEntityDestroy;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import pl.best241.ccanticheat.wrappers.WrapperPlayServerNamedEntitySpawn;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import pl.best241.ccanticheat.backend.RedisBackend;
import java.util.UUID;
import java.util.HashMap;
import pl.best241.ccanticheat.CcAntiCheat;

public class AntiAura
{
    public static String prefix;
    private static CcAntiCheat plugin;
    private static final int FIRSTNPCID = -241;
    private static final int SECONDNPCID = -1337;
    private static HashMap<UUID, Integer> hits;
    
    public AntiAura(final CcAntiCheat plugin) {
        AntiAura.plugin = plugin;
        handleDamage();
        runAuraCheckTicker();
    }
    
    public static void runAuraCheckTicker() {
        final boolean banMode;
        final Player[] array;
        int length;
        int i;
        Player player;
        Location firstBot;
        Location secondBot;
        final Runnable ticker = () -> {
            banMode = RedisBackend.isBanModeEnabled();
            if (banMode) {
                Bukkit.getOnlinePlayers();
                for (length = array.length; i < length; ++i) {
                    player = array[i];
                    if (banMode || CcAntiCheat.reports.contains(player.getUniqueId())) {
                        removeHits(player.getUniqueId());
                        firstBot = calculateLocLeft(player.getLocation().clone(), 2.0, 1.3);
                        secondBot = calculateLocRight(player.getLocation().clone(), 2.0, 1.3);
                        spawnBot(player, firstBot, -241);
                        scheduleRunnable(() -> despawnBot(player, -241), 2L);
                        scheduleRunnable(() -> spawnBot(player, secondBot, -1337), 20L);
                        scheduleRunnable(() -> despawnBot(player, -1337), 22L);
                    }
                }
            }
            return;
        };
        scheduleRunnableTicker(ticker, 400L);
    }
    
    public static Location calculateLocLeft(final Location loc, final double add1, final double add2) {
        loc.setPitch(0.0f);
        final Vector vector1 = loc.getDirection();
        vector1.multiply(add1);
        loc.add(vector1);
        loc.setYaw(addYaw(loc.getYaw(), 100.0f));
        final Vector vector2 = loc.getDirection();
        vector2.multiply(-add2);
        loc.add(vector2);
        return loc;
    }
    
    public static Location calculateLocRight(final Location loc, final double add1, final double add2) {
        loc.setPitch(0.0f);
        final Vector vector1 = loc.getDirection();
        vector1.multiply(add1);
        loc.add(vector1);
        loc.setYaw(addYaw(loc.getYaw(), -100.0f));
        final Vector vector2 = loc.getDirection();
        vector2.multiply(-add2);
        loc.add(vector2);
        return loc;
    }
    
    public static float addYaw(float yaw, final float addYaw) {
        yaw += addYaw;
        yaw %= 360.0f;
        return yaw;
    }
    
    public static void scheduleRunnable(final Runnable runnable, final long ticks) {
        Bukkit.getScheduler().runTaskLater((Plugin)AntiAura.plugin, runnable, ticks);
    }
    
    public static void scheduleRunnableTicker(final Runnable runnable, final long ticks) {
        Bukkit.getScheduler().runTaskTimer((Plugin)AntiAura.plugin, runnable, ticks, ticks);
    }
    
    public static void spawnBot(final Player player, final Location loc, final int id) {
        final WrapperPlayServerNamedEntitySpawn spawned = new WrapperPlayServerNamedEntitySpawn();
        spawned.setEntityID(id);
        spawned.setPosition(loc.toVector());
        final String name = generateRandomString(16);
        spawned.setPlayerName(name);
        spawned.setYaw(loc.getYaw());
        spawned.setPitch(loc.getPitch());
        final WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (Object)(byte)0);
        spawned.setMetadata(watcher);
        spawned.sendPacket(player);
    }
    
    public static void despawnBot(final Player player, final int... id) {
        final WrapperPlayServerEntityDestroy despawn = new WrapperPlayServerEntityDestroy();
        despawn.setEntities(id);
        despawn.sendPacket(player);
    }
    
    public static String generateRandomString(final int length) {
        final StringBuilder buffer = new StringBuilder();
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final int charactersLength = characters.length();
        for (int i = 0; i < length; ++i) {
            final double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int)index));
        }
        return buffer.toString();
    }
    
    public static void handleDamage() {
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(AntiAura.plugin, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
            public void onPacketReceiving(final PacketEvent event) {
                final WrapperPlayClientUseEntity use = new WrapperPlayClientUseEntity(event.getPacket());
                final Player sender = event.getPlayer();
                final int targetID = use.getTargetID();
                use.getMouse();
                final int hits = AntiAura.getHits(sender.getUniqueId());
                if ((hits == 0 && targetID == -241) || (hits == 1 && targetID == -1337)) {
                    AntiAura.addHit(sender.getUniqueId());
                    if (AntiAura.getHits(sender.getUniqueId()) >= 2) {
                        AntiAura.removeHits(sender.getUniqueId());
                        PubSubManager.sendReport(AntiAura.prefix + ChatColor.RED + sender.getName() + " uzyl kill aury!");
                        LogManager.logCheat(sender.getUniqueId(), "Kill aura(" + hits + ")");
                        if (RedisBackend.isBanModeEnabled()) {
                            RedisBackend.banPlayer(sender.getUniqueId(), 3600000L + System.currentTimeMillis());
                            sender.kickPlayer(ChatColor.RED + "Cheater! Ban na godzine!");
                        }
                    }
                }
            }
        });
    }
    
    public static void addHit(final UUID uuid) {
        if (AntiAura.hits.containsKey(uuid)) {
            AntiAura.hits.put(uuid, AntiAura.hits.get(uuid) + 1);
            return;
        }
        AntiAura.hits.put(uuid, 1);
    }
    
    public static int getHits(final UUID uuid) {
        if (AntiAura.hits.containsKey(uuid)) {
            return AntiAura.hits.get(uuid);
        }
        return 0;
    }
    
    public static void removeHits(final UUID uuid) {
        AntiAura.hits.remove(uuid);
    }
    
    static {
        AntiAura.prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "AntiCheat" + ChatColor.GRAY + "] ";
        AntiAura.hits = new HashMap<UUID, Integer>();
    }
}
