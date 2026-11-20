/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: AuthServiceImpl.java
 * Lần cuối cập nhật: 10/11/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực nghiệp vụ xác thực đăng nhập: kiểm tra thông tin tài khoản và so khớp mật khẩu bằng BCrypt.
 */
package com.shipping.service.impl;

import com.shipping.dao.UserDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.User;
import com.shipping.service.AuthService;
import com.shipping.util.BCrypt;

/**
 * Triển khai {@link AuthService}.
 */
public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO;

    /**
     * Phương thức AuthServiceImpl: dùng để khởi tạo đối tượng AuthServiceImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Phương thức login: dùng để thực hiện nghiệp vụ xác thực đăng nhập người dùng bằng tài khoản và mật khẩu, phục vụ kiểm soát đăng nhập hệ thống.
     */
    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AppException(ErrorCode.TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG);
        }

        User user = userDAO.findByUsername(username.trim())
                .orElseThrow(() -> new AppException(ErrorCode.TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG));

        boolean matched = BCrypt.checkpw(password, user.getPassword());
        if (!matched) {
            throw new AppException(ErrorCode.TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG);
        }

        if (user.getRoles().isEmpty()) {
            throw new AppException(ErrorCode.TAI_KHOAN_KHONG_CO_VAI_TRO);
        }

        return user;
    }
}
