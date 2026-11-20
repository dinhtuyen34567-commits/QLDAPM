/**
 * Người code: Phan Tiến Dũng (UC-03)
 * Tên file: AuthServiceTest.java
 * Lần cuối cập nhật: 14/11/2026 10:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp kiểm thử đơn vị cho AuthServiceImpl, kiểm tra các ca đăng nhập thành công, sai mật khẩu, tài khoản không tồn tại hoặc bị vô hiệu hóa.
 */
package com.shipping.service;

import com.shipping.dao.UserDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.User;
import com.shipping.model.UserRole;
import com.shipping.service.impl.AuthServiceImpl;
import com.shipping.util.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử AuthService - Xác thực và đăng nhập (UC_5.1)")
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    // Hash BCrypt chuẩn của mật khẩu "Dev@12345"
    private static final String SAMPLE_BCRYPT_HASH = "$2a$10$Xtybkrdhfr8feW7xGAC.3eRpBdlMG4HBG0j1Tw.S3OHAzmgq4/WJW";

    /**
     * Phương thức setUp: dùng để khởi tạo cấu hình môi trường kiểm thử và nạp các mock object trước khi chạy mỗi ca test, phục vụ thiết lập môi trường test độc lập.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userDAO);
    }

    /**
     * Phương thức testLogin_Success_Customer: dùng để kiểm thử kịch bản Login Success Customer, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Đăng nhập thành công với vai trò Khách hàng")
    void testLogin_Success_Customer() {
        User user = new User(1L, "KH001", "khachhang1", SAMPLE_BCRYPT_HASH,
                "Nguyễn Văn A", "kh1@example.com", "0912345678", Set.of(UserRole.CUSTOMER));

        when(userDAO.findByUsername("khachhang1")).thenReturn(Optional.of(user));

        User result = authService.login("khachhang1", "Dev@12345");

        assertNotNull(result);
        assertEquals("khachhang1", result.getUsername());
        assertTrue(result.hasRole(UserRole.CUSTOMER));
    }

    /**
     * Phương thức testLogin_Success_Staff: dùng để kiểm thử kịch bản Login Success Staff, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Đăng nhập thành công với vai trò Nhân viên CSKH")
    void testLogin_Success_Staff() {
        User user = new User(2L, "NV001", "cskh1", SAMPLE_BCRYPT_HASH,
                "Nguyễn Thị CSKH", "cskh1@example.com", "0981112233", Set.of(UserRole.STAFF));

        when(userDAO.findByUsername("cskh1")).thenReturn(Optional.of(user));

        User result = authService.login("cskh1", "Dev@12345");

        assertNotNull(result);
        assertEquals("cskh1", result.getUsername());
        assertTrue(result.hasRole(UserRole.STAFF));
    }

    /**
     * Phương thức testLogin_WrongPassword: dùng để kiểm thử kịch bản Login WrongPassword, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Đăng nhập thất bại khi sai mật khẩu")
    void testLogin_WrongPassword() {
        User user = new User(1L, "KH001", "khachhang1", SAMPLE_BCRYPT_HASH,
                "Nguyễn Văn A", "kh1@example.com", "0912345678", Set.of(UserRole.CUSTOMER));

        when(userDAO.findByUsername("khachhang1")).thenReturn(Optional.of(user));

        AppException ex = assertThrows(AppException.class, () -> authService.login("khachhang1", "SaiMatKhau123"));
        assertEquals(ErrorCode.TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG, ex.getErrorCode());
    }

    /**
     * Phương thức testLogin_UserNotFound: dùng để kiểm thử kịch bản Login UserNotFound, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Đăng nhập thất bại khi tài khoản không tồn tại")
    void testLogin_UserNotFound() {
        when(userDAO.findByUsername("khongtontai")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> authService.login("khongtontai", "Dev@12345"));
        assertEquals(ErrorCode.TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG, ex.getErrorCode());
    }

    /**
     * Phương thức testLogin_NoRole: dùng để kiểm thử kịch bản Login NoRole, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Đăng nhập thất bại khi tài khoản chưa được phân quyền")
    void testLogin_NoRole() {
        User user = new User(5L, "USER01", "chuarole", SAMPLE_BCRYPT_HASH,
                "Chưa Role", "norole@example.com", "0900000000", Collections.emptySet());

        when(userDAO.findByUsername("chuarole")).thenReturn(Optional.of(user));

        AppException ex = assertThrows(AppException.class, () -> authService.login("chuarole", "Dev@12345"));
        assertEquals(ErrorCode.TAI_KHOAN_KHONG_CO_VAI_TRO, ex.getErrorCode());
    }
}
