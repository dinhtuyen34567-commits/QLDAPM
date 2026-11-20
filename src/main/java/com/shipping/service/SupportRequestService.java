/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequestService.java
 * Lần cuối cập nhật: 10/11/2026 15:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện định nghĩa nghiệp vụ quản lý khiếu nại: tạo khiếu nại mới, lấy chi tiết, phân trang danh sách và cập nhật xử lý.
 */
package com.shipping.service;

import com.shipping.model.CreateSupportRequest;
import com.shipping.model.StaffDTO;
import com.shipping.model.SupportDetailDTO;
import com.shipping.model.SupportPageResult;
import com.shipping.model.SupportRequest;
import com.shipping.model.UpdateSupportRequest;

/**
 * Dịch vụ xử lý nghiệp vụ UC-03: Quản lý thông tin và khiếu nại.
 */
public interface SupportRequestService {

    /**
     * Phương thức createRequest: dùng để thực thi thao tác createRequest, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestService.
     *
     * Khách hàng tạo yêu cầu hỗ trợ / khiếu nại (API 6 & BR-20, BR-21).
     *
     * @param customerId ID khách hàng gửi yêu cầu
     * @param req        thông tin khiếu nại
     * @return bản ghi yêu cầu hỗ trợ vừa tạo
     */
    SupportRequest createRequest(long customerId, CreateSupportRequest req);

    /**
     * Phương thức getStaffMe: dùng để lấy giá trị của thuộc tính StaffMe, phục vụ việc truy xuất dữ liệu của đối tượng.
     *
     * Lấy thông tin nhân viên đang trực (API 7).
     *
     * @param staffId ID nhân viên
     * @return DTO thông tin nhân viên
     */
    StaffDTO getStaffMe(long staffId);

    /**
     * Phương thức getSupportList: dùng để lấy giá trị của thuộc tính SupportList, phục vụ việc truy xuất dữ liệu của đối tượng.
     *
     * Nhân viên lấy danh sách yêu cầu phân trang và lọc theo trạng thái (API 8 & BR-23).
     *
     * @param staffId ID nhân viên thao tác
     * @param status  trạng thái cần lọc (có thể null)
     * @param page    trang hiện tại (1-based)
     * @return kết quả phân trang
     */
    SupportPageResult getSupportList(long staffId, String status, int page);

    /**
     * Phương thức getSupportDetail: dùng để lấy thông tin chi tiết đầy đủ của một yêu cầu khiếu nại kèm thông tin đơn hàng và khách hàng, phục vụ UC-03.
     *
     * Nhân viên xem chi tiết yêu cầu hỗ trợ (API 9 & BR-27).
     *
     * @param staffId   ID nhân viên
     * @param requestId ID yêu cầu
     * @return chi tiết đầy đủ 3 khối thông tin
     */
    SupportDetailDTO getSupportDetail(long staffId, long requestId);

    /**
     * Phương thức updateSupportRequest: dùng để kiểm tra điều kiện chuyển trạng thái và cập nhật nội dung xử lý/phản hồi yêu cầu khiếu nại của nhân viên CSKH, phục vụ UC-03.
     *
     * Nhân viên cập nhật phản hồi, tiếp nhận hoặc đóng yêu cầu (API 10 & BR-24, BR-25, BR-26).
     *
     * @param staffId   ID nhân viên
     * @param requestId ID yêu cầu
     * @param req       thông tin cập nhật
     * @return chi tiết yêu cầu sau khi cập nhật
     */
    SupportDetailDTO updateSupportRequest(long staffId, long requestId, UpdateSupportRequest req);
}
