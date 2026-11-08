package com.shipping.config;

import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Tien ich doc thong tin khach hang dang dang nhap tu phien.
 *
 * <p>Server luon lay {@code customerId} tu phien (BR-01); client khong the
 * quyet dinh nguoi gui.</p>
 */
public final class SessionUtils {

    /** Ten thuoc tinh phien luu id nguoi dung. */
    public static final String ATTR_USER_ID = "userId";

    /** Ten thuoc tinh phien luu vai tro nguoi dung. */
    public static final String ATTR_ROLE = "role";

    /** Vai tro khach hang. */
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    /** Ten thuoc tinh phien luu ho ten nguoi dung, dung o header cac trang. */
    public static final String ATTR_USER_NAME = "userName";

    private SessionUtils() {
        // Lop tien ich, khong tao the
    }

    /**
     * Lay id khach hang dang dang nhap tu phien.
     *
     * @param request request hien tai
     * @return id khach hang
     * @throws AppException nếu chưa đăng nhập hoặc phiên không hợp lệ
     */
    public static long requireCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new AppException(ErrorCode.KHONG_CO_QUYEN);
        }
        Object userId = session.getAttribute(ATTR_USER_ID);
        if (userId == null) {
            throw new AppException(ErrorCode.KHONG_CO_QUYEN);
        }
        try {
            return Long.parseLong(String.valueOf(userId));
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.KHONG_CO_QUYEN);
        }
    }

    /**
     * Kiem tra nguoi dung hien tai co phai khach hang hay khong.
     *
     * @param request request hien tai
     * @return {@code true} nếu vai trò là khách hàng
     */
    public static boolean isCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return ROLE_CUSTOMER.equals(session.getAttribute(ATTR_ROLE));
    }

    /**
     * Ghi thong tin khach hang vao phien.
     *
     * @param request  request hien tai
     * @param userId   id nguoi dung
     * @param role     vai tro
     * @param fullName ho ten hien thi o dau trang
     */
    public static void setLogin(HttpServletRequest request, long userId, String role,
                                String fullName) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USER_ID, userId);
        session.setAttribute(ATTR_ROLE, role);
        session.setAttribute(ATTR_USER_NAME, fullName);
    }
}
