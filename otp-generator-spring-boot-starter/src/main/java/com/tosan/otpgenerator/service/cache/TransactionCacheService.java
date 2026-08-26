package com.tosan.otpgenerator.service.cache;

import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.otpgenerator.model.OtpData;
import com.tosan.otpgenerator.service.enums.CacheName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class TransactionCacheService {

    private final TedissonCacheManager cacheManager;
    private static final String TRANSACTION_CACHE = CacheName.TRANSACTION.name();

    public TransactionCacheService(TedissonCacheManager cacheManager) {

        this.cacheManager = cacheManager;
    }

    public void addTransaction(OtpData txData, String transactionUserKey, long ttlSeconds) {

        cacheManager.addItemToCache(TRANSACTION_CACHE, transactionUserKey, txData, ttlSeconds, TimeUnit.SECONDS);
    }

    public List<OtpData> getTransactions(Set<String> transactionIds) {

        List<OtpData> otpData = new ArrayList<>();
        for (String key : transactionIds) {
            OtpData tx = cacheManager.getItemFromCache(TRANSACTION_CACHE, key);
            if (tx != null) {
                otpData.add(tx);
            }
        }
        return otpData;
    }

    public void removeTransaction(String transactionUserKey) {

        cacheManager.removeItemFromCache(TRANSACTION_CACHE, transactionUserKey);
    }

    public OtpData getTransaction(String transactionUserKey) {

        return cacheManager.getItemFromCache(TRANSACTION_CACHE, transactionUserKey);
    }

    public Long getRemainingTtlSeconds(String transactionUserKey) {

        long remainingTtl = cacheManager.getRemainingItemTtl(TRANSACTION_CACHE, transactionUserKey, TimeUnit.SECONDS);
        if (remainingTtl < 0) {
            return null;
        }
        return remainingTtl;
    }
}
