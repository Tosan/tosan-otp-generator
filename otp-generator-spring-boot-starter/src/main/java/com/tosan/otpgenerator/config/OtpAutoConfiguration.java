package com.tosan.otpgenerator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosan.client.redis.api.TedissonCacheManager;
import com.tosan.otpgenerator.service.OcraOtpProvider;
import com.tosan.otpgenerator.service.OtpGenerationService;
import com.tosan.otpgenerator.service.SecretKeyProvider;
import com.tosan.otpgenerator.service.cache.*;
import com.tosan.otpgenerator.utils.OtpUtil;
import com.tosan.otpgenerator.utils.OtpValidator;
import com.tosan.otpgenerator.utils.TimeStepUtil;
import com.tosan.tools.lockmanager.api.LockManagementService;
import com.tosan.tools.lockmanager.impl.redis.RedisLockManagementService;
import com.tosan.tools.lockmanager.impl.redis.RedisLockService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@AutoConfiguration
@EnableConfigurationProperties(OtpProperties.class)
public class OtpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OtpUtil otpUtil(ObjectMapper objectMapper) {
        return new OtpUtil(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public Clock otpClock() {
        return Clock.systemUTC();
    }

    @Bean
    @ConditionalOnMissingBean
    public TimeStepUtil timeStepUtil(Clock otpClock) {
        return new TimeStepUtil(otpClock);
    }

    @Bean
    @ConditionalOnMissingBean
    public OtpCacheInitializer otpCacheInitializer(TedissonCacheManager tedissonCacheManager) {
        return new OtpCacheInitializer(tedissonCacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserTransactionCacheService userTransactionCacheService(TedissonCacheManager cacheManager) {
        return new UserTransactionCacheService(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCacheService transactionCacheService(
            TedissonCacheManager cacheManager) {

        return new TransactionCacheService(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public OtpConsumptionMarkerService otpConsumptionMarker(
            TedissonCacheManager cacheManager,
            OtpProperties properties,
            UserTransactionManager userTransactionManager,
            OtpUtil otpUtil) {

        return new OtpConsumptionMarkerService(cacheManager, properties, userTransactionManager, otpUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SecretKeyProvider.class)
    public OtpGenerationService otpGenerationService(
            UserTransactionManager userTransactionManager,
            OtpUtil otpUtil,
            OcraOtpProvider ocraOtpProvider) {

        return new OtpGenerationService(userTransactionManager, otpUtil, ocraOtpProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SecretKeyProvider.class)
    public OcraOtpProvider ocraOtpProvider(
            OtpProperties properties,
            UserTransactionManager userTransactionManager,
            OtpUtil otpUtil,
            SecretKeyProvider secretKeyProvider,
            OtpValidator otpValidator,
            TimeStepUtil timeStepUtil,
            OtpConsumptionMarkerService consumptionMarker) {

        return new OcraOtpProvider(properties, userTransactionManager, otpUtil, secretKeyProvider,
                otpValidator, timeStepUtil, consumptionMarker);
    }

    @Bean
    @ConditionalOnMissingBean
    public OtpValidator otpValidator() {
        return new OtpValidator();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "tedisson.redis.enabled", havingValue = "false", matchIfMissing = true)
    static class LocalCacheConfiguration {

        @Bean
        @ConditionalOnMissingBean(UserTransactionManager.class)
        UserTransactionManager userTransactionManager(
                UserTransactionCacheService userTransactionCacheService,
                TransactionCacheService transactionCacheService,
                OtpProperties otpProperties,
                OtpUtil otpUtil) {

            return new LocalUserTransactionManager(
                    userTransactionCacheService, transactionCacheService, otpProperties, otpUtil);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "tedisson.redis.enabled", havingValue = "true")
    static class RedisCacheConfiguration {

        @Bean
        @ConditionalOnMissingBean(LockManagementService.class)
        @ConditionalOnBean(RedissonClient.class)
        LockManagementService lockManagementService(RedissonClient redissonClient) {
            RedisLockService redisLockService = new RedisLockService();
            redisLockService.setRedisClient(redissonClient);
            return new RedisLockManagementService(redisLockService);
        }

        @Bean
        @ConditionalOnMissingBean(UserTransactionManager.class)
        @ConditionalOnBean(LockManagementService.class)
        UserTransactionManager userTransactionManager(
                UserTransactionCacheService userTransactionCacheService,
                TransactionCacheService transactionCacheService,
                LockManagementService lockManagementService,
                OtpProperties otpProperties,
                OtpUtil otpUtil) {

            return new CentralUserTransactionManager(
                    userTransactionCacheService, transactionCacheService,
                    lockManagementService, otpProperties, otpUtil);
        }
    }
}
