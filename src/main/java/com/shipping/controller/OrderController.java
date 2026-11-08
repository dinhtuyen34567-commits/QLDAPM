package com.shipping.controller;

import com.google.gson.JsonSyntaxException;
import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.exception.AppException;
import com.shipping.model.CreateOrderRequest;
import com.shipping.model.CreateResult;
import com.shipping.model.Customer;
import com.shipping.model.Order;
import com.shipping.model.Quote;
import com.shipping.service.OrderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller phuc vu UC-01: ba API nghiep vu va hai trang giao dien.
 *
 * <ul>
 *   <li>{@code GET  /api/v1/khach-hang/me} - thong tin nguoi gui</li>
 *   <li>{@code POST /api/v1/don-hang/tinh-cuoc} - tinh cuoc</li>
 *   <li>{@code POST /api/v1/don-hang} - tao don hang</li>
 *   <li>{@code GET  /don-hang/tao} - man hinh tao don</li>
 *   <li>{@code GET  /don-hang/thanh-cong} - man hinh thanh cong</li>
 * </ul>
 */
@WebServlet(urlPatterns = {
        "/api/v1/khach-hang/me",
        "/api/v1/don-hang/tinh-cuoc",
        "/api/v1/don-hang",
        "/don-hang/tao",
        "/don-hang/thanh-cong"
})
public class OrderController extends BaseController {

    /** Header chong tao trung don (BR-13). */
    private static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";

    /** Duong dan man hinh tao don. */
    private static final String VIEW_CREATE_ORDER = "/views/create-order.jsp";

    /** Duong dan man hinh thanh cong. */
    private static final String VIEW_ORDER_SUCCESS = "/views/order-success.jsp";

    private OrderService orderService;

    @Override
    public void init() {
        // @WebServlet yeu cau constructor khong tham so nen service duoc lay
        // tu ServletContext sau khi AppInitializer da khoi tao.
        // Servlet mac dinh la lazy nen init() chay o request dau tien, luc do
        // listener da chay xong; van de lay lai o getOrderService() de an toan.
        orderService = (OrderService) getServletContext()
                .getAttribute(AppInitializer.ATTR_ORDER_SERVICE);
    }

    /**
     * Lay service tu ServletContext, dam bao da duoc khoi tao.
     *
     * @return service xu ly nghiep vu
     */
    private OrderService getOrderService() {
        if (orderService == null) {
            orderService = (OrderService) getServletContext()
                    .getAttribute(AppInitializer.ATTR_ORDER_SERVICE);
        }
        if (orderService == null) {
            throw new IllegalStateException("Chua khoi tao OrderService");
        }
        return orderService;
    }

    /**
     * GET /api/v1/khach-hang/me - thong tin nguoi gui de dien va khoa hai o.
     */
    private void handleGetMe(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long customerId = SessionUtils.requireCustomerId(request);
        Customer customer = getOrderService().getSender(customerId);
        writeJson(response, HttpServletResponse.SC_OK, customer);
    }

    /**
     * POST /api/v1/don-hang/tinh-cuoc - tinh tong khoi luong va tong tien.
     */
    private void handleQuote(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUtils.requireCustomerId(request);

        CreateOrderRequest body = parseBody(request);
        List<com.shipping.model.ItemRequest> items = body.getItems();
        Quote quote = getOrderService().quote(items);
        writeJson(response, HttpServletResponse.SC_OK, quote);
    }

    /**
     * POST /api/v1/don-hang - tao don hang moi.
     *
     * <p>Tra 201 khi tao moi, 200 khi gui lai cung {@code Idempotency-Key}.</p>
     */
    private void handleCreateOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long customerId = SessionUtils.requireCustomerId(request);

        CreateOrderRequest body = parseBody(request);
        String idempotencyKey = request.getHeader(HEADER_IDEMPOTENCY_KEY);

        // Kiem tra truoc khi ghi de tra 200 khi idempotent
        boolean existed = idempotencyKey != null && !idempotencyKey.isBlank()
                && getOrderService().findOwnedOrderByIdempotencyKey(customerId, idempotencyKey)
                        .isPresent();

        CreateResult result = getOrderService().createOrder(customerId, body, idempotencyKey);

        writeJson(response, existed
                ? HttpServletResponse.SC_OK
                : HttpServletResponse.SC_CREATED, result);
    }

    /**
     * GET /don-hang/tao - chuyen toi man hinh tao don.
     */
    private void handleCreatePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtils.requireCustomerId(request);
        request.getRequestDispatcher(VIEW_CREATE_ORDER).forward(request, response);
    }

    /**
     * GET /don-hang/thanh-cong?maVanDon=... - man hinh thanh cong.
     *
     * <p>Chi hien thi don thuoc khach hang dang dang nhap; neu khong thi quay
     * ve trang chinh de khong lo thong tin don cua nguoi khac.</p>
     */
    private void handleSuccessPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long customerId = SessionUtils.requireCustomerId(request);

        String trackingCode = request.getParameter("maVanDon");
        Optional<Order> order = getOrderService().findOwnedOrder(customerId, trackingCode);

        if (order.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("donHang", order.get());
        request.getRequestDispatcher(VIEW_ORDER_SUCCESS).forward(request, response);
    }

    /**
     * Doc va phan tich JSON body cua request.
     *
     * @param request request hien tai
     * @return doi tuong da phan tich
     * @throws IOException nếu body khong phai JSON hop le
     */
    private CreateOrderRequest parseBody(HttpServletRequest request) throws IOException {
        String raw = new String(request.getInputStream().readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        if (raw == null || raw.isBlank()) {
            throw new AppException(com.shipping.exception.ErrorCode.THIEU_THONG_TIN);
        }
        try {
            CreateOrderRequest body = GSON.fromJson(raw, CreateOrderRequest.class);
            if (body == null) {
                throw new AppException(com.shipping.exception.ErrorCode.THIEU_THONG_TIN);
            }
            return body;
        } catch (JsonSyntaxException e) {
            throw new AppException(com.shipping.exception.ErrorCode.THIEU_THONG_TIN);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Cac duong dan trang: thieu quyen thi quay ve trang chinh thay vi
        // tra JSON (nguoi dung khong phai la may goi API).
        if (path.startsWith("/don-hang/")) {
            if (!SessionUtils.isCustomer(request)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            try {
                switch (path) {
                    case "/don-hang/tao":
                        handleCreatePage(request, response);
                        break;
                    case "/don-hang/thanh-cong":
                        handleSuccessPage(request, response);
                        break;
                    default:
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (Exception e) {
                writeSystemError(response, e);
            }
            return;
        }

        try {
            switch (path) {
                case "/api/v1/khach-hang/me":
                    handleGetMe(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String path = request.getServletPath();
        try {
            switch (path) {
                case "/api/v1/don-hang/tinh-cuoc":
                    handleQuote(request, response);
                    break;
                case "/api/v1/don-hang":
                    handleCreateOrder(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }
}
