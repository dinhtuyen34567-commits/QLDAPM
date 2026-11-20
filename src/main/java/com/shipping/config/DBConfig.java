/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: DBConfig.java
 * Lần cuối cập nhật: 25/09/2026 10:15
 * 
 * Tổng quát chức năng của lớp:
 * Quản lý và cấu hình kết nối cơ sở dữ liệu MySQL thông qua Connection Pool HikariCP, phục vụ cấp phát và tối ưu hóa kết nối cho tầng DAO.
 */
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

    /**
     * Phương thức DBConfig: dùng để khởi tạo đối tượng DBConfig với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    private DBConfig() {
        // Lop tien ich, khong tao the
    }

    /**
     * Phương thức getDataSource: dùng để cung cấp đối tượng DataSource (HikariCP) dùng chung, phục vụ việc cấp phát kết nối CSDL tối ưu cho ứng dụng.
     *
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
     * Phương thức createDataSource: dùng để khởi tạo và cấu hình các thông số cho HikariDataSource, phục vụ kết nối tới cơ sở dữ liệu MySQL.
     *
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
     * Phương thức shutdown: dùng để đóng kết nối và giải phóng toàn bộ tài nguyên của HikariDataSource, phục vụ việc tắt ứng dụng an toàn.
     *
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
