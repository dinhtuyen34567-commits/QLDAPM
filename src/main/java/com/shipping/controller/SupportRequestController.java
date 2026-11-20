/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequestController.java
 * Lần cuối cập nhật: 13/11/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp điều khiển tiếp nhận và điều phối các API nghiệp vụ khiếu nại cũng như hiển thị các trang JSP quản lý và tạo khiếu nại.
 */
package com.shipping.controller;

import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.CreateSupportRequest;
import com.shipping.model.SupportDetailDTO;
import com.shipping.model.SupportPageResult;
import com.shipping.model.SupportRequest;
import com.shipping.model.UpdateSupportRequest;
import com.shipping.service.SupportRequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller UC-03: Xử lý tạo khiếu nại (Khách hàng) và quản lý khiếu nại (CSKH).
 */
@WebServlet(urlPatterns = {
        "/ho-tro/tao",
        "/cskh/yeu-cau",
        "/cskh/yeu-cau/chi-tiet",
        "/api/v1/yeu-cau",
        "/api/v1/yeu-cau/*"
})
public class SupportRequestController extends BaseController {

    private static final String VIEW_COMPLAINT_CREATE = "/views/complaint-create.jsp";
    private static final String VIEW_SUPPORT_LIST = "/views/support-list.jsp";
    private static final String VIEW_SUPPORT_DETAIL = "/views/support-detail.jsp";

    private SupportRequestService supportService;

    /**
     * Phương thức init: dùng để khởi tạo cấu hình và tài nguyên cần thiết cho Servlet/Filter khi ứng dụng khởi động, phục vụ chuẩn bị môi trường chạy.
     */
    @Override
    public void init() {
        supportService = (SupportRequestService) getServletContext()
                .getAttribute(AppInitializer.ATTR_SUPPORT_SERVICE);
    }

    /**
     * Phương thức getSupportService: dùng để lấy đối tượng SupportRequestService từ ServletContext, phục vụ điều phối các tác vụ khiếu nại CSKH.
     */
    private SupportRequestService getSupportService() {
        if (supportService == null) {
            supportService = (SupportRequestService) getServletContext()
                    .getAttribute(AppInitializer.ATTR_SUPPORT_SERVICE);
        }
        if (supportService == null) {
            throw new IllegalStateException("Chưa khởi tạo SupportRequestService");
        }
        return supportService;
    }

    /**
     * Phương thức doGet: dùng để tiếp nhận và xử lý yêu cầu HTTP GET từ client, phục vụ hiển thị giao diện hoặc trả dữ liệu qua API.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // 1. Khách hàng mở trang tạo khiếu nại
        if ("/ho-tro/tao".equals(path)) {
            request.getRequestDispatcher(VIEW_COMPLAINT_CREATE).forward(request, response);
            return;
        }

        // 2. Nhân viên CSKH mở danh sách yêu cầu
        if ("/cskh/yeu-cau".equals(path)) {
            request.getRequestDispatcher(VIEW_SUPPORT_LIST).forward(request, response);
            return;
        }

        // 3. Nhân viên CSKH mở màn hình chi tiết xử lý
        if ("/cskh/yeu-cau/chi-tiet".equals(path)) {
            request.getRequestDispatcher(VIEW_SUPPORT_DETAIL).forward(request, response);
            return;
        }

        // 4. API danh sách hoặc chi tiết
        try {
            long staffId = SessionUtils.requireStaffId(request);
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || "/".equals(pathInfo)) {
                // API 8: GET /api/v1/yeu-cau?trangThai=&trang=
                String status = request.getParameter("trangThai");
                int page = 1;
                try {
                    String pageStr = request.getParameter("trang");
                    if (pageStr != null) {
                        page = Integer.parseInt(pageStr);
                    }
                } catch (NumberFormatException ignored) {
                }

                SupportPageResult result = getSupportService().getSupportList(staffId, status, page);
                writeJson(response, HttpServletResponse.SC_OK, result);
            } else {
                // API 9: GET /api/v1/yeu-cau/{id}
                long requestId = parseRequestId(pathInfo);
                SupportDetailDTO detail = getSupportService().getSupportDetail(staffId, requestId);
                writeJson(response, HttpServletResponse.SC_OK, detail);
            }
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }

    /**
     * Phương thức doPost: dùng để tiếp nhận và xử lý yêu cầu HTTP POST từ client, phục vụ việc tiếp nhận và xử lý dữ liệu gửi lên từ người dùng.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/api/v1/yeu-cau".equals(path)) {
            // API 6: POST /api/v1/yeu-cau (Khách hàng tạo khiếu nại)
            try {
                long customerId = SessionUtils.requireCustomerId(request);
                CreateSupportRequest body = parseBody(request, CreateSupportRequest.class);
                SupportRequest created = getSupportService().createRequest(customerId, body);
                writeJson(response, HttpServletResponse.SC_CREATED, created);
            } catch (AppException e) {
                writeError(response, e);
            } catch (Exception e) {
                writeSystemError(response, e);
            }
        }
    }

    /**
     * Phương thức handlePatch: dùng để xử lý yêu cầu HTTP PATCH (cập nhật từng phần), phục vụ việc cập nhật trạng thái và phản hồi khiếu nại.
     */
    protected void handlePatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // API 10: PATCH /api/v1/yeu-cau/{id} (Nhân viên cập nhật)
        try {
            long staffId = SessionUtils.requireStaffId(request);
            String pathInfo = request.getPathInfo();
            long requestId = parseRequestId(pathInfo);

            UpdateSupportRequest body = parseBody(request, UpdateSupportRequest.class);
            SupportDetailDTO updated = getSupportService().updateSupportRequest(staffId, requestId, body);
            writeJson(response, HttpServletResponse.SC_OK, updated);
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }

    /**
     * Phương thức service: dùng để tiếp nhận mọi yêu cầu HTTP và phân phối tới các phương thức xử lý tương ứng (GET, POST, PATCH), phục vụ điều hướng vòng đời Servlet.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            handlePatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    /**
     * Phương thức parseRequestId: dùng để tách và phân tích mã định danh yêu cầu từ đường dẫn URL (PathInfo), phục vụ xác định bản ghi cần thao tác.
     */
    private long parseRequestId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new AppException(ErrorCode.YEU_CAU_KHONG_TON_TAI);
        }
        try {
            String idStr = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.YEU_CAU_KHONG_TON_TAI);
        }
    }
}
