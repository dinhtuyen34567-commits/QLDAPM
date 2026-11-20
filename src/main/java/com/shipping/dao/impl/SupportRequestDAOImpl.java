/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequestDAOImpl.java
 * Lần cuối cập nhật: 09/11/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực SupportRequestDAO, thực hiện thêm mới khiếu nại, truy vấn chi tiết, phân trang theo trạng thái và cập nhật xử lý.
 */
package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.DuplicateKeyException;
import com.shipping.dao.SupportRequestDAO;
import com.shipping.model.SupportDetailDTO;
import com.shipping.model.SupportListItemDTO;
import com.shipping.model.SupportRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai {@link SupportRequestDAO} bằng JDBC thuần.
 */
public class SupportRequestDAOImpl implements SupportRequestDAO {

    private static final String SQL_INSERT =
            /**
             * Phương thức tblYeuCauHoTro: dùng để thực thi thao tác tblYeuCauHoTro, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestDAOImpl.
             */
            "INSERT INTO tblYeuCauHoTro (maYeuCau, noiDung, trangThai, phanHoi, khachHangId, donHangId, nhanVienId) "
            /**
             * Phương thức VALUES: dùng để thực thi thao tác VALUES, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestDAOImpl.
             */
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_ORDER_ID =
            "SELECT id FROM tblDonHang WHERE maVanDon = ? AND khachHangId = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT y.id, y.maYeuCau, y.noiDung, y.trangThai, y.phanHoi, y.nhanVienId, "
            + "       nvUser.hoTen AS tenNhanVien, "
            + "       khUser.id AS khId, khUser.hoTen AS khHoTen, khUser.sdt AS khSdt, khUser.email AS khEmail, "
            + "       d.id AS donHangId, d.maVanDon, d.trangThai AS trangThaiDon, d.tenNguoiNhan, d.sdtNguoiNhan, d.diaChiGiao, d.tongKhoiLuong "
            + "FROM tblYeuCauHoTro y "
            + "INNER JOIN tblNguoiDung khUser ON khUser.id = y.khachHangId "
            + "LEFT JOIN tblNguoiDung nvUser ON nvUser.id = y.nhanVienId "
            + "INNER JOIN tblDonHang d ON d.id = y.donHangId "
            + "WHERE y.id = ?";

    private static final String SQL_FIND_ORDER_ITEMS =
            "SELECT h.tenHang, c.soLuong, c.donGia "
            + "FROM tblChiTietDonHang c "
            + "INNER JOIN tblHangHoa h ON h.id = c.hangHoaId "
            + "WHERE c.donHangId = ?";

    private final ConnectionProvider connectionProvider;

