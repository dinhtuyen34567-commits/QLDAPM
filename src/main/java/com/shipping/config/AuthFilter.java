/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: AuthFilter.java
 * Lần cuối cập nhật: 03/11/2026 15:45
 * 
 * Tổng quát chức năng của lớp:
 * Bộ lọc kiểm soát truy cập (Authentication & Authorization Filter): xác thực quyền hạn và chuyển hướng đăng nhập cho các đường dẫn được bảo vệ.
 */
package com.shipping.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shipping.model.ErrorResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Bộ lọc kiểm tra phiên đăng nhập và phân quyền truy cập (RBAC) cho toàn bộ hệ thống.
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    /**
     * Phương thức init: dùng để khởi tạo cấu hình và tài nguyên cần thiết cho Servlet/Filter khi ứng dụng khởi động, phục vụ chuẩn bị môi trường chạy.
     */
    @Override
    public void init(jakarta.servlet.FilterConfig config) {
    }

    /**
     * Phương thức doFilter: dùng để đánh chặn và kiểm tra các yêu cầu HTTP trước khi chuyển tới Servlet đích, phục vụ kiểm soát xác thực hoặc mã hóa dữ liệu.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();

        // 1. Các tài nguyên tĩnh và công khai: cho qua trực tiếp
        if (path.startsWith("/assets/") || path.equals("/dang-nhap") || path.equals("/dang-xuat")
                || path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/logout")
                || path.equals("/dev-login") || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Nhánh API dành riêng cho Nhân viên CSKH
        if (path.startsWith("/api/v1/nhan-vien")
                || (path.startsWith("/api/v1/yeu-cau") && !httpRequest.getMethod().equalsIgnoreCase("POST"))) {
            if (!SessionUtils.isStaff(httpRequest)) {
                writeForbidden(httpResponse);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 3. Nhánh Giao diện Web dành cho Nhân viên CSKH
        if (path.startsWith("/cskh/")) {
            if (!SessionUtils.isStaff(httpRequest)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dang-nhap?redirect=" + path);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 4. Nhánh API dành cho Khách hàng
        if (path.startsWith("/api/v1/don-hang") || path.startsWith("/api/v1/khach-hang")
                || path.startsWith("/api/v1/van-don") || path.startsWith("/api/v1/hang-hoa")
                || (path.equals("/api/v1/yeu-cau") && httpRequest.getMethod().equalsIgnoreCase("POST"))) {
            if (!SessionUtils.isCustomer(httpRequest)) {
                writeUnauthorized(httpResponse);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 5. Nhánh Giao diện Web dành cho Khách hàng
        if (path.startsWith("/don-hang/") || path.equals("/tra-cuu") || path.equals("/ho-tro/tao")) {
            if (!SessionUtils.isCustomer(httpRequest)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dang-nhap?redirect=" + path);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Phương thức writeUnauthorized: dùng để phản hồi mã lỗi 401 Unauthorized kèm thông báo đăng nhập, phục vụ chặn người dùng chưa xác thực.
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(GSON.toJson(new ErrorResponse(
                "KHONG_CO_QUYEN", "Phiên hết hạn hoặc không phải khách hàng")));
    }

    /**
     * Phương thức writeForbidden: dùng để phản hồi mã lỗi 403 Forbidden kèm thông báo từ chối truy cập, phục vụ chặn người dùng không đủ quyền hạn.
     */
    private void writeForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(GSON.toJson(new ErrorResponse(
                "KHONG_CO_QUYEN", "Bạn không có quyền truy cập chức năng này")));
    }

    /**
     * Phương thức destroy: dùng để giải phóng tài nguyên và dọn dẹp kết nối khi Servlet/Filter kết thúc vòng đời, phục vụ tránh rò rỉ tài nguyên hệ thống.
     */
    @Override
    public void destroy() {
    }
}
