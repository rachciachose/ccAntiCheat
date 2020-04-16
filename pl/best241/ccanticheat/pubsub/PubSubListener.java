// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.pubsub;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import pl.best241.ccanticheat.CcAntiCheat;
import java.util.UUID;
import pl.best241.ccanticheat.listeners.PubSubRecieveMessageListener;
import org.bukkit.Bukkit;
import pl.best241.rdbplugin.events.PubSubRecieveMessageEvent;
import org.bukkit.event.Listener;

public class PubSubListener implements Listener
{
    @EventHandler
    public static void onPubSubListener(final PubSubRecieveMessageEvent event) {
        final String channel = event.getPayload().getSubChannel();
        if (channel.equals("anticheatReport")) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("ccAntiCheat.viewReports") && PubSubRecieveMessageListener.getStatus()) {
                    player.sendMessage(event.getPayload().getMessage());
                }
            }
        }
        else if (channel.equals("anticheatReportToCheck")) {
            final UUID uuid = UUID.fromString(event.getPayload().getMessage());
            CcAntiCheat.reports.add(uuid);
        }
    }
}
