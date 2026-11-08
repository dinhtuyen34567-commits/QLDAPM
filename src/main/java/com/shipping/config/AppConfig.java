package com.shipping.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nạp cấu hình ứng dụng từ {@code application.properties}.
 *
 * <p>Cấu hình nằm ngoài mã nguồn. Mỗi thuộc tính có thể bị ghi đè bằng biến môi
 * trường cùng tên (ví dụ {@code DB_URL}, {@code DB_USER}, {@code DB_PASSWORD}),
 * nhờ vậy mật khẩu CSDL không bao giờ nằm trong file hay trong mã nguồn.</p>
 */
public final class AppConfig {

    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());

    private static final String CONFIG_RESOURCE = "/application.properties";

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (in != null) {
                PROPERTIES.load(in);
            } else {
                LOG.warning("Khong tim thay " + CONFIG_RESOURCE
                        + "; dung toan bo gia tri mac dinh va bien moi truong.");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Loi khi doc " + CONFIG_RESOURCE, e);
        }
    }

    private AppConfig() {
        // Lop tien ich, khong tao the
    }

    /**
     * Tên biến môi trường tương ứng với từng thuộc tính cấu hình.
     *
     * <p>Biến môi trường dùng tên viết HOA theo quy ước ({@code DB_URL},
     * {@code DB_USER}, {@code DB_PASSWORD}), khác với tên thuộc tính trong file
     * ({@code db.url}, {@code db.user}, {@code db.password}).</p>
     */
    private static final java.util.Map<String, String> ENV_NAMES = java.util.Map.of(
            "db.url", "DB_URL",
            "db.user", "DB_USER",
            "db.password", "DB_PASSWORD",
            "db.driver", "DB_DRIVER",
            "billing.vat-percent", "BILLING_VAT_PERCENT",
            "app.dev-login-enabled", "APP_DEV_LOGIN_ENABLED",
            "order.max-quantity-per-line", "ORDER_MAX_QUANTITY_PER_LINE",
            "order.max-lines", "ORDER_MAX_LINES",
            "order.max-total-weight-kg", "ORDER_MAX_TOTAL_WEIGHT_KG");

    /**
     * Lấy giá trị cấu hình, ưu tiên biến môi trường nếu có.
     *
     * <p>Thứ tự ưu tiên: biến môi trường tên chuẩn (ví dụ {@code DB_URL}), rồi
     * biến môi trường trùng tên thuộc tính (ví dụ {@code db.url}), cuối cùng là
     * giá trị trong file cấu hình.</p>
     *
     * @param key          tên thuộc tính
     * @param defaultValue giá trị dùng khi không cấu hình
     * @return giá trị đã cấu hình
     */
    public static String get(String key, String defaultValue) {
        String envName = ENV_NAMES.get(key);
        if (envName != null) {
            String env = System.getenv(envName);
            if (env != null && !env.isBlank()) {
                return env;
            }
        }
        // Cho phep dat bien moi truong trung ten thuoc tinh
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Lấy giá trị cấu hình dạng số nguyên.
     *
     * @param key          tên thuộc tính
     * @param defaultValue giá trị dùng khi không cấu hình hoặc không phải số
     * @return giá trị số nguyên
     */
    public static int getInt(String key, int defaultValue) {
        String raw = get(key, null);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Gia tri cau hinh khong phai so nguyen: " + key + "=" + raw);
            return defaultValue;
        }
    }

    /**
     * Lấy giá trị cấu hình dạng số thập phân.
     *
     * @param key          tên thuộc tính
     * @param defaultValue giá trị dùng khi không cấu hình hoặc không phải số
     * @return giá trị số thập phân
     */
    public static java.math.BigDecimal getBigDecimal(String key, java.math.BigDecimal defaultValue) {
        String raw = get(key, null);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return new java.math.BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Gia tri cau hinh khong phai so thap phan: " + key + "=" + raw);
            return defaultValue;
        }
    }

    /**
     * Lấy giá trị cấu hình dạng boolean.
     *
     * @param key          tên thuộc tính
     * @param defaultValue giá trị dùng khi không cấu hình
     * @return {@code true} nếu giá trị là {@code true}
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String raw = get(key, null);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw.trim());
    }
}
