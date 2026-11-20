/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: StaffDAO.java
 * Lần cuối cập nhật: 07/11/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện khai báo các phương thức truy xuất thông tin nhân viên chăm sóc khách hàng trong CSDL.
 */
package com.shipping.dao;

import com.shipping.model.StaffDTO;
import java.util.Optional;

/**
 * Truy cập thông tin nhân viên (tblNhanVien + tblNguoiDung).
 */
public interface StaffDAO {

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     *
     * Lấy thông tin nhân viên theo ID người dùng.
     *
     * @param staffId ID người dùng trong tblNhanVien
     * @return thông tin nhân viên nếu tìm thấy
     */
    Optional<StaffDTO> findById(long staffId);
}
