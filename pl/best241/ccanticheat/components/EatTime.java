// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

class EatTime
{
    private final long time1;
    private final long time2;
    
    public EatTime(final long time1, final long time2) {
        this.time1 = time1;
        this.time2 = time2;
    }
    
    public long getTime1() {
        return this.time1;
    }
    
    public long getTime2() {
        return this.time2;
    }
    
    public boolean isBetween(final long time) {
        return time > this.time1 && time < this.time2;
    }
}
