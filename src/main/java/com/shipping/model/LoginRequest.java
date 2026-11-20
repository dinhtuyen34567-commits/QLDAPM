/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: LoginRequest.java
 * Lần cuối cập nhật: 03/11/2026 09:15
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO tiếp nhận dữ liệu đăng nhập (tên đăng nhập và mật khẩu) từ form người dùng.
 */
package com.shipping.model;

/**
 * DTO nhận thông tin đăng nhập.
 */
public class LoginRequest {

    private String username;
    private String password;

    /**
     * Phương thức LoginRequest: dùng để khởi tạo đối tượng LoginRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public LoginRequest() {
    }

    /**
     * Phương thức LoginRequest: dùng để khởi tạo đối tượng LoginRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Phương thức getUsername: dùng để lấy giá trị của thuộc tính Username, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Phương thức setUsername: dùng để gán hoặc cập nhật giá trị cho thuộc tính Username, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Phương thức getPassword: dùng để lấy giá trị của thuộc tính Password, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Phương thức setPassword: dùng để gán hoặc cập nhật giá trị cho thuộc tính Password, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
