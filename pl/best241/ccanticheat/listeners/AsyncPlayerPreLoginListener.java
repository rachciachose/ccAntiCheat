// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.listeners;

import org.bukkit.event.EventHandler;
import java.util.UUID;
import org.bukkit.ChatColor;
import pl.best241.ccanticheat.backend.RedisBackend;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.Listener;

public class AsyncPlayerPreLoginListener implements Listener
{
    @EventHandler
    public static void onPlayerPreLoginListener(final AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();
        if (RedisBackend.isBanned(uuid)) {
            final long banTime = RedisBackend.getBanTime(uuid);
            final String timeTo = getTimeTo(banTime);
            event.setKickMessage(ChatColor.BLUE + "Jestes zbanowany jeszcze " + ChatColor.RED + timeTo);
        }
    }
    
    public static String getTimeTo(final long to) {
        final long now = System.currentTimeMillis();
        long diff = to - now;
        long seconds = diff / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        final long days = hours / 24L;
        diff -= seconds * 1000L;
        seconds -= minutes * 60L;
        minutes -= hours * 60L;
        hours -= days * 24L;
        String time = "";
        if (days != 0L) {
            time = time + days + "d ";
        }
        if (hours != 0L) {
            time = time + hours + "h ";
        }
        if (minutes != 0L) {
            time = time + minutes + "m ";
        }
        if (seconds != 0L) {
            time = time + seconds + "s ";
        }
        return time.substring(0, time.length() - 1);
    }
}
