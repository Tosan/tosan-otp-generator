package com.tosan.otpgenerator.service.cache;

import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.otpgenerator.service.enums.CacheName;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class UserTransactionCacheService {

    private static final String USER_CACHE = CacheName.USER.name();


    private final TedissonCacheManager cacheManager;

    public UserTransactionCacheService(TedissonCacheManager cacheManager) {

        this.cacheManager = cacheManager;
    }

    public void addTransactionToUser(String userId, String transactionUserKey, long ttl) {

        Set<String> transactionUserKeys = loadTransactionKeys(userId);
        transactionUserKeys.add(transactionUserKey);
        cacheManager.addItemToCache(USER_CACHE, userId, transactionUserKeys, ttl, TimeUnit.SECONDS);

    }

    public Set<String> getTransactionIds(String userId) {

        Set<String> keys = cacheManager.getItemFromCache(USER_CACHE, userId);
        if (keys == null || keys.isEmpty()) {
            return Set.of();
        }
        return Set.copyOf(keys);
    }

    public void removeTransaction(String userId, String transactionUserKey) {



            Set<String> updatedKeys = loadTransactionKeys(userId);
            if (updatedKeys.isEmpty()) {
                return;
            }

            updatedKeys.remove(transactionUserKey);
            if (updatedKeys.isEmpty()) {
                removeUser(userId);
            } else {
                cacheManager.replaceCacheItem(USER_CACHE, userId, updatedKeys);
            }
    }

    public Set<String> loadTransactionKeys(String userId) {

        return Optional.ofNullable(cacheManager.<Set<String>>getItemFromCache(USER_CACHE, userId))
                .map(HashSet::new)
                .orElseGet(HashSet::new);
    }

    public void removeUser(String userId) {

        cacheManager.removeItemFromCache(USER_CACHE, userId);
    }
}
