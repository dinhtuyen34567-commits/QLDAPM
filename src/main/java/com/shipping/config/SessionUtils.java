/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SessionUtils.java
 * Lần cuối cập nhật: 03/11/2026 11:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp tiện ích quản lý phiên làm việc (HttpSession): lưu thông tin đăng nhập, kiểm tra vai trò khách hàng/nhân viên và xóa phiên khi đăng xuất.
 */
package com.shipping.config;

import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Tiện ích đọc và quản lý phiên đăng nhập cho Khách hàng và Nhân viên.
 */
public final class SessionUtils {

    /** Tên thuộc tính phiên lưu id người dùng. */
    public static final String ATTR_USER_ID = "userId";

    /** Tên thuộc tính phiên lưu vai trò người dùng. */
    public static final String ATTR_ROLE = "role";

    /** Vai trò khách hàng. */
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    /** Vai trò nhân viên. */
    public static final String ROLE_STAFF = "STAFF";

    /** Tên thuộc tính phiên lưu họ tên người dùng, dùng ở header các trang. */
    public static final String ATTR_USER_NAME = "userName";

    /**
     * Phương thức SessionUtils: dùng để khởi tạo đối tượng SessionUtils với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    private SessionUtils() {
        // Lớp tiện ích, không tạo thể hiện
    }

    /**
     * Phương thức requireCustomerId: dùng để trích xuất mã khách hàng từ phiên làm việc (session) và kiểm tra bắt buộc đăng nhập, phục vụ bảo vệ các tài nguyên yêu cầu quyền khách hàng.
     *
     * Lấy id khách hàng đang đăng nhập từ phiên.
     *
     * @param request request hiện tại
     * @return id khách hàng
     * @throws AppException nếu chưa đăng nhập hoặc không phải khách hàng
     */
    public static long requireCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new AppException(ErrorCode.KHONG_CO_QUYEN);
        }
        if (!isCustomer(request)) {
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
     * Phương thức requireStaffId: dùng để trích xuất mã nhân viên từ phiên làm việc và kiểm tra bắt buộc đăng nhập, phục vụ bảo vệ các chức năng quản trị CSKH.
     *
     * Lấy id nhân viên đang đăng nhập từ phiên.
     *
     * @param request request hiện tại
     * @return id nhân viên
     * @throws AppException nếu chưa đăng nhập hoặc không phải nhân viên
     */
    public static long requireStaffId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new AppException(ErrorCode.KHONG_CO_QUYEN);
        }
        if (!isStaff(request)) {
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
     * Phương thức isCustomer: dùng để kiểm tra xem người dùng hiện tại có vai trò Khách hàng hay không, phục vụ phân quyền truy cập.
     *
     * Kiểm tra người dùng hiện tại có phải khách hàng hay không.
     *
     * @param request request hiện tại
     * @return {@code true} nếu vai trò là khách hàng
     */
    public static boolean isCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object role = session.getAttribute(ATTR_ROLE);
        return ROLE_CUSTOMER.equals(role) || UserRole.CUSTOMER.name().equals(role);
    }

    /**
     * Phương thức isStaff: dùng để kiểm tra xem người dùng hiện tại có vai trò Nhân viên hay không, phục vụ phân quyền truy cập chức năng nội bộ.
     *
     * Kiểm tra người dùng hiện tại có phải nhân viên hay không.
     *
     * @param request request hiện tại
     * @return {@code true} nếu vai trò là nhân viên
     */
    public static boolean isStaff(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object role = session.getAttribute(ATTR_ROLE);
        return ROLE_STAFF.equals(role) || UserRole.STAFF.name().equals(role);
    }

    /**
     * Phương thức setLogin: dùng để ghi nhận thông tin đăng nhập thành công vào phiên làm việc (HttpSession), phục vụ duy trì trạng thái đăng nhập.
     *
     * Ghi thông tin đăng nhập vào phiên.
     *
     * @param request  request hiện tại
     * @param userId   id người dùng
     * @param role     vai trò (CUSTOMER / STAFF)
     * @param fullName họ tên hiển thị ở đầu trang
     */
    public static void setLogin(HttpServletRequest request, long userId, String role,
                                String fullName) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USER_ID, userId);
        session.setAttribute(ATTR_ROLE, role);
        session.setAttribute(ATTR_USER_NAME, fullName);
    }

    /**
     * Phương thức logout: dùng để hủy phiên làm việc hiện tại của người dùng, phục vụ chức năng đăng xuất an toàn khỏi hệ thống.
     *
     * Hủy phiên làm việc hiện tại (đăng xuất).
     *
     * @param request request hiện tại
     */
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
