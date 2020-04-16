// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.pubsub;

import java.util.UUID;
import pl.best241.rdbplugin.pubsub.PubSub;

public class PubSubManager
{
    public static void sendReport(final String message) {
        PubSub.broadcast("anticheatReport", message);
    }
    
    public static void addPlayerToCheck(final UUID uuid) {
        PubSub.broadcast("anticheatReportToCheck", uuid.toString());
    }
}
