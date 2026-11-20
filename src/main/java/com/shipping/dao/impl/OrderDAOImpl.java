/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: OrderDAOImpl.java
 * Lần cuối cập nhật: 07/10/2026 16:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực OrderDAO, thực thi chèn đơn hàng, lưu chi tiết hàng hóa, lưu hóa đơn và kiểm tra chống tạo trùng đơn (Idempotency Key).
 */
package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.DuplicateKeyException;
import com.shipping.dao.OrderDAO;
import com.shipping.model.Invoice;
import com.shipping.model.Order;
import com.shipping.model.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai {@link OrderDAO} bằng JDBC thuần.
 *
 * <p>Các phương thức ghi nhận {@link Connection} từ service nên không tự
 * {@code commit} hay {@code rollback}: việc quản lý giao dịch thuộc về service
 * (BR-07).</p>
 */
public class OrderDAOImpl implements OrderDAO {

    /** SQLState của MySQL khi vi phạm ràng buộc UNIQUE. */
    private static final String SQL_STATE_DUPLICATE_KEY = "23000";

    /** Mã lỗi MySQL khi vi phạm ràng buộc UNIQUE. */
    private static final int MYSQL_ERROR_DUPLICATE_ENTRY = 1062;

    private static final String SQL_INSERT_ORDER =
            "INSERT INTO tblDonHang "
            + "(maVanDon, ngayTao, trangThai, tongKhoiLuong, khachHangId, "
            + " tenNguoiNhan, sdtNguoiNhan, diaChiGiao, chuyenXeId, idempotencyKey) "
            /**
             * Phương thức VALUES: dùng để thực thi thao tác VALUES, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
             */
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_DETAIL =
            /**
             * Phương thức tblChiTietDonHang: dùng để thực thi thao tác tblChiTietDonHang, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
             */
            "INSERT INTO tblChiTietDonHang (donHangId, hangHoaId, soLuong, donGia) "
            /**
             * Phương thức VALUES: dùng để thực thi thao tác VALUES, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
             */
            + "VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_INVOICE =
            "INSERT INTO tblHoaDon "
            + "(maHoaDon, donHangId, tongTien, vat, phuongThucThanhToan) "
            /**
             * Phương thức VALUES: dùng để thực thi thao tác VALUES, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
             */
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_IDEMPOTENCY_KEY =
            "SELECT id, maVanDon, ngayTao, trangThai, tongKhoiLuong, khachHangId, "
            + "tenNguoiNhan, sdtNguoiNhan, diaChiGiao, chuyenXeId, idempotencyKey "
            + "FROM tblDonHang WHERE khachHangId = ? AND idempotencyKey = ?";

    private static final String SQL_FIND_BY_TRACKING_CODE =
            "SELECT id, maVanDon, ngayTao, trangThai, tongKhoiLuong, khachHangId, "
            + "tenNguoiNhan, sdtNguoiNhan, diaChiGiao, chuyenXeId, idempotencyKey "
            + "FROM tblDonHang WHERE khachHangId = ? AND maVanDon = ?";

    private static final String SQL_FIND_INVOICE_TOTAL =
            "SELECT tongTien FROM tblHoaDon WHERE donHangId = ?";

    private static final String SQL_INVOICE_CODE_EXISTS =
            "SELECT 1 FROM tblHoaDon WHERE maHoaDon = ? LIMIT 1";

    private final ConnectionProvider connectionProvider;

