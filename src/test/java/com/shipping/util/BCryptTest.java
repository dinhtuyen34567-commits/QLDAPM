/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: BCryptTest.java
 * Lần cuối cập nhật: 13/11/2026 16:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp kiểm thử đơn vị cho thuật toán mã hóa mật khẩu BCrypt, kiểm tra tính đúng đắn của việc sinh salt và băm mật khẩu.
 */
package com.shipping.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử BCrypt utility")
class BCryptTest {

    /**
     * Phương thức testPrivateConstructor: dùng để kiểm thử kịch bản PrivateConstructor, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Constructor là private và có thể khởi tạo qua reflection")
    void testPrivateConstructor() throws Exception {
        Constructor<BCrypt> constructor = BCrypt.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BCrypt instance = constructor.newInstance();
        assertNotNull(instance);
    }

    /**
     * Phương thức testCheckpw: dùng để kiểm thử kịch bản Checkpw, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("checkpw với mật khẩu hợp lệ và không hợp lệ")
    void testCheckpw() {
        String salt = BCrypt.gensalt(10);
        assertNotNull(salt);
        String hash = BCrypt.hashpw("Secret123", salt);
        assertNotNull(hash);

        assertTrue(BCrypt.checkpw("Secret123", hash));
        assertFalse(BCrypt.checkpw("WrongPass", hash));
    }

    /**
     * Phương thức testCheckpwNullOrInvalid: dùng để kiểm thử kịch bản CheckpwNullOrInvalid, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("checkpw với null hoặc hash không hợp lệ trả về false")
    void testCheckpwNullOrInvalid() {
        assertFalse(BCrypt.checkpw(null, "somehash"));
        assertFalse(BCrypt.checkpw("pass", null));
        assertFalse(BCrypt.checkpw(null, null));
        assertFalse(BCrypt.checkpw("pass", "invalid-hash-string"));
    }
}
