// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.logs;

import java.util.Collection;
import redis.clients.jedis.Tuple;
import java.util.ArrayList;
import redis.clients.jedis.Jedis;
import pl.best241.rdbplugin.JedisFactory;
import java.util.UUID;

public class LogManager
{
    public static void logCheat(final UUID uuid, final String message) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.zadd("ccAntiCheat.reports:" + uuid.toString(), (double)System.currentTimeMillis(), message);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static ArrayList<Tuple> getLogs(final UUID uuid, final int size) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<Tuple> logs = new ArrayList<Tuple>(jedis.zrevrangeWithScores("ccAntiCheat.reports:" + uuid.toString(), 0L, (long)size));
        JedisFactory.getInstance().returnJedis(jedis);
        return logs;
    }
}
