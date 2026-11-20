/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequestDAO.java
 * Lần cuối cập nhật: 08/11/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện khai báo các thao tác CSDL liên quan đến tiếp nhận, truy vấn phân trang và cập nhật trạng thái khiếu nại.
 */
package com.shipping.dao;

import com.shipping.model.SupportDetailDTO;
import com.shipping.model.SupportListItemDTO;
import com.shipping.model.SupportRequest;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Interface truy cập dữ liệu bảng {@code tblYeuCauHoTro}.
 */
public interface SupportRequestDAO {

    /**
     * Phương thức insert: dùng để chèn bản ghi mới vào CSDL trong một giao dịch cụ thể, phục vụ việc lưu trữ dữ liệu an toàn.
     *
     * Thêm mới một yêu cầu hỗ trợ (gắn với mã vận đơn của khách hàng).
     *
     * @param conn kết nối CSDL (nếu null thì tự lấy từ ConnectionProvider)
     * @param req  thông tin yêu cầu cần tạo
     * @return ID bản ghi vừa tạo
     */
    long insert(Connection conn, SupportRequest req);

    /**
     * Phương thức findOrderIdByTrackingCode: dùng để truy vấn mã đơn hàng (orderId) từ mã vận đơn thuộc quyền sở hữu của khách hàng, phục vụ liên kết khiếu nại với đơn hàng.
     *
     * Tìm ID đơn hàng thuộc quyền sở hữu của khách hàng theo mã vận đơn.
     *
     * @param trackingCode mã vận đơn
     * @param customerId   ID khách hàng
     * @return ID đơn hàng nếu tồn tại và đúng sở hữu
     */
    Optional<Long> findOrderIdByTrackingCode(String trackingCode, long customerId);

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     *
     * Tìm chi tiết yêu cầu hỗ trợ theo ID (kèm thông tin khách hàng và chi tiết đơn hàng).
     *
     * @param id ID yêu cầu
     * @return chi tiết yêu cầu nếu tìm thấy
     */
    Optional<SupportDetailDTO> findById(long id);

    /**
     * Phương thức findByPage: dùng để truy vấn danh sách bản ghi có phân trang và lọc theo trạng thái, phục vụ hiển thị danh sách trên giao diện người dùng.
     *
     * Lấy danh sách yêu cầu hỗ trợ phân trang, lọc theo trạng thái, sắp xếp giảm dần theo ID.
     *
     * @param status trạng thái cần lọc (có thể null hoặc rỗng để lấy tất cả)
     * @param offset vị trí bắt đầu
     * @param limit  số bản ghi tối đa
     * @return danh sách yêu cầu
     */
    List<SupportListItemDTO> findByPage(String status, int offset, int limit);

    /**
     * Phương thức count: dùng để đếm tổng số lượng bản ghi thỏa mãn điều kiện lọc trạng thái, phục vụ tính toán số trang trong phân trang dữ liệu.
     *
     * Đếm tổng số yêu cầu hỗ trợ theo điều kiện lọc trạng thái.
     *
     * @param status trạng thái cần lọc (có thể null hoặc rỗng)
     * @return tổng số bản ghi
     */
    int count(String status);

    /**
     * Phương thức updateWithCondition: dùng để cập nhật trạng thái, phản hồi và nhân viên phụ trách cho yêu cầu khiếu nại theo điều kiện nhất quán, phục vụ quy trình xử lý UC-03.
     *
     * Cập nhật trạng thái và phản hồi của yêu cầu với điều kiện kiểm soát tương tranh (BR-25).
     *
     * @param id        ID yêu cầu
     * @param newStatus trạng thái mới
     * @param reply     nội dung phản hồi
     * @param staffId   ID nhân viên đang thao tác
     * @return {@code true} nếu có đúng 1 dòng được cập nhật thành công, {@code false} nếu vi phạm điều kiện
     */
    boolean updateWithCondition(long id, String newStatus, String reply, long staffId);
}
