package com.shipping.dao;

import com.shipping.model.Customer;

import java.util.Optional;

/**
 * Truy cập dữ liệu khách hàng ({@code tblNguoiDung} + {@code tblKhachHang}).
 */
public interface CustomerDAO {

    /**
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
