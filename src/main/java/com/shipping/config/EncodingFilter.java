/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: EncodingFilter.java
 * Lần cuối cập nhật: 26/09/2026 09:30
 * 
 * Tổng quát chức năng của lớp:
 * Bộ lọc Servlet (Filter) thiết lập chuẩn mã hóa ký tự UTF-8 cho toàn bộ HTTP request và response, phục vụ hiển thị chính xác tiếng Việt trong hệ thống.
 */
package com.shipping.config;

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
 * Dat encoding UTF-8 cho request va response.
 *
 * <p>Dam bao tieng Viet co dau hien thi dung o moi man hinh, ke ca khi client
 * gui body JSON (CLAUDE.md muc 2).</p>
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    /**
     * Phương thức init: dùng để khởi tạo cấu hình và tài nguyên cần thiết cho Servlet/Filter khi ứng dụng khởi động, phục vụ chuẩn bị môi trường chạy.
     */
    @Override
    public void init(jakarta.servlet.FilterConfig config) {
        // Khong can cau hinh
    }

    /**
     * Phương thức doFilter: dùng để đánh chặn và kiểm tra các yêu cầu HTTP trước khi chuyển tới Servlet đích, phục vụ kiểm soát xác thực hoặc mã hóa dữ liệu.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }
        if (request instanceof HttpServletRequest httpRequest) {
            httpRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }

        chain.doFilter(request, response);
    }

    /**
     * Phương thức destroy: dùng để giải phóng tài nguyên và dọn dẹp kết nối khi Servlet/Filter kết thúc vòng đời, phục vụ tránh rò rỉ tài nguyên hệ thống.
     */
    @Override
    public void destroy() {
        // Khong tai nguyen can giai phong
    }
}
