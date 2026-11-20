/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: OrderService.java
 * Lần cuối cập nhật: 08/10/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện định nghĩa các nghiệp vụ cốt lõi của UC-01: lấy thông tin người gửi, tính cước vận chuyển và tạo mới đơn hàng.
 */
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
     * Phương thức getSender: dùng để lấy thông tin chi tiết của khách hàng gửi đơn, phục vụ tự động điền dữ liệu người gửi trong UC-01.
     *
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
     * Phương thức quote: dùng để thực thi thao tác quote, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderService.
     *
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
     * Phương thức createOrder: dùng để thực hiện toàn bộ quy trình tạo đơn hàng trong một giao dịch (kiểm tra đầu vào, tính cước, lưu đơn, lưu chi tiết, lưu hóa đơn), phục vụ UC-01.
     *
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
     * Phương thức findOwnedOrder: dùng để thực thi thao tác findOwnedOrder, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderService.
     *
     * Tìm đơn hàng thuộc khách hàng đang đăng nhập theo mã vận đơn, phục vụ
     * màn hình thành công.
     *
     * @param customerId   id khách hàng đang đăng nhập
     * @param trackingCode mã vận đơn
     * @return đơn hàng nếu tồn tại và thuộc khách hàng đó
     */
    Optional<Order> findOwnedOrder(long customerId, String trackingCode);

    /**
     * Phương thức findOwnedOrderByIdempotencyKey: dùng để thực thi thao tác findOwnedOrderByIdempotencyKey, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderService.
     *
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