    /**
     * Phương thức SupportRequestDAOImpl: dùng để khởi tạo đối tượng SupportRequestDAOImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportRequestDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Phương thức insert: dùng để chèn bản ghi mới vào CSDL trong một giao dịch cụ thể, phục vụ việc lưu trữ dữ liệu an toàn.
     */
    @Override
    public long insert(Connection conn, SupportRequest req) {
        boolean manageConnection = (conn == null);
        Connection c = null;
        try {
            c = manageConnection ? connectionProvider.getConnection() : conn;
            try (PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, req.getMaYeuCau());
                ps.setString(2, req.getNoiDung());
                ps.setString(3, req.getTrangThai());
                ps.setString(4, req.getPhanHoi());
                ps.setLong(5, req.getKhachHangId());
                ps.setLong(6, req.getDonHangId());
                if (req.getNhanVienId() != null) {
                    ps.setLong(7, req.getNhanVienId());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        long generatedId = keys.getLong(1);
                        req.setId(generatedId);
                        return generatedId;
                    }
                }
                throw new SQLException("Không lấy được ID yêu cầu vừa tạo");
            }
        } catch (SQLException e) {
            throw translateDuplicate(e, "Không thể lưu yêu cầu hỗ trợ");
        } finally {
            if (manageConnection && c != null) {
                try {
                    c.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Phương thức findOrderIdByTrackingCode: dùng để truy vấn mã đơn hàng (orderId) từ mã vận đơn thuộc quyền sở hữu của khách hàng, phục vụ liên kết khiếu nại với đơn hàng.
     */
    @Override
    public Optional<Long> findOrderIdByTrackingCode(String trackingCode, long customerId) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ORDER_ID)) {
            ps.setString(1, trackingCode);
            ps.setLong(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi tìm đơn hàng theo mã vận đơn", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     */
    @Override
    public Optional<SupportDetailDTO> findById(long id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                SupportDetailDTO dto = new SupportDetailDTO();
                dto.setId(rs.getLong("id"));
                dto.setMaYeuCau(rs.getString("maYeuCau"));
                dto.setNoiDung(rs.getString("noiDung"));
                dto.setTrangThai(rs.getString("trangThai"));
                dto.setPhanHoi(rs.getString("phanHoi"));
                long staffId = rs.getLong("nhanVienId");
                dto.setNhanVienId(rs.wasNull() ? null : staffId);
                dto.setTenNhanVien(rs.getString("tenNhanVien"));

                SupportDetailDTO.CustomerInfo khach = new SupportDetailDTO.CustomerInfo(
                        rs.getLong("khId"),
                        rs.getString("khHoTen"),
                        rs.getString("khSdt"),
                        rs.getString("khEmail")
                );
                dto.setKhachHang(khach);

                long orderId = rs.getLong("donHangId");
                List<SupportDetailDTO.OrderItemInfo> items = new ArrayList<>();
                try (PreparedStatement psItems = c.prepareStatement(SQL_FIND_ORDER_ITEMS)) {
                    psItems.setLong(1, orderId);
                    try (ResultSet rsItems = psItems.executeQuery()) {
                        while (rsItems.next()) {
                            items.add(new SupportDetailDTO.OrderItemInfo(
                                    rsItems.getString("tenHang"),
                                    rsItems.getInt("soLuong"),
                                    rsItems.getBigDecimal("donGia")
                            ));
                        }
                    }
                }

                SupportDetailDTO.OrderInfo donHang = new SupportDetailDTO.OrderInfo(
                        orderId,
                        rs.getString("maVanDon"),
                        rs.getString("trangThaiDon"),
                        rs.getString("tenNguoiNhan"),
                        rs.getString("sdtNguoiNhan"),
                        rs.getString("diaChiGiao"),
                        rs.getBigDecimal("tongKhoiLuong"),
                        items
                );
                dto.setDonHang(donHang);

                return Optional.of(dto);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi tìm chi tiết yêu cầu hỗ trợ", e);
        }
    }

    /**
     * Phương thức findByPage: dùng để truy vấn danh sách bản ghi có phân trang và lọc theo trạng thái, phục vụ hiển thị danh sách trên giao diện người dùng.
     */
    @Override
    public List<SupportListItemDTO> findByPage(String status, int offset, int limit) {
        StringBuilder sql = new StringBuilder(
                "SELECT y.id, y.maYeuCau, y.trangThai, d.maVanDon, khUser.hoTen AS hoTenKhachHang "
                + "FROM tblYeuCauHoTro y "
                + "INNER JOIN tblDonHang d ON d.id = y.donHangId "
                + "INNER JOIN tblNguoiDung khUser ON khUser.id = y.khachHangId "
        );

        boolean hasStatus = (status != null && !status.trim().isEmpty());
        if (hasStatus) {
            sql.append("WHERE y.trangThai = ? ");
        }
        sql.append("ORDER BY y.id DESC LIMIT ? OFFSET ?");

        List<SupportListItemDTO> list = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (hasStatus) {
                ps.setString(paramIdx++, status.trim());
            }
            ps.setInt(paramIdx++, limit);
            ps.setInt(paramIdx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupportListItemDTO(
                            rs.getLong("id"),
                            rs.getString("maYeuCau"),
                            rs.getString("trangThai"),
                            rs.getString("maVanDon"),
                            rs.getString("hoTenKhachHang"),
                            ""
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi lấy danh sách yêu cầu hỗ trợ", e);
        }
        return list;
    }

    /**
     * Phương thức count: dùng để đếm tổng số lượng bản ghi thỏa mãn điều kiện lọc trạng thái, phục vụ tính toán số trang trong phân trang dữ liệu.
     */
    @Override
    public int count(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tblYeuCauHoTro ");
        boolean hasStatus = (status != null && !status.trim().isEmpty());
        if (hasStatus) {
            sql.append("WHERE trangThai = ?");
        }

        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            if (hasStatus) {
                ps.setString(1, status.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi đếm số yêu cầu hỗ trợ", e);
        }
        return 0;
    }

    /**
     * Phương thức updateWithCondition: dùng để cập nhật trạng thái, phản hồi và nhân viên phụ trách cho yêu cầu khiếu nại theo điều kiện nhất quán, phục vụ quy trình xử lý UC-03.
     */
    @Override
    public boolean updateWithCondition(long id, String newStatus, String reply, long staffId) {
        String sql;
        if ("DANG_XU_LY".equals(newStatus)) {
            // BR-25: Nhân viên đầu tiên nhận yêu cầu MOI (hoặc chính nhân viên đó đã nhận trước đó cập nhật tiếp)
            sql = "UPDATE tblYeuCauHoTro SET trangThai = ?, phanHoi = ?, nhanVienId = ? "
                    + "WHERE id = ? AND (trangThai = 'MOI' OR nhanVienId = ?)";
        } else if ("DA_DONG".equals(newStatus)) {
            // BR-24 & BR-25: Chỉ nhân viên phụ trách mới được đóng yêu cầu đang ở DANG_XU_LY
            sql = "UPDATE tblYeuCauHoTro SET trangThai = ?, phanHoi = ? "
                    + "WHERE id = ? AND trangThai = 'DANG_XU_LY' AND nhanVienId = ?";
        } else {
            return false;
        }

        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if ("DANG_XU_LY".equals(newStatus)) {
                ps.setString(1, newStatus);
                ps.setString(2, reply);
                ps.setLong(3, staffId);
                ps.setLong(4, id);
                ps.setLong(5, staffId);
            } else {
                ps.setString(1, newStatus);
                ps.setString(2, reply);
                ps.setLong(3, id);
                ps.setLong(4, staffId);
            }
            int updated = ps.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Lỗi khi cập nhật yêu cầu hỗ trợ", e);
        }
    }

    /**
     * Phương thức translateDuplicate: dùng để thực thi thao tác translateDuplicate, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestDAOImpl.
     */
    private RuntimeException translateDuplicate(SQLException e, String message) {
        if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
            return new DuplicateKeyException(message, "maYeuCau", e);
        }
        return new IllegalStateException(message, e);
    }
}
