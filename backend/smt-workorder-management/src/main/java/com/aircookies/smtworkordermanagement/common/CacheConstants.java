package com.aircookies.smtworkordermanagement.common;

public class CacheConstants {

    public static final String DEPT_CACHE_KEY = "dept:";
    public static final String LINE_CACHE_KEY = "line:";
    public static final String PRODUCT_CACHE_KEY = "product:";
    public static final String ROLE_CACHE_KEY = "role:";
    public static final String USER_CACHE_KEY = "user:";
    public static final String WORKORDER_CACHE_KEY = "workorder:";
    public static final String JWT_TOKEN_CACHE_KEY = "jwt:token:";
    public static final String STATS_CACHE_KEY = "stats:";

    public static final long DEPT_CACHE_EXPIRE = 86400;
    public static final long LINE_CACHE_EXPIRE = 86400;
    public static final long PRODUCT_CACHE_EXPIRE = 43200;
    public static final long ROLE_CACHE_EXPIRE = 86400;
    public static final long USER_CACHE_EXPIRE = 1800;
    public static final long WORKORDER_CACHE_EXPIRE = 600;
    public static final long JWT_TOKEN_CACHE_EXPIRE = 86400;
    public static final long STATS_CACHE_EXPIRE = 3600;

    public static String getUserCacheKey(String username) {
        return USER_CACHE_KEY + "username:" + username;
    }

    public static String getUserCacheKeyById(Long id) {
        return USER_CACHE_KEY + "id:" + id;
    }

    public static String getDeptCacheKey(Integer id) {
        return DEPT_CACHE_KEY + id;
    }

    public static String getLineCacheKey(Integer id) {
        return LINE_CACHE_KEY + id;
    }

    public static String getProductCacheKey(Long id) {
        return PRODUCT_CACHE_KEY + id;
    }

    public static String getWorkOrderCacheKey(Long id) {
        return WORKORDER_CACHE_KEY + id;
    }

    public static String getJwtTokenCacheKey(String token) {
        return JWT_TOKEN_CACHE_KEY + token;
    }

    public static String getStatsDailyCacheKey(String date) {
        return STATS_CACHE_KEY + "daily:" + date;
    }

    public static String getStatsProductionCacheKey(String startDate, String endDate) {
        return STATS_CACHE_KEY + "production:" + startDate + ":" + endDate;
    }
}