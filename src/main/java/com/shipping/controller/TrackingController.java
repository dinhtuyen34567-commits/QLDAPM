/**
 * Người code: Nguyễn Đình Tuyển (UC-02)
 * Tên file: TrackingController.java
 * Lần cuối cập nhật: 26/10/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp điều khiển tiếp nhận yêu cầu HTTP tra cứu đơn hàng, cung cấp API JSON tra cứu và điều hướng hiển thị trang tra cứu JSP.
 */
package com.shipping.controller;

import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.exception.AppException;
import com.shipping.model.TrackingInfo;
import com.shipping.service.TrackingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller UC-02: API tra cứu và màn hình tra cứu vận đơn.
 *
 * <ul>
 *   <li>{@code GET /api/v1/van-don/tra-cuu?maVanDon=...}</li>
 *   <li>{@code GET /tra-cuu}</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/v1/van-don/tra-cuu", "/tra-cuu"})
public class TrackingController extends BaseController {

    private static final String VIEW_TRACKING = "/views/tracking.jsp";

    private TrackingService trackingService;

    /**
     * Phương thức init: dùng để khởi tạo cấu hình và tài nguyên cần thiết cho Servlet/Filter khi ứng dụng khởi động, phục vụ chuẩn bị môi trường chạy.
     */
    @Override
    public void init() {
        trackingService = (TrackingService) getServletContext()
                .getAttribute(AppInitializer.ATTR_TRACKING_SERVICE);
    }

    /**
     * Phương thức getTrackingService: dùng để lấy đối tượng TrackingService từ ServletContext, phục vụ điều phối tác vụ tra cứu vận đơn.
     */
    private TrackingService getTrackingService() {
        if (trackingService == null) {
            trackingService = (TrackingService) getServletContext()
                    .getAttribute(AppInitializer.ATTR_TRACKING_SERVICE);
        }
        if (trackingService == null) {
            throw new IllegalStateException("Chua khoi tao TrackingService");
        }
        return trackingService;
    }

    /**
     * Phương thức doGet: dùng để tiếp nhận và xử lý yêu cầu HTTP GET từ client, phục vụ hiển thị giao diện hoặc trả dữ liệu qua API.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/tra-cuu".equals(path)) {
            if (!SessionUtils.isCustomer(request)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            request.getRequestDispatcher(VIEW_TRACKING).forward(request, response);
            return;
        }

        try {
            long customerId = SessionUtils.requireCustomerId(request);
            String trackingCode = request.getParameter("maVanDon");
            TrackingInfo info = getTrackingService().track(customerId, trackingCode);
            writeJson(response, HttpServletResponse.SC_OK, info);
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }
}
