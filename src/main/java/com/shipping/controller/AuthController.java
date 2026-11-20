/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: AuthController.java
 * Lần cuối cập nhật: 12/11/2026 09:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp điều khiển xử lý đăng nhập và đăng xuất: tiếp nhận thông tin từ form đăng nhập, thiết lập phiên làm việc và phân luồng điều hướng.
 */
package com.shipping.controller;

import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.LoginRequest;
import com.shipping.model.User;
import com.shipping.model.UserRole;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý đăng nhập, đăng xuất (UC_5.1 & UC_5.2).
 */
@WebServlet(urlPatterns = {
        "/dang-nhap",
        "/dang-xuat",
        "/api/v1/auth/login",
        "/api/v1/auth/logout"
})
public class AuthController extends BaseController {

    private static final String VIEW_LOGIN = "/views/login.jsp";

    /**
     * Phương thức doGet: dùng để tiếp nhận và xử lý yêu cầu HTTP GET từ client, phục vụ hiển thị giao diện hoặc trả dữ liệu qua API.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/dang-xuat".equals(path)) {
            SessionUtils.logout(request);
            response.sendRedirect(request.getContextPath() + "/dang-nhap");
            return;
        }

        if ("/dang-nhap".equals(path)) {
            if (SessionUtils.isStaff(request)) {
                response.sendRedirect(request.getContextPath() + "/cskh/yeu-cau");
                return;
            }
            if (SessionUtils.isCustomer(request)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        }
    }

    /**
     * Phương thức doPost: dùng để tiếp nhận và xử lý yêu cầu HTTP POST từ client, phục vụ việc tiếp nhận và xử lý dữ liệu gửi lên từ người dùng.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/api/v1/auth/logout".equals(path)) {
            SessionUtils.logout(request);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            writeJson(response, HttpServletResponse.SC_OK, res);
            return;
        }

        if ("/api/v1/auth/login".equals(path)) {
            try {
                LoginRequest loginReq = parseBody(request, LoginRequest.class);
                com.shipping.service.AuthService authService = (com.shipping.service.AuthService)
                        getServletContext().getAttribute(AppInitializer.ATTR_AUTH_SERVICE);

                if (authService == null) {
                    throw new AppException(ErrorCode.LOI_HE_THONG);
                }

                User user = authService.login(loginReq.getUsername(), loginReq.getPassword());

                // Ưu tiên xác định vai trò: nếu có quyền STAFF thì cho chọn hoặc mặc định STAFF
                String primaryRole;
                String redirectUrl;
                if (user.hasRole(UserRole.STAFF)) {
                    primaryRole = SessionUtils.ROLE_STAFF;
                    redirectUrl = request.getContextPath() + "/cskh/yeu-cau";
                } else if (user.hasRole(UserRole.CUSTOMER)) {
                    primaryRole = SessionUtils.ROLE_CUSTOMER;
                    redirectUrl = request.getContextPath() + "/";
                } else {
                    throw new AppException(ErrorCode.TAI_KHOAN_KHONG_CO_VAI_TRO);
                }

                SessionUtils.setLogin(request, user.getId(), primaryRole, user.getHoTen());

                Map<String, Object> res = new HashMap<>();
                res.put("userId", user.getId());
                res.put("username", user.getUsername());
                res.put("hoTen", user.getHoTen());
                res.put("role", primaryRole);
                res.put("redirectUrl", redirectUrl);

                writeJson(response, HttpServletResponse.SC_OK, res);
            } catch (AppException e) {
                writeError(response, e);
            } catch (Exception e) {
                writeSystemError(response, e);
            }
        }
    }
}
