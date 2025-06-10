package com.reward.common.constant;

/**
 * 公共常量定义
 */
public class CommonConstants {

    /**
     * 响应码常量
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_ERROR = 500;
    }

    /**
     * 性别常量
     */
    public static class Gender {
        public static final int MALE = 1;    // 男
        public static final int FEMALE = 2;  // 女
    }

    /**
     * 观众标签常量
     */
    public static class AudienceTag {
        public static final String HIGH = "high";     // 高消费人群
        public static final String MEDIUM = "medium"; // 中消费人群
        public static final String LOW = "low";       // 低消费人群
        public static final String UNKNOWN = "unknown"; // 未知
    }

    /**
     * 缓存KEY常量
     */
    public static class CacheKey {
        public static final String AUDIENCE_PROFILE = "audience:profile:";
        public static final String ANCHOR_TOP10 = "anchor:top10:";
        public static final String ANCHOR_BALANCE = "anchor:balance:";
        public static final String ANCHOR_COMMISSION = "anchor:commission:";
        public static final String AUDIENCE_TAG = "audience:tag:";
    }

    /**
     * 服务端口常量
     */
    public static class ServicePort {
        public static final int AUDIENCE_SERVICE_1 = 8091;
        public static final int AUDIENCE_SERVICE_2 = 8092;
        public static final int FINANCE_SERVICE_1 = 8093;
        public static final int FINANCE_SERVICE_2 = 8094;
        public static final int ANALYTICS_SERVICE_1 = 8095;
        public static final int ANALYTICS_SERVICE_2 = 8096;
        public static final int SIMULATION_SERVICE_1 = 8081;
        public static final int SIMULATION_SERVICE_2 = 8082;
    }
}