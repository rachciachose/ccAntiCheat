// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.backend;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import redis.clients.jedis.Jedis;
import pl.best241.rdbplugin.JedisFactory;
import java.util.UUID;

public class RedisBackend
{
    public static void banPlayer(final UUID uuid, final Long time) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccAntiCheat.bans", uuid.toString(), time.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static boolean isBanned(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String hget = jedis.hget("ccAntiCheat.bans", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        return hget != null && Long.valueOf(hget) <= System.currentTimeMillis();
    }
    
    public static long getBanTime(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String hget = jedis.hget("ccAntiCheat.bans", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        if (hget == null) {
            return -1L;
        }
        return Long.valueOf(hget);
    }
    
    public static boolean isBanModeEnabled() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String get = jedis.get("ccAntiCheat.banMode");
        JedisFactory.getInstance().returnJedis(jedis);
        return get != null && Boolean.valueOf(get);
    }
    
    public static void setBanModeEnabled(final Boolean mode) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.set("ccAntiCheat.banMode", mode.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static int getNumberOfPlayerReports(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String hget = jedis.hget("ccAntiCheat.reports", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        if (hget == null) {
            return 0;
        }
        final String[] data = hget.split(";");
        final String date = data[0];
        final String numberString = data[1];
        System.out.println("Get number of players " + hget);
        if (getCurrentDate().equals(date)) {
            return Integer.valueOf(numberString);
        }
        return 0;
    }
    
    public static void setNumberOfPlayerReports(final UUID uuid, final int number) {
        final String date = getCurrentDate();
        final String data = date + ";" + Integer.toString(number);
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        System.out.println("Set number of players " + data);
        jedis.hset("ccAntiCheat.reports", uuid.toString(), data);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void addPlayerToCheck(final UUID uuid, final long timeTo) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccAntiCheat.playersToCheck", uuid.toString(), Long.toString(timeTo));
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void removePlayerToCheck(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hdel("ccAntiCheat.playersToCheck", new String[] { uuid.toString() });
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static ArrayList<UUID> getAllPlayersToCheck() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> hgetAll = (Map<String, String>)jedis.hgetAll("ccAntiCheat.playersToCheck");
        JedisFactory.getInstance().returnJedis(jedis);
        final ArrayList<UUID> uuids = new ArrayList<UUID>();
        for (final String key : hgetAll.keySet()) {
            final String value = hgetAll.get(key);
            final long time = Long.valueOf(value);
            if (time >= System.currentTimeMillis()) {
                final UUID uuid = UUID.fromString(key);
                uuids.add(uuid);
            }
        }
        return uuids;
    }
    
    private static String getCurrentDate() {
        return Calendar.getInstance().get(1) + "-" + Calendar.getInstance().get(2) + "-" + Calendar.getInstance().get(5);
    }
}
