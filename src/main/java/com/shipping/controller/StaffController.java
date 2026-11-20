/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: StaffController.java
 * Lần cuối cập nhật: 12/11/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp điều khiển cung cấp thông tin nhân viên đang đăng nhập phục vụ hiển thị trên thanh tiêu đề của giao diện CSKH.
 */
package com.shipping.controller;

import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.exception.AppException;
import com.shipping.model.StaffDTO;
import com.shipping.service.SupportRequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller trả thông tin nhân viên đang trực (API 7).
 */
@WebServlet(urlPatterns = {"/api/v1/nhan-vien/me"})
public class StaffController extends BaseController {

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
        return supportService;
    }

    /**
     * Phương thức doGet: dùng để tiếp nhận và xử lý yêu cầu HTTP GET từ client, phục vụ hiển thị giao diện hoặc trả dữ liệu qua API.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long staffId = SessionUtils.requireStaffId(request);
            StaffDTO staff = getSupportService().getStaffMe(staffId);
            writeJson(response, HttpServletResponse.SC_OK, staff);
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }
}
