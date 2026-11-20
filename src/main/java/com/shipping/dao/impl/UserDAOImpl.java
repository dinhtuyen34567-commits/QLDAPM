/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: UserDAOImpl.java
 * Lần cuối cập nhật: 07/11/2026 10:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực UserDAO, thực hiện truy vấn tài khoản người dùng theo tên đăng nhập hoặc mã định danh ID.
 */
package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.UserDAO;
import com.shipping.model.User;
import com.shipping.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Triển khai {@link UserDAO} bằng JDBC thuần.
 */
public class UserDAOImpl implements UserDAO {

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT id, maNguoiDung, username, password, hoTen, email, sdt "
            + "FROM tblNguoiDung WHERE username = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, maNguoiDung, username, password, hoTen, email, sdt "
            + "FROM tblNguoiDung WHERE id = ?";

    private static final String SQL_CHECK_CUSTOMER =
            "SELECT 1 FROM tblKhachHang WHERE nguoiDungId = ?";

    private static final String SQL_CHECK_STAFF =
            "SELECT 1 FROM tblNhanVien WHERE nguoiDungId = ?";

    private final ConnectionProvider connectionProvider;

    /**
     * Phương thức UserDAOImpl: dùng để khởi tạo đối tượng UserDAOImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public UserDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Phương thức findByUsername: dùng để tìm kiếm thông tin người dùng trong CSDL theo tên đăng nhập, phục vụ quy trình xác thực đăng nhập.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserWithRoles(c, rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi tìm người dùng theo username", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     */
    @Override
    public Optional<User> findById(long id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserWithRoles(c, rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi tìm người dùng theo id", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức mapUserWithRoles: dùng để thực thi thao tác mapUserWithRoles, phục vụ tiến trình xử lý nghiệp vụ của lớp UserDAOImpl.
     */
    private User mapUserWithRoles(Connection c, ResultSet rs) throws SQLException {
        long userId = rs.getLong("id");
        User user = new User();
        user.setId(userId);
        user.setMaNguoiDung(rs.getString("maNguoiDung"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setHoTen(rs.getString("hoTen"));
        user.setEmail(rs.getString("email"));
        user.setSdt(rs.getString("sdt"));

        Set<UserRole> roles = new HashSet<>();
        try (PreparedStatement psKh = c.prepareStatement(SQL_CHECK_CUSTOMER)) {
            psKh.setLong(1, userId);
            try (ResultSet rsKh = psKh.executeQuery()) {
                if (rsKh.next()) {
                    roles.add(UserRole.CUSTOMER);
                }
            }
        }

        try (PreparedStatement psNv = c.prepareStatement(SQL_CHECK_STAFF)) {
            psNv.setLong(1, userId);
            try (ResultSet rsNv = psNv.executeQuery()) {
                if (rsNv.next()) {
                    roles.add(UserRole.STAFF);
                }
            }
        }

        user.setRoles(roles);
        return user;
    }
}
