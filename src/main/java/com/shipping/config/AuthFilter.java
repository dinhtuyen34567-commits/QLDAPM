package com.shipping.config;

import com.shipping.model.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
 * Kiem tra nguoi dung da dang nhap va co vai tro khach hang truoc khi vao API.
 *
 * <p>Day la tien dieu kien "khach hang da dang nhap" cua UC-01 (BR-01). Cac API
 * tra JSON nen khi thieu quyen cung tra JSON chu khong phai trang loi HTML.</p>
 */
@WebFilter(urlPatterns = "/api/*")
public class AuthFilter implements Filter {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    /** Cac duong dan khong yeu cau dang nhap (dev-login tam thoi). */
    private static final String DEV_LOGIN_PATH = "/dev-login";

    @Override
    public void init(jakarta.servlet.FilterConfig config) {
        // Khong can cau hinh
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();

        // Duong dan dev-login tam thoi van qua duoc khi duoc bat
        if (DEV_LOGIN_PATH.equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!SessionUtils.isCustomer(httpRequest)) {
            writeUnauthorized(httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Tra ve 401 kem thong bao nghiep vu.
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(GSON.toJson(new ErrorResponse(
                "KHONG_CO_QUYEN", "Phiên hết hạn hoặc không phải khách hàng")));
    }

    @Override
    public void destroy() {
        // Khong tai nguyen can giai phong
    }
}
