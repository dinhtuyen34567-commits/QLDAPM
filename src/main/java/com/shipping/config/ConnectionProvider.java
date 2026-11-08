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
     * Mở một kết nối mới tới CSDL.
     *
     * @return kết nối, caller chịu trách nhiệm đóng lại
     * @throws SQLException nếu không kết nối được
     */
    Connection getConnection() throws SQLException;
}
