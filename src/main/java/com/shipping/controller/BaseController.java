/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: BaseController.java
 * Lần cuối cập nhật: 27/09/2026 10:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp điều khiển cơ sở cung cấp các tiện ích xử lý JSON, phản hồi HTTP và quản lý ngoại lệ tập trung cho toàn bộ các Controller trong hệ thống.
 */
package com.shipping.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.ErrorResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tien ich chung cho controller: ghi JSON ra response va xu ly ngoai le.
 *
 * <p>Controller chi lo phan tich request, goi service va anh xa ket qua ra
 * HTTP; khong chua logic nghiep vu hay SQL (CLAUDE.md muc 6.2).</p>
 */
public abstract class BaseController extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(BaseController.class.getName());

    /** Gson dung chung cho moi controller. */
    protected static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    /**
     * Phương thức writeJson: dùng để tuần tự hóa đối tượng Java sang định dạng JSON và ghi trực tiếp vào phản hồi HTTP, phục vụ trả kết quả API chuẩn UTF-8 về cho phía client.
     *
     * Ghi mot doi tuong JSON ra response.
     *
     * @param response response cua servlet
     * @param status   ma HTTP
     * @param body     doi tuong can ghi
     * @throws IOException nếu ghi response lỗi
     */
    protected void writeJson(HttpServletResponse response, int status, Object body)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter out = response.getWriter()) {
            out.write(GSON.toJson(body));
        }
    }

    /**
     * Phương thức parseBody: dùng để đọc và chuyển đổi nội dung JSON từ luồng HTTP request sang đối tượng POJO, phục vụ tiếp nhận và ràng buộc dữ liệu đầu vào trong các API tiếp nhận payload.
     *
     * Phân tích body JSON của request thành đối tượng POJO.
     *
     * @param request request hiện tại
     * @param clazz   lớp đích
     * @param <T>     kiểu đối tượng
     * @return đối tượng đã phân tích
     * @throws IOException nếu đọc stream lỗi
     */
    protected <T> T parseBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String raw = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (raw == null || raw.isBlank()) {
            throw new AppException(ErrorCode.THIEU_THONG_TIN);
        }
        try {
            T body = GSON.fromJson(raw, clazz);
            if (body == null) {
                throw new AppException(ErrorCode.THIEU_THONG_TIN);
            }
            return body;
        } catch (JsonSyntaxException e) {
            throw new AppException(ErrorCode.THIEU_THONG_TIN);
        }
    }

    /**
     * Phương thức writeError: dùng để chuyển đổi ngoại lệ nghiệp vụ thành phản hồi JSON kèm mã trạng thái HTTP tương ứng, phục vụ thông báo lỗi cho người dùng.
     *
     * Ghi lỗi nghiệp vụ ra response dưới dạng {@code {"code","message"}}.
     *
     * @param response response cua servlet
     * @param e        ngoai le nghiep vu
     * @throws IOException nếu ghi response lỗi
     */
    protected void writeError(HttpServletResponse response, AppException e)
            throws IOException {
        ErrorCode code = e.getErrorCode();
        writeJson(response, code.getHttpStatus(),
                new ErrorResponse(code.name(), code.getMessage()));
    }

    /**
     * Phương thức writeSystemError: dùng để ghi log lỗi hệ thống và phản hồi lỗi 500 an toàn (không lộ chi tiết stack trace), phục vụ bảo mật và ổn định hệ thống.
     *
     * Ghi lỗi hệ thống, không lộ stack trace ra ngoài (CLAUDE.md mục 11).
     *
     * @param response response cua servlet
     * @param e        ngoai le goc
     * @throws IOException nếu ghi response lỗi
     */
    protected void writeSystemError(HttpServletResponse response, Exception e)
            throws IOException {
        LOG.log(Level.SEVERE, "Loi he thong khi xu ly request", e);
        ErrorCode code = ErrorCode.LOI_HE_THONG;
        writeJson(response, code.getHttpStatus(),
                new ErrorResponse(code.name(), code.getMessage()));
    }
}
