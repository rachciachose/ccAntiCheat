// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

import java.util.Iterator;
import pl.best241.ccanticheat.backend.RedisBackend;
import pl.best241.ccanticheat.logs.LogManager;
import pl.best241.ccanticheat.pubsub.PubSubManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import pl.best241.ccanticheat.CcAntiCheat;
import org.bukkit.event.Listener;

public class AntiKoxAndHit implements Listener
{
    private CcAntiCheat plugin;
    private static HashMap<UUID, Long> startEat;
    private static HashMap<UUID, EatTime> eatTimes;
    private static HashMap<UUID, ArrayList<Long>> hitTimes;
    
    public AntiKoxAndHit(final CcAntiCheat plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.runCheckTicker();
    }
    
    @EventHandler
    public void playerInteractListener(final PlayerInteractEvent event) {
        final Action a = event.getAction();
        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (!event.hasItem()) {
            return;
        }
        final Player p = event.getPlayer();
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            AntiKoxAndHit.startEat.put(p.getUniqueId(), System.currentTimeMillis());
        }
    }
    
    @EventHandler
    public void playerConsumeListener(final PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        if (item.getType() == Material.GOLDEN_APPLE) {
            final long startTime = AntiKoxAndHit.startEat.get(player.getUniqueId());
            final EatTime eatTime = new EatTime(startTime, System.currentTimeMillis());
            AntiKoxAndHit.eatTimes.put(player.getUniqueId(), eatTime);
        }
    }
    
    @EventHandler
    public void playerEntityDamageByEntityListener(final EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if (damager instanceof Player) {
            final Player attacker = (Player)damager;
            ArrayList<Long> get = AntiKoxAndHit.hitTimes.get(attacker.getUniqueId());
            if (get == null) {
                get = new ArrayList<Long>();
            }
            get.add(System.currentTimeMillis());
            AntiKoxAndHit.hitTimes.put(attacker.getUniqueId(), get);
        }
    }
    
    public void runCheckTicker() {
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    final ArrayList<Long> get = AntiKoxAndHit.hitTimes.get(online.getUniqueId());
                    if (get != null) {
                        final EatTime eatTime = AntiKoxAndHit.eatTimes.get(online.getUniqueId());
                        if (eatTime != null) {
                            boolean cheater = false;
                            for (final Long hitTime : get) {
                                if (eatTime.isBetween(hitTime)) {
                                    cheater = true;
                                    break;
                                }
                            }
                            if (cheater) {
                                PubSubManager.sendReport(AntiAura.prefix + ChatColor.RED + online.getName() + " jadl koxa i bil jednoczesnie!");
                                LogManager.logCheat(online.getUniqueId(), "KoxAndHit");
                                if (RedisBackend.isBanModeEnabled() && CcAntiCheat.reports.contains(online.getUniqueId())) {
                                    RedisBackend.banPlayer(online.getUniqueId(), 3600000L + System.currentTimeMillis());
                                    online.kickPlayer(ChatColor.RED + "Cheater! Ban na godzine!");
                                }
                            }
                            get.clear();
                            AntiKoxAndHit.hitTimes.put(online.getUniqueId(), get);
                            AntiKoxAndHit.eatTimes.remove(online.getUniqueId());
                        }
                    }
                }
            }
        }, 40L, 40L);
    }
    
    static {
        AntiKoxAndHit.startEat = new HashMap<UUID, Long>();
        AntiKoxAndHit.eatTimes = new HashMap<UUID, EatTime>();
        AntiKoxAndHit.hitTimes = new HashMap<UUID, ArrayList<Long>>();
    }
}
