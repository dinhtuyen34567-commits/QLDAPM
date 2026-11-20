/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ConnectionProvider.java
 * Lần cuối cập nhật: 25/09/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện (Interface) trừu tượng hóa việc cung cấp kết nối JDBC Connection, phục vụ việc quản lý giao dịch và hỗ trợ viết unit test kiểm thử.
 */
package com.shipping.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Cung cấp kết nối CSDL cho các lớp khác.
 *
 * <p>Khai báo thành interface để service có thể nhận qua constructor và test
 * được bằng mock mà không cần MySQL thật (CLAUDE.md mục 6.2).</p>
 */
public interface ConnectionProvider {

    /**
     * Phương thức getConnection: dùng để lấy một kết nối JDBC (Connection) từ DataSource, phục vụ thực thi các truy vấn và giao dịch SQL.
     *
     * Mở một kết nối mới tới CSDL.
     *
     * @return kết nối, caller chịu trách nhiệm đóng lại
     * @throws SQLException nếu không kết nối được
     */
    Connection getConnection() throws SQLException;
}
