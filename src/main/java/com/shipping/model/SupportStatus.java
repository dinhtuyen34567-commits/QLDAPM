/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportStatus.java
 * Lần cuối cập nhật: 04/11/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Enum định nghĩa các trạng thái của yêu cầu khiếu nại / hỗ trợ khách hàng (MOI, DANG_XU_LY, DA_DONG).
 */
package com.shipping.model;

/**
 * Trạng thái yêu cầu hỗ trợ / khiếu nại (khớp CSDL tblYeuCauHoTro).
 */
public enum SupportStatus {
    /**
     * Phương thức MOI: dùng để thực thi thao tác MOI, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportStatus.
     */
    MOI("Mới"),
    /**
     * Phương thức DANG_XU_LY: dùng để thực thi thao tác DANG_XU_LY, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportStatus.
     */
    DANG_XU_LY("Đang xử lý"),
    /**
     * Phương thức DA_DONG: dùng để thực thi thao tác DA_DONG, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportStatus.
     */
    DA_DONG("Đã đóng");

    private final String description;

    /**
     * Phương thức SupportStatus: dùng để khởi tạo đối tượng SupportStatus với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    SupportStatus(String description) {
        this.description = description;
    }

    /**
     * Phương thức getDescription: dùng để lấy giá trị của thuộc tính Description, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getDescription() {
        return description;
    }
}
