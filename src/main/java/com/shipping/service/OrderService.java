package com.shipping.service;

import com.shipping.model.CreateOrderRequest;
import com.shipping.model.CreateResult;
import com.shipping.model.Customer;
import com.shipping.model.ItemRequest;
import com.shipping.model.Order;
import com.shipping.model.Quote;

import java.util.List;
import java.util.Optional;

/**
 * Nghiệp vụ UC-01: tạo đơn hàng vận chuyển.
 *
 * <p>Mọi quy tắc nghiệp vụ và toàn bộ quản lý giao dịch nằm ở tầng này.
 * Controller chỉ gọi xuống service và ánh xạ lỗi ra HTTP.</p>
 */
public interface OrderService {

    /**
     * Lấy thông tin người gửi để điền sẵn và khóa hai ô [Tên người gửi],
     * [SĐT người gửi] (bước 2 của kịch bản).
     *
     * <p>Tương ứng hàm {@code getThongTinKH()} trong tài liệu thiết kế.</p>
     *
     * @param customerId id khách hàng đang đăng nhập
     * @return thông tin khách hàng
     * @throws com.shipping.exception.AppException nếu không tìm thấy khách hàng
     */
    Customer getSender(long customerId);

    /**
     * Tính tổng khối lượng và tổng tiền của đơn hàng.
     *
     * <p>Tương ứng các hàm {@code tinhTongKhoiLuong()} và {@code tinhTongTien()}
     * trong tài liệu thiết kế (mục 3.4).</p>
     *
     * @param items danh sách mặt hàng
     * @return kết quả tính cước
     * @throws com.shipping.exception.AppException theo thứ tự kiểm tra ở mục 5.3
     */
    Quote quote(List<ItemRequest> items);

    /**
     * Tạo đơn hàng mới gồm đơn hàng, chi tiết đơn hàng và hóa đơn, tất cả
     * trong một giao dịch (BR-07).
     *
     * <p>Tương ứng các hàm {@code taoDonHangMoi()}, {@code luuChiTiet()} và
     * {@code taoHoaDonMoi()} trong tài liệu thiết kế.</p>
     *
     * @param customerId     id khách hàng đang đăng nhập, lấy từ phiên (BR-01)
     * @param req            thông tin đơn hàng do client gửi
     * @param idempotencyKey khóa chống tạo trùng, có thể null
     * @return kết quả tạo đơn gồm mã vận đơn
     * @throws com.shipping.exception.AppException theo thứ tự kiểm tra ở mục 5.3
     */
    CreateResult createOrder(long customerId, CreateOrderRequest req, String idempotencyKey);

    /**
     * Tìm đơn hàng thuộc khách hàng đang đăng nhập theo mã vận đơn, phục vụ
     * màn hình thành công.
     *
     * @param customerId   id khách hàng đang đăng nhập
     * @param trackingCode mã vận đơn
     * @return đơn hàng nếu tồn tại và thuộc khách hàng đó
     */
    Optional<Order> findOwnedOrder(long customerId, String trackingCode);

    /**
     * Tìm đơn hàng đã tồn tại theo khóa chống tạo trùng của khách hàng.
     *
     * <p>Dùng để phân biệt HTTP 201 (tạo mới) và HTTP 200 (gửi lại cùng khóa)
     * theo BR-13.</p>
     *
     * @param customerId     id khách hàng đang đăng nhập
     * @param idempotencyKey khóa idempotency
     * @return đơn hàng đã tồn tại, hoặc {@link Optional#empty()}
     */
    Optional<Order> findOwnedOrderByIdempotencyKey(long customerId, String idempotencyKey);
}
