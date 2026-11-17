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

    @Override
    public void init() {
        trackingService = (TrackingService) getServletContext()
                .getAttribute(AppInitializer.ATTR_TRACKING_SERVICE);
    }

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
