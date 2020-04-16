// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccanticheat.components;

class KeepAliveData
{
    private final int keepAliveId;
    private final long keepAliveTime;
    
    public KeepAliveData(final int keepAliveId) {
        this.keepAliveId = keepAliveId;
        this.keepAliveTime = System.currentTimeMillis();
    }
    
    public int getKeepAliveId() {
        return this.keepAliveId;
    }
    
    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }
    
    public int getPing() {
        return (int)(System.currentTimeMillis() - this.getKeepAliveTime());
    }
    
    @Override
    public String toString() {
        return "KeepAliveId[" + this.keepAliveId + ";" + this.keepAliveTime + "]";
    }
}
