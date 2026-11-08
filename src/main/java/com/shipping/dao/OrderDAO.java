package com.shipping.dao;

import com.shipping.model.Invoice;
import com.shipping.model.Order;
import com.shipping.model.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Truy cập dữ liệu đơn hàng, chi tiết đơn hàng và hóa đơn.
 *
 * <p>Các phương thức ghi nhận {@link Connection} do service quản lý, nhờ vậy
 * ba lần ghi (đơn hàng, chi tiết, hóa đơn) nằm chung một giao dịch (BR-07).</p>
 */
public interface OrderDAO {

    /**
     * Thêm một đơn hàng mới.
     *
     * @param c kết nối đang trong giao dịch
     * @param o đơn hàng cần lưu
     * @return id của bản ghi vừa tạo
     * @throws DuplicateKeyException nếu trùng {@code maVanDon} hoặc
     *                               {@code (khachHangId, idempotencyKey)}
     */
    long insertOrder(Connection c, Order o);

    /**
     * Thêm hàng loạt chi tiết đơn hàng.
     *
     * @param c       kết nối đang trong giao dịch
     * @param orderId id của đơn hàng
     * @param details danh sách dòng chi tiết
     * @throws DuplicateKeyException nếu trùng khóa chính kép {@code (donHangId, hangHoaId)}
     */
    void insertDetails(Connection c, long orderId, List<OrderDetail> details);

    /**
     * Thêm một hóa đơn mới.
     *
     * @param c kết nối đang trong giao dịch
     * @param i hóa đơn cần lưu
     * @return id của bản ghi vừa tạo
     * @throws DuplicateKeyException nếu trùng {@code maHoaDon} hoặc {@code donHangId}
     */
    long insertInvoice(Connection c, Invoice i);

    /**
     * Tìm đơn hàng theo khóa chống tạo trùng của một khách hàng (BR-13).
     *
     * @param customerId id khách hàng
     * @param key        khóa idempotency
     * @return đơn hàng đã tồn tại, hoặc {@link Optional#empty()}
     */
    Optional<Order> findByIdempotencyKey(long customerId, String key);

    /**
     * Tìm đơn hàng theo mã vận đơn, chỉ trong phạm vi khách hàng đang đăng nhập.
     *
     * <p>Lọc theo {@code khachHangId} ngay trong truy vấn để không lộ tồn tại
     * của đơn thuộc khách hàng khác (BR-15).</p>
     *
     * @param customerId  id khách hàng
     * @param trackingCode mã vận đơn
     * @return đơn hàng, hoặc {@link Optional#empty()}
     */
    Optional<Order> findByTrackingCode(long customerId, String trackingCode);

    /**
     * Lấy tổng tiền của hóa đơn theo id đơn hàng.
     *
     * <p>Dùng khi client gửi lại cùng khóa idempotency và cần trả về tổng tiền
     * của đơn đã có (BR-13).</p>
     *
     * @param orderId id đơn hàng
     * @return tổng tiền đã gồm VAT, hoặc {@link Optional#empty()} nếu chưa có hóa đơn
     */
    Optional<BigDecimal> findInvoiceTotalByOrderId(long orderId);

    /**
     * Kiểm tra mã hóa đơn đã tồn tại hay chưa.
     *
     * <p>Phục vụ cơ chế thử lại sinh mã hóa đơn khi trùng khóa UNIQUE
     * (quy ước mã hóa đơn, cùng cơ chế BR-08).</p>
     *
     * @param invoiceCode mã hóa đơn cần kiểm tra
     * @return {@code true} nếu đã tồn tại
     */
    boolean invoiceCodeExists(String invoiceCode);
}
