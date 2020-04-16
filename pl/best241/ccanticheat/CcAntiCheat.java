// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat;

import java.util.Iterator;
import pl.best241.ccanticheat.listeners.PubSubRecieveMessageListener;
import org.bukkit.Bukkit;
import pl.best241.ccanticheat.pubsub.PubSubManager;
import org.bukkit.entity.Player;
import java.util.Date;
import java.text.DateFormat;
import redis.clients.jedis.Tuple;
import pl.best241.ccanticheat.logs.LogManager;
import pl.best241.ccsectors.api.CcSectorsAPI;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.best241.ccanticheat.backend.RedisBackend;
import pl.best241.ccanticheat.listeners.AsyncPlayerPreLoginListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import pl.best241.ccanticheat.pubsub.PubSubListener;
import java.util.UUID;
import java.util.ArrayList;
import pl.best241.ccanticheat.components.AntiKoxAndHit;
import pl.best241.ccanticheat.components.AntiKnockback;
import pl.best241.ccanticheat.components.MultiClick;
import pl.best241.ccanticheat.components.ForceHP;
import pl.best241.ccanticheat.components.AntiAura;
import org.bukkit.plugin.java.JavaPlugin;

public class CcAntiCheat extends JavaPlugin
{
    private CcAntiCheat plugin;
    private AntiAura antiAura;
    private ForceHP forceHP;
    private MultiClick mutiClick;
    private AntiKnockback antiKnockback;
    private AntiKoxAndHit antiKoxAndHit;
    public static ArrayList<UUID> reports;
    
    public void onEnable() {
        this.plugin = this;
        this.antiAura = new AntiAura(this);
        this.forceHP = new ForceHP(this);
        this.mutiClick = new MultiClick(this);
        this.antiKnockback = new AntiKnockback(this);
        this.antiKoxAndHit = new AntiKoxAndHit(this);
        this.getServer().getPluginManager().registerEvents((Listener)new PubSubListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AsyncPlayerPreLoginListener(), (Plugin)this);
        CcAntiCheat.reports = RedisBackend.getAllPlayersToCheck();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("aclogs")) {
            if (sender.hasPermission("ccAntiCheat.logs")) {
                String target = null;
                int number = -1;
                if (args.length == 1) {
                    target = args[0];
                }
                else {
                    if (args.length != 2) {
                        sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /aclogs nick iloscLogow");
                        return false;
                    }
                    target = args[0];
                    if (StringUtils.isNumeric((CharSequence)args[1])) {
                        number = Integer.parseInt(args[1]);
                    }
                    else {
                        number = -1;
                    }
                }
                final UUID uuid = CcSectorsAPI.getUUID(target);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Gracza nie ma w bazie danych!");
                    return false;
                }
                sender.sendMessage(ChatColor.DARK_GRAY + "==============" + ChatColor.YELLOW + "AntiCheat" + ChatColor.DARK_GRAY + "==============");
                for (final Tuple log : LogManager.getLogs(uuid, number)) {
                    sender.sendMessage(ChatColor.RED + DateFormat.getInstance().format(new Date((long)log.getScore())) + ": " + ChatColor.BLUE + log.getElement());
                }
                sender.sendMessage(ChatColor.DARK_GRAY + "============================");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("acmode")) {
            if (sender.hasPermission("ccAntiCheat.changeMode")) {
                if (args.length == 1 && (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false"))) {
                    final Boolean mode = Boolean.parseBoolean(args[0]);
                    RedisBackend.setBanModeEnabled(mode);
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Tryb banowania:" + ChatColor.BLUE + mode);
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /acmode true|false");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Nie masz uprawnien!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("report")) {
            if (args.length >= 2) {
                final String nick = args[0];
                final UUID uuid2 = CcSectorsAPI.getUUID(nick);
                if (uuid2 != null && CcSectorsAPI.isPlayerOnline(uuid2)) {
                    if (sender instanceof Player) {
                        final Player player = (Player)sender;
                        final int numberOfPlayerReports = RedisBackend.getNumberOfPlayerReports(player.getUniqueId());
                        if (numberOfPlayerReports > 5) {
                            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Przekroczyles limit 5 reportow dziennie!");
                            return false;
                        }
                        RedisBackend.setNumberOfPlayerReports(player.getUniqueId(), numberOfPlayerReports + 1);
                    }
                    String reason = "";
                    for (int i = 1; i < args.length; ++i) {
                        if (i != 1) {
                            reason += " ";
                        }
                        reason += args[i];
                    }
                    LogManager.logCheat(uuid2, "Reported by " + sender.getName() + " reason: " + reason);
                    RedisBackend.addPlayerToCheck(uuid2, System.currentTimeMillis() + 86400000L);
                    PubSubManager.addPlayerToCheck(uuid2);
                    PubSubManager.sendReport(AntiAura.prefix + ChatColor.DARK_GREEN + sender.getName() + " reported " + nick + " for: " + reason);
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.DARK_GREEN + "Gracz zostal zgloszony! Zostanie teraz sprawdzony!");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Gracz jest offline!");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Uzycie: /report NICK powod");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("acdebug")) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    final Player player2 = (Player)sender;
                    player2.sendMessage(AntiKnockback.getPing(player2.getUniqueId()) + " " + AntiKnockback.hasLag(player2.getUniqueId()));
                }
                else if (args.length == 1) {
                    final Player player2 = Bukkit.getPlayer(args[0]);
                    if (player2 != null && player2.isOnline()) {
                        sender.sendMessage(AntiKnockback.getPing(player2.getUniqueId()) + " " + AntiKnockback.hasLag(player2.getUniqueId()));
                    }
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("acnotify") && sender instanceof Player) {
            final Player player2 = (Player)sender;
            if (player2.hasPermission("ccAntiCheat.notify")) {
                player2.sendMessage("Status changed to " + PubSubRecieveMessageListener.getStatus());
                PubSubRecieveMessageListener.broadcastNotifyPlayerStatus(!PubSubRecieveMessageListener.getStatus());
            }
        }
        return false;
    }
    
    static {
        CcAntiCheat.reports = new ArrayList<UUID>();
    }
}
