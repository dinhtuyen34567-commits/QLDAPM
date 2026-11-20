/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: User.java
 * Lần cuối cập nhật: 02/11/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể đại diện cho tài khoản người dùng đăng nhập hệ thống, chứa tên đăng nhập, mật khẩu băm, vai trò và trạng thái hoạt động.
 */
package com.shipping.model;

import java.util.Collections;
import java.util.Set;

/**
 * Đối tượng người dùng chung ánh xạ từ bảng tblNguoiDung.
 */
public class User {

    private long id;
    private String maNguoiDung;
    private String username;
    private String password;
    private String hoTen;
    private String email;
    private String sdt;
    private Set<UserRole> roles;

    /**
     * Phương thức User: dùng để khởi tạo đối tượng User với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public User() {
    }

    /**
     * Phương thức User: dùng để khởi tạo đối tượng User với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public User(long id, String maNguoiDung, String username, String password,
                String hoTen, String email, String sdt, Set<UserRole> roles) {
        this.id = id;
        this.maNguoiDung = maNguoiDung;
        this.username = username;
        this.password = password;
        this.hoTen = hoTen;
        this.email = email;
        this.sdt = sdt;
        this.roles = roles;
    }

    /**
     * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getId() {
        return id;
    }

    /**
     * Phương thức setId: dùng để gán hoặc cập nhật giá trị cho thuộc tính Id, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Phương thức getMaNguoiDung: dùng để lấy giá trị của thuộc tính MaNguoiDung, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    /**
     * Phương thức setMaNguoiDung: dùng để gán hoặc cập nhật giá trị cho thuộc tính MaNguoiDung, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
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

    /**
     * Phương thức getHoTen: dùng để lấy giá trị của thuộc tính HoTen, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getHoTen() {
        return hoTen;
    }

    /**
     * Phương thức setHoTen: dùng để gán hoặc cập nhật giá trị cho thuộc tính HoTen, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    /**
     * Phương thức getEmail: dùng để lấy giá trị của thuộc tính Email, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Phương thức setEmail: dùng để gán hoặc cập nhật giá trị cho thuộc tính Email, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Phương thức getSdt: dùng để lấy giá trị của thuộc tính Sdt, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getSdt() {
        return sdt;
    }

    /**
     * Phương thức setSdt: dùng để gán hoặc cập nhật giá trị cho thuộc tính Sdt, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    /**
     * Phương thức getRoles: dùng để lấy giá trị của thuộc tính Roles, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Set<UserRole> getRoles() {
        return roles != null ? roles : Collections.emptySet();
    }

    /**
     * Phương thức setRoles: dùng để gán hoặc cập nhật giá trị cho thuộc tính Roles, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * Phương thức hasRole: dùng để thực thi thao tác hasRole, phục vụ tiến trình xử lý nghiệp vụ của lớp User.
     */
    public boolean hasRole(UserRole role) {
        return roles != null && roles.contains(role);
    }
}
