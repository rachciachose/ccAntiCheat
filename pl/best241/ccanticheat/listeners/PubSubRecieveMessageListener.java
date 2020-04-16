// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.listeners;

import pl.best241.rdbplugin.pubsub.PubSub;
import org.bukkit.event.EventHandler;
import pl.best241.rdbplugin.events.PubSubRecieveMessageEvent;
import org.bukkit.event.Listener;

public class PubSubRecieveMessageListener implements Listener
{
    private static boolean notifyEnabled;
    
    @EventHandler
    public static void onPubSubRecieveMessage(final PubSubRecieveMessageEvent event) {
        if (event.getChannel().equals("ccAntiCheat.notify")) {
            final Boolean notifyStatus = Boolean.valueOf(event.getMessage());
            PubSubRecieveMessageListener.notifyEnabled = notifyStatus;
        }
    }
    
    public static void broadcastNotifyPlayerStatus(final Boolean status) {
        PubSub.broadcast("ccAntiCheat.notify", status.toString());
    }
    
    public static boolean getStatus() {
        return PubSubRecieveMessageListener.notifyEnabled;
    }
    
    static {
        PubSubRecieveMessageListener.notifyEnabled = true;
    }
}