    /**
     * Phương thức OrderDAOImpl: dùng để khởi tạo đối tượng OrderDAOImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public OrderDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Phương thức insertOrder: dùng để chèn thông tin đơn hàng mới vào bảng don_hang trong CSDL, phục vụ bước lưu đơn hàng trong UC-01.
     */
    @Override
    public long insertOrder(Connection c, Order o) {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERT_ORDER,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getTrackingCode());
            ps.setTimestamp(2, Timestamp.valueOf(
                    o.getCreatedAt() == null ? LocalDateTime.now() : o.getCreatedAt()));
            ps.setString(3, o.getStatus());
            ps.setBigDecimal(4, o.getTotalWeight());
            ps.setInt(5, o.getCustomerId());
            ps.setString(6, o.getReceiverName());
            ps.setString(7, o.getReceiverPhone());
            ps.setString(8, o.getDeliveryAddress());
            if (o.getTripId() == null) {
                ps.setNull(9, Types.INTEGER);
            } else {
                ps.setInt(9, o.getTripId());
            }
            if (o.getIdempotencyKey() == null) {
                ps.setNull(10, Types.VARCHAR);
            } else {
                ps.setString(10, o.getIdempotencyKey());
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new SQLException("Khong lay duoc id don hang vua tao");
        } catch (SQLException e) {
            throw translateDuplicate(e, "Khong them duoc don hang");
        }
    }

    /**
     * Phương thức insertDetails: dùng để chèn danh sách chi tiết mặt hàng vào bảng chi_tiet_don_hang theo lô (batch), phục vụ lưu thông tin hàng hóa trong UC-01.
     */
    @Override
    public void insertDetails(Connection c, long orderId, List<OrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERT_DETAIL)) {
            for (OrderDetail d : details) {
                ps.setLong(1, orderId);
                ps.setInt(2, d.getProductId());
                ps.setInt(3, d.getQuantity());
                ps.setBigDecimal(4, d.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw translateDuplicate(e, "Khong them duoc chi tiet don hang");
        }
    }

    /**
     * Phương thức insertInvoice: dùng để chèn thông tin hóa đơn cước vận chuyển vào bảng hoa_don trong CSDL, phục vụ bước lập hóa đơn trong UC-01.
     */
    @Override
    public long insertInvoice(Connection c, Invoice i) {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERT_INVOICE,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getCode());
            ps.setLong(2, i.getOrderId());
            ps.setBigDecimal(3, i.getTotalAmount());
            ps.setBigDecimal(4, i.getVatPercent());
            ps.setString(5, i.getPaymentMethod());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new SQLException("Khong lay duoc id hoa don vua tao");
        } catch (SQLException e) {
            throw translateDuplicate(e, "Khong them duoc hoa don");
        }
    }

    /**
     * Phương thức findByIdempotencyKey: dùng để tìm kiếm đơn hàng theo khóa chống tạo trùng (Idempotency Key), phục vụ đảm bảo tính idempotent của API tạo đơn hàng (BR-13).
     */
    @Override
    public Optional<Order> findByIdempotencyKey(long customerId, String key) {
        if (key == null) {
            return Optional.empty();
        }
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_IDEMPOTENCY_KEY)) {
            ps.setLong(1, customerId);
            ps.setString(2, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi tim don hang theo idempotencyKey", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức findByTrackingCode: dùng để tìm kiếm thông tin đơn hàng theo mã vận đơn và mã khách hàng, phục vụ việc tra cứu và đối soát đơn hàng.
     */
    @Override
    public Optional<Order> findByTrackingCode(long customerId, String trackingCode) {
        if (trackingCode == null) {
            return Optional.empty();
        }
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_TRACKING_CODE)) {
            ps.setLong(1, customerId);
            ps.setString(2, trackingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi tim don hang theo ma van don", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức findInvoiceTotalByOrderId: dùng để truy vấn tổng tiền hóa đơn tương ứng với mã đơn hàng, phục vụ kiểm tra và hiển thị thanh toán.
     */
    @Override
    public Optional<BigDecimal> findInvoiceTotalByOrderId(long orderId) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_INVOICE_TOTAL)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("tongTien");
                    return Optional.ofNullable(total);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi lay tong tien hoa don", e);
        }
        return Optional.empty();
    }

    /**
     * Phương thức invoiceCodeExists: dùng để kiểm tra sự tồn tại của mã hóa đơn trong CSDL, phục vụ cơ chế chống trùng khóa UNIQUE khi sinh mã hóa đơn.
     */
    @Override
    public boolean invoiceCodeExists(String invoiceCode) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INVOICE_CODE_EXISTS)) {
            ps.setString(1, invoiceCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi kiem tra ma hoa don", e);
        }
    }

    /**
     * Phương thức translateDuplicate: dùng để thực thi thao tác translateDuplicate, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
     *
     * Chuyển lỗi SQL thành {@link DuplicateKeyException} nếu là vi phạm UNIQUE,
     * ngược lại giữ nguyên dạng lỗi hệ thống.
     *
     * @param e       lỗi SQL gốc
     * @param message thông báo ngữ cảnh
     * @return exception phù hợp để ném ra
     */
    private RuntimeException translateDuplicate(SQLException e, String message) {
        boolean duplicateByState = e.getSQLState() != null
                && SQL_STATE_DUPLICATE_KEY.equals(e.getSQLState());
        boolean duplicateByCode = e.getErrorCode() == MYSQL_ERROR_DUPLICATE_ENTRY;
        if (duplicateByState || duplicateByCode) {
            return new DuplicateKeyException(message, extractConstraintName(e), e);
        }
        return new IllegalStateException(message, e);
    }

    /**
     * Phương thức extractConstraintName: dùng để thực thi thao tác extractConstraintName, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
     *
     * Trích xuất tên ràng buộc UNIQUE bị vi phạm từ thông báo lỗi MySQL.
     *
     * <p>MySQL báo theo dạng {@code Duplicate entry '...' for key 'tblDonHang.maVanDon'}.
     * Với ràng buộc nhiều cột {@code UNIQUE (khachHangId, idempotencyKey)} MySQL chỉ
     * nêu cột đầu tiên, nên tên ràng buộc lấy được là {@code khachHangId}.</p>
     *
     * @param e lỗi SQL gốc
     * @return tên ràng buộc, hoặc {@code null} nếu không trích được
     */
    private String extractConstraintName(SQLException e) {
        String msg = e.getMessage();
        if (msg == null) {
            return null;
        }
        int idx = msg.lastIndexOf("for key '");
        if (idx < 0) {
            return null;
        }
        int start = idx + "for key '".length();
        int end = msg.indexOf('\'', start);
        if (end < 0) {
            return null;
        }
        return msg.substring(start, end);
    }

    /**
     * Phương thức mapRow: dùng để thực thi thao tác mapRow, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderDAOImpl.
     *
     * Chuyển một dòng kết quả truy vấn sang đối tượng {@link Order}.
     */
    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setTrackingCode(rs.getString("maVanDon"));
        Timestamp created = rs.getTimestamp("ngayTao");
        if (created != null) {
            o.setCreatedAt(created.toLocalDateTime());
        }
        o.setStatus(rs.getString("trangThai"));
        o.setTotalWeight(rs.getBigDecimal("tongKhoiLuong"));
        o.setCustomerId(rs.getInt("khachHangId"));
        o.setReceiverName(rs.getString("tenNguoiNhan"));
        o.setReceiverPhone(rs.getString("sdtNguoiNhan"));
        o.setDeliveryAddress(rs.getString("diaChiGiao"));
        int tripId = rs.getInt("chuyenXeId");
        o.setTripId(rs.wasNull() ? null : tripId);
        o.setIdempotencyKey(rs.getString("idempotencyKey"));
        return o;
    }
}
