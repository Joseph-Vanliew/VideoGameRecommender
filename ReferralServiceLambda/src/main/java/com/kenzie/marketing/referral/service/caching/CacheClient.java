package com.kenzie.marketing.referral.service.caching;

import com.kenzie.marketing.referral.service.dependency.DaggerServiceComponent;

import com.kenzie.marketing.referral.service.exceptions.InvalidDataException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import java.util.Optional;

public class CacheClient {


    @Inject
    public CacheClient() {}


    // Put your Cache Client Here

    // Since Jedis is being used multithreaded, you MUST get a new Jedis instances and close it inside every method.
    // Do NOT use a single instance across multiple of these methods

    // Use Jedis in each method by doing the following:
    // Jedis cache = DaggerServiceComponent.create().provideJedis();
    // ... use the cache
    // cache.close();

    // Remember to check for null keys!

    public void setValue(String key, int seconds, String value) {
        checkNonNullKey(key);
        Jedis cache = DaggerServiceComponent.create().provideJedis();
        cache.setex(key, seconds, value);
        cache.close();
    }
    public Optional<String> getValue(String key) {
        checkNonNullKey(key);
        Jedis cache = DaggerServiceComponent.create().provideJedis();
        Optional<String> optionalS = Optional.ofNullable(cache.get(key));
        cache.close();
        return optionalS;
    }
    public void invalidate(String key) {
        checkNonNullKey(key);
        Jedis cache = DaggerServiceComponent.create().provideJedis();
        cache.del(key);
        cache.close();
    }
    private void checkNonNullKey(String key) {
        if(null == key) {
            throw new InvalidDataException("key is not valid" + null);
        }
    }

}
