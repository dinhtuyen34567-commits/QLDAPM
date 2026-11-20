/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: AuthService.java
 * Lần cuối cập nhật: 09/11/2026 15:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện định nghĩa nghiệp vụ xác thực đăng nhập người dùng và kiểm tra tính hợp lệ của tài khoản.
 */
package com.shipping.service;

import com.shipping.model.User;

/**
 * Dịch vụ xác thực và đăng nhập người dùng (UC_5.1).
 */
public interface AuthService {

    /**
     * Phương thức login: dùng để thực hiện nghiệp vụ xác thực đăng nhập người dùng bằng tài khoản và mật khẩu, phục vụ kiểm soát đăng nhập hệ thống.
     *
     * Xác thực thông tin đăng nhập bằng username và mật khẩu.
     *
     * @param username tên đăng nhập
     * @param password mật khẩu plain-text
     * @return đối tượng User nếu xác thực thành công
     * @throws com.shipping.exception.AppException nếu sai thông tin hoặc tài khoản chưa phân quyền
     */
    User login(String username, String password);
}
