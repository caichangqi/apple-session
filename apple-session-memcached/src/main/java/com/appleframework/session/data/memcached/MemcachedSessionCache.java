package com.appleframework.session.data.memcached;

import com.appleframework.session.data.SerializeUtil;
import com.appleframework.session.data.SessionCache;
import com.appleframework.session.data.SessionMap;
import com.whalin.MemCached.MemCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * User: Antergone
 * Date: 16/2/25
 */
public class MemcachedSessionCache implements SessionCache {

    private static final Logger LOG = LoggerFactory.getLogger(MemcachedSessionCache.class);
    
    private MemcachedPool cachePool;

    @Override
    public void put(String sessionId, SessionMap sessionMap, int timeout) {
        MemCachedClient memCachedClient = null;
        try {
            memCachedClient = cachePool.getClient();
            memCachedClient.set(sessionId, SerializeUtil.serialize(sessionMap), new Date(timeout * 1000));
        } catch (Exception e) {
            LOG.error("Put session to memcached error", e);
        }
    }

    @Override
    public SessionMap get(String sessionId) {
        MemCachedClient memCachedClient = null;
        SessionMap sessionMap = null;
        try {
            memCachedClient = cachePool.getClient();
            if (memCachedClient.keyExists(sessionId)) {
                sessionMap = (SessionMap) memCachedClient.get(sessionId);
            }
        } catch (Exception e) {
            LOG.error("Read session from memcached error", e);
            return null;
        }
        return sessionMap;
    }

    @Override
    public void setMaxInactiveInterval(String sessionId, int interval) {

        MemCachedClient memCachedClient = null;
        try {
            memCachedClient = cachePool.getClient();
            if (memCachedClient.keyExists(sessionId)) {
                memCachedClient.set(sessionId, memCachedClient.get(sessionId), new Date(interval * 1000));
            }
        } catch (Exception e) {
            LOG.error("Set session max inactive interval to memcached error", e);
        }

    }

    @Override
    public void destroy(String sessionId) {

        MemCachedClient memCachedClient = null;
        try {
            memCachedClient = cachePool.getClient();
            memCachedClient.delete(sessionId);
        } catch (Exception e) {
            LOG.error("Destroy session from memcached error", e);
        }
    }

	public void setCachePool(MemcachedPool cachePool) {
		this.cachePool = cachePool;
	}

    
    
    
    
}
