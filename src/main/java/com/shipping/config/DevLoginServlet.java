/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: DevLoginServlet.java
 * Lần cuối cập nhật: 10/10/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Servlet hỗ trợ môi trường phát triển (Dev) cho phép chuyển đổi nhanh phiên đăng nhập của các khách hàng mẫu.
 */
package com.shipping.config;

import com.shipping.dao.CustomerDAO;
import com.shipping.model.Customer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servlet dang nhap phat trien, CHI de chay thu cuc bo.
 *
 * <p><b>TAm thoi, se bi xoa khi co module dang nhap that.</b> Mac dinh bi tat
 * ({@code app.dev-login-enabled=false}); chi bat bang bien moi truong
 * {@code APP_DEV_LOGIN=true}.</p>
 */
@WebServlet(urlPatterns = "/dev-login")
public class DevLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(DevLoginServlet.class.getName());

    private CustomerDAO customerDAO;

    /**
     * Phương thức init: dùng để khởi tạo cấu hình và tài nguyên cần thiết cho Servlet/Filter khi ứng dụng khởi động, phục vụ chuẩn bị môi trường chạy.
     */
    @Override
    public void init() {
        // @WebServlet yeu cau constructor khong tham so nen DAO duoc lay
        // tu ServletContext sau khi AppInitializer da khoi tao.
        customerDAO = (CustomerDAO) getServletContext()
                .getAttribute(AppInitializer.ATTR_CUSTOMER_DAO);
    }

    /**
     * Phương thức getCustomerDAO: dùng để lấy giá trị của thuộc tính CustomerDAO, phục vụ việc truy xuất dữ liệu của đối tượng.
     *
     * Lay DAO tu ServletContext, dam bao da duoc khoi tao.
     *
     * @return DAO truy cap khach hang
     */
    private CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            customerDAO = (CustomerDAO) getServletContext()
                    .getAttribute(AppInitializer.ATTR_CUSTOMER_DAO);
        }
        if (customerDAO == null) {
            throw new IllegalStateException("Chua khoi tao CustomerDAO");
        }
        return customerDAO;
    }

    /**
     * Phương thức doGet: dùng để tiếp nhận và xử lý yêu cầu HTTP GET từ client, phục vụ hiển thị giao diện hoặc trả dữ liệu qua API.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Chi bat khi duoc bat tuong minh qua bien moi truong
        boolean enabled = AppConfig.getBoolean("app.dev-login-enabled", false)
                || "true".equalsIgnoreCase(System.getenv("APP_DEV_LOGIN"));

        if (!enabled) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long userId = 1L;
        String rawId = request.getParameter("userId");
        if (rawId != null && !rawId.isBlank()) {
            try {
                userId = Long.parseLong(rawId.trim());
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId khong hop le");
                return;
            }
        }

        Optional<Customer> customer = getCustomerDAO().findById(userId);
        if (customer.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Khong tim thay khach hang");
            return;
        }

        SessionUtils.setLogin(request, customer.get().getId(), SessionUtils.ROLE_CUSTOMER,
                customer.get().getFullName());
        LOG.info("Dev login: dang nhap tam thoi khach hang id=" + customer.get().getId());

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<!DOCTYPE html><html lang=\"vi\"><head>"
                + "<meta charset=\"UTF-8\"><title>Dev login</title></head><body>"
                + "<p>Dang nhap tam thoi thanh cong: "
                + escapeHtml(customer.get().getFullName())
                + " (id " + customer.get().getId() + ")</p>"
                + "<p><a href=\"" + request.getContextPath() + "/don-hang/tao\">"
                + "Den man hinh tao don</a></p>"
                + "</body></html>");
    }

    /**
     * Phương thức escapeHtml: dùng để mã hóa các ký tự đặc biệt của HTML, phục vụ ngăn chặn lỗ hổng bảo mật XSS.
     *
     * Escape HTML de tranh XSS khi hien thi ten nguoi dung.
     */
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
