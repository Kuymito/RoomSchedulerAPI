package org.example.roomschedulerapi.classroomscheduler.service.impl;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.example.roomschedulerapi.classroomscheduler.service.OtpService;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRATION_MINUTES = 5; // OTP will expire after 5 minutes
    private final LoadingCache<String, String> otpCache;

    public OtpServiceImpl() {
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String key) {
                        return ""; // Default value if key is not found, though we'll always put values explicitly
                    }
                });
    }

    /**
     * Generates a 6-digit OTP and caches it with the provided key (e.g., email).
     * @param key The identifier for the OTP (e.g., user's email).
     * @return The generated OTP string.
     */
    public String generateOtp(String key) {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        String otp = String.valueOf(otpValue);
        otpCache.put(key, otp);
        return otp;
    }

    /**
     * Retrieves the cached OTP for the given key.
     * @param key The identifier for the OTP.
     * @return The cached OTP, or null if it has expired or doesn't exist.
     */
    public String getOtp(String key) {
        try {
            return otpCache.getIfPresent(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clears the OTP from the cache for the given key.
     * @param key The identifier for the OTP to clear.
     */
    public void clearOtp(String key) {
        otpCache.invalidate(key);
    }
}