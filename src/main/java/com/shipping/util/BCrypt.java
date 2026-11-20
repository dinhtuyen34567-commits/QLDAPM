/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: BCrypt.java
 * Lần cuối cập nhật: 02/11/2026 08:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp tiện ích mã hóa mật khẩu theo giải thuật băm OpenBSD BCrypt, hỗ trợ tạo salt ngẫu nhiên và kiểm tra mật khẩu an toàn.
 */
package com.shipping.util;

/**
 * Tiện ích kiểm tra và băm mật khẩu BCrypt, tương thích hash $2a$10$... trong CSDL.
 */
public final class BCrypt {

    /**
     * Phương thức BCrypt: dùng để khởi tạo đối tượng BCrypt với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    private BCrypt() {
    }

    /**
     * Phương thức checkpw: dùng để kiểm tra và so khớp mật khẩu người dùng nhập với chuỗi băm BCrypt lưu trong CSDL, phục vụ việc xác thực đăng nhập.
     *
     * Kiểm tra mật khẩu plain-text có khớp với hash BCrypt hay không.
     *
     * @param plaintext mật khẩu người dùng nhập
     * @param hashed    chuỗi hash BCrypt
     * @return true nếu khớp
     */
    public static boolean checkpw(String plaintext, String hashed) {
        if (plaintext == null || hashed == null) {
            return false;
        }
        try {
            return org.mindrot.jbcrypt.BCrypt.checkpw(plaintext, hashed);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Phương thức hashpw: dùng để mã hóa mật khẩu thô dạng văn bản bằng giải thuật băm BCrypt với salt ngẫu nhiên, phục vụ bảo mật mật khẩu người dùng.
     *
     * Băm mật khẩu plain-text với salt.
     *
     * @param plaintext mật khẩu
     * @param salt      salt BCrypt
     * @return chuỗi hash
     */
    public static String hashpw(String plaintext, String salt) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(plaintext, salt);
    }

    /**
     * Phương thức gensalt: dùng để sinh chuỗi muối (salt) ngẫu nhiên theo số vòng lặp băm quy định, phục vụ quá trình mã hóa mật khẩu an toàn.
     *
     * Tạo salt BCrypt ngẫu nhiên.
     *
     * @param logRounds số vòng băm (thường là 10)
     * @return chuỗi salt
     */
    public static String gensalt(int logRounds) {
        return org.mindrot.jbcrypt.BCrypt.gensalt(logRounds);
    }
}
