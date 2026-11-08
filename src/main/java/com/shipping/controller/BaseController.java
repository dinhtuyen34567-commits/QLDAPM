package com.shipping.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.ErrorResponse;

import jakarta.servlet.http.HttpServlet;
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
