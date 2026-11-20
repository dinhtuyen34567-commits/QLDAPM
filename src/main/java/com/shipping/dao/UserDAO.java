/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: UserDAO.java
 * Lần cuối cập nhật: 06/11/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện khai báo các thao tác CSDL liên quan đến tài khoản người dùng phục vụ xác thực đăng nhập.
 */
package com.shipping.dao;

import com.shipping.model.User;
import java.util.Optional;

/**
 * Truy cập thông tin người dùng chung và vai trò (tblNguoiDung, tblKhachHang, tblNhanVien).
 */
public interface UserDAO {

    /**
     * Phương thức findByUsername: dùng để tìm kiếm thông tin người dùng trong CSDL theo tên đăng nhập, phục vụ quy trình xác thực đăng nhập.
     *
     * Tìm người dùng theo tên đăng nhập kèm các vai trò của họ.
     *
     * @param username tên đăng nhập
     * @return người dùng nếu tìm thấy
     */
    Optional<User> findByUsername(String username);

    /**
     * Phương thức findById: dùng để truy vấn thông tin chi tiết của một bản ghi theo mã định danh khóa chính, phục vụ lấy dữ liệu chi tiết cho các tầng nghiệp vụ.
     *
     * Tìm người dùng theo ID.
     *
     * @param id id người dùng
     * @return người dùng nếu tìm thấy
     */
    Optional<User> findById(long id);
}
