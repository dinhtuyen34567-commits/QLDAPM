/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: CustomerDAO.java
 * Lần cuối cập nhật: 05/10/2026 10:45
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện (Interface) khai báo các phương thức thao tác dữ liệu khách hàng trong CSDL phục vụ UC-01.
 */
package com.shipping.dao;

import com.shipping.model.Customer;

import java.util.Optional;

/**
 * Truy cập dữ liệu khách hàng ({@code tblNguoiDung} + {@code tblKhachHang}).
 */
public interface CustomerDAO {

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     *
     * Lấy thông tin khách hàng theo id người dùng.
     *
     * <p>Chỉ trả về khách hàng nếu người dùng thực sự có bản ghi trong
     * {@code tblKhachHang}; nếu là nhân viên hoặc tài xế thì trả rỗng.</p>
     *
     * @param userId id trong {@code tblNguoiDung}
     * @return thông tin khách hàng, hoặc {@link Optional#empty()} nếu không phải khách hàng
     */
    Optional<Customer> findById(long userId);
}
