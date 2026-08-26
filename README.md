# OTP Generator Spring Boot Starter

Spring Boot starter for OCRA-based OTP generation and validation with transaction-scoped caching.

## Installation

```xml
<dependency>
    <groupId>com.tosan</groupId>
    <artifactId>otp-generator-spring-boot-starter</artifactId>
    <version>REPLACE_WITH_LATEST_VERSION</version>
</dependency>
```

## Setup

1. Implement `SecretKeyProvider` to supply each user's secret key:

```java
@Component
public class MySecretKeyProvider implements SecretKeyProvider {
    @Override
    public byte[] getSecretKey(String userId) {
        return loadSecretKey(userId);
    }
}
```

2. Configure OTP properties (optional — defaults shown):

```properties
otp.suite=OCRA-1:HOTP-SHA256-6:TX-T30S
otp.time-step-seconds=30
otp.crypto-algorithm=HmacSHA1
otp.allowed-clock-skew=1
```

3. For distributed caching, enable Redis:

```properties
tedisson.redis.enabled=true
tedisson.redis.single-server.address=localhost:6379
```

Without Redis, the starter uses local EhCache or Caffeine.

## Usage

Inject `OtpGenerationService` and implement `OtpRequest` / `OtpVerificationRequest` (or use your own DTOs):

```java
@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpGenerationService otpGenerationService;

    public String generate(String userId, Map<String, Object> otpData, int length) {
        return otpGenerationService.generate(new MyOtpRequest(userId, otpData, length));
    }

    public void validate(String userId, String otp, Map<String, Object> otpData, int length) {
        otpGenerationService.validate(new MyOtpVerificationRequest(userId, otp, otpData, length));
    }
}
```

See `otp-generator-spring-boot-sample` for a complete example.
