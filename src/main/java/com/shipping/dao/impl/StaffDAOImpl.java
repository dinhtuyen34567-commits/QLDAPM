/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: StaffDAOImpl.java
 * Lần cuối cập nhật: 08/11/2026 09:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực StaffDAO, thực hiện truy vấn thông tin nhân viên theo mã định danh staffId từ CSDL.
 */
package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.StaffDAO;
import com.shipping.model.StaffDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Triển khai {@link StaffDAO} bằng JDBC thuần.
 */
public class StaffDAOImpl implements StaffDAO {

    private static final String SQL_FIND_BY_ID =
            "SELECT n.id, n.hoTen, nv.viTri "
            + "FROM tblNguoiDung n "
            + "INNER JOIN tblNhanVien nv ON nv.nguoiDungId = n.id "
            + "WHERE n.id = ?";

    private final ConnectionProvider connectionProvider;

    /**
     * Phương thức StaffDAOImpl: dùng để khởi tạo đối tượng StaffDAOImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public StaffDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     */
    @Override
    public Optional<StaffDTO> findById(long staffId) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StaffDTO dto = new StaffDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setHoTen(rs.getString("hoTen"));
                    dto.setViTri(rs.getString("viTri"));
                    return Optional.of(dto);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi lấy thông tin nhân viên", e);
        }
        return Optional.empty();
    }
}
