package com.shipping.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cấu hình pool kết nối HikariCP.
 *
 * <p>Thông tin đăng nhập CSDL lấy từ {@link AppConfig}, tức là từ file cấu hình
 * hoặc biến môi trường; không hardcode mật khẩu trong mã nguồn (CLAUDE.md mục 11).</p>
 */
public final class DBConfig {

    private static final Logger LOG = Logger.getLogger(DBConfig.class.getName());

    private static volatile HikariDataSource dataSource;

    private DBConfig() {
        // Lop tien ich, khong tao the
    }

    /**
     * Lấy data source dùng chung, khởi tạo lazily.
     *
     * @return data source đã sẵn sàng
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    /**
     * Tạo data source mới từ cấu hình.
     *
     * @return pool kết nối HikariCP
     */
    private static HikariDataSource createDataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(AppConfig.get("db.url", "jdbc:mysql://localhost:3306/vanchuyen"
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
                + "&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"));
        cfg.setUsername(AppConfig.get("db.user", "root"));
        cfg.setPassword(AppConfig.get("db.password", ""));
        cfg.setDriverClassName(AppConfig.get("db.driver", "com.mysql.cj.jdbc.Driver"));

        cfg.setMaximumPoolSize(AppConfig.getInt("db.pool.maxSize", 10));
        cfg.setMinimumIdle(AppConfig.getInt("db.pool.minIdle", 2));
        cfg.setConnectionTimeout(AppConfig.getInt("db.pool.connectionTimeout", 10000));
        cfg.setPoolName("ShippingOrderPool");

        // Chuan duong danh tieng Viet qua connection (keo theo db.url)
        cfg.addDataSourceProperty("characterEncoding", "UTF-8");

        LOG.info("Khoi tao HikariCP: " + cfg.getPoolName());
        return new HikariDataSource(cfg);
    }

    /**
     * Đóng pool kết nối, dùng khi ứng dụng tắt.
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                dataSource.close();
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "Loi khi dong pool ket noi", e);
            }
        }
        dataSource = null;
    }
}
