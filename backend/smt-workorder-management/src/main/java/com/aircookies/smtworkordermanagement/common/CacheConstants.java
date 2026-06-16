package com.aircookies.smtworkordermanagement.common;

/**
 * 缓存常量类
 * <p>
 * 定义系统中所有缓存键的前缀和过期时间（秒），统一管理缓存策略。
 * 提供静态方法拼接具体的缓存键，确保缓存键命名规范一致。
 * </p>
 *
 * <ul>
 *   <li>缓存键前缀：用于区分不同业务模块的缓存数据</li>
 *   <li>缓存过期时间：根据业务特点设置不同的 TTL（Time To Live）
 *     <ul>
 *       <li>部门、产线、角色：24小时（86400秒），数据变更频率低</li>
 *       <li>产品：12小时（43200秒）</li>
 *       <li>用户：30分钟（1800秒），需要较快反映用户状态变更</li>
 *       <li>工单：10分钟（600秒），需要较快反映工单状态变更</li>
 *       <li>JWT令牌：24小时（86400秒），与令牌有效期一致</li>
 *       <li>统计数据：1小时（3600秒）</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class CacheConstants {

    // ==================== 缓存键前缀 ====================
    public static final String DEPT_CACHE_KEY = "dept:";
    public static final String LINE_CACHE_KEY = "line:";
    public static final String PRODUCT_CACHE_KEY = "product:";
    public static final String ROLE_CACHE_KEY = "role:";
    public static final String USER_CACHE_KEY = "user:";
    public static final String WORKORDER_CACHE_KEY = "workorder:";
    public static final String JWT_TOKEN_CACHE_KEY = "jwt:token:";
    public static final String STATS_CACHE_KEY = "stats:";

    // ==================== 缓存过期时间（秒） ====================
    public static final long DEPT_CACHE_EXPIRE = 86400;      // 24小时
    public static final long LINE_CACHE_EXPIRE = 86400;      // 24小时
    public static final long PRODUCT_CACHE_EXPIRE = 43200;   // 12小时
    public static final long ROLE_CACHE_EXPIRE = 86400;      // 24小时
    public static final long USER_CACHE_EXPIRE = 1800;       // 30分钟
    public static final long WORKORDER_CACHE_EXPIRE = 600;   // 10分钟
    public static final long JWT_TOKEN_CACHE_EXPIRE = 86400; // 24小时
    public static final long STATS_CACHE_EXPIRE = 3600;      // 1小时

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