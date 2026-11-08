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

    @Override
    public void init(jakarta.servlet.FilterConfig config) {
        // Khong can cau hinh
    }

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

    @Override
    public void destroy() {
        // Khong tai nguyen can giai phong
    }
}
