/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: AppInitializer.java
 * Lần cuối cập nhật: 26/09/2026 14:45
 * 
 * Tổng quát chức năng của lớp:
 * ServletContextListener khởi tạo các thành phần cốt lõi của ứng dụng (DataSource, DAO, Service) khi máy chủ khởi động và dọn dẹp tài nguyên khi dừng ứng dụng.
 */
package com.shipping.config;

import com.shipping.dao.CustomerDAO;
import com.shipping.dao.OrderDAO;
import com.shipping.dao.ProductDAO;
import com.shipping.dao.StaffDAO;
import com.shipping.dao.SupportRequestDAO;
import com.shipping.dao.TrackingDAO;
import com.shipping.dao.UserDAO;
import com.shipping.dao.impl.CustomerDAOImpl;
import com.shipping.dao.impl.OrderDAOImpl;
import com.shipping.dao.impl.ProductDAOImpl;
import com.shipping.dao.impl.StaffDAOImpl;
import com.shipping.dao.impl.SupportRequestDAOImpl;
import com.shipping.dao.impl.TrackingDAOImpl;
import com.shipping.dao.impl.UserDAOImpl;
import com.shipping.service.AuthService;
import com.shipping.service.OrderService;
import com.shipping.service.SupportRequestService;
import com.shipping.service.TrackingService;
import com.shipping.service.impl.AuthServiceImpl;
import com.shipping.service.impl.OrderServiceImpl;
import com.shipping.service.impl.SupportRequestServiceImpl;
import com.shipping.service.impl.TrackingServiceImpl;
import com.shipping.util.OrderUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Khởi động ứng dụng: tạo pool kết nối, tạo các DAO và Service, gắn vào
 * {@link ServletContext} để các servlet lấy đúng instance.
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppInitializer.class.getName());

    public static final String ATTR_ORDER_SERVICE = "orderService";
    public static final String ATTR_TRACKING_SERVICE = "trackingService";
    public static final String ATTR_AUTH_SERVICE = "authService";
    public static final String ATTR_SUPPORT_SERVICE = "supportService";
    public static final String ATTR_PRODUCT_DAO = "productDAO";
    public static final String ATTR_CUSTOMER_DAO = "customerDAO";
    public static final String ATTR_USER_DAO = "userDAO";
    public static final String ATTR_STAFF_DAO = "staffDAO";
    public static final String ATTR_SUPPORT_DAO = "supportDAO";

    /**
     * Phương thức contextInitialized: dùng để lắng nghe sự kiện ứng dụng khởi động và tiến hành khởi tạo DataSource, DAO, Service, phục vụ nạp tài nguyên ban đầu.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        ConnectionProvider connectionProvider = () -> {
            Connection c = DBConfig.getDataSource().getConnection();
            return c;
        };

        ProductDAO productDAO = new ProductDAOImpl(connectionProvider);
        CustomerDAO customerDAO = new CustomerDAOImpl(connectionProvider);
        OrderDAO orderDAO = new OrderDAOImpl(connectionProvider);
        TrackingDAO trackingDAO = new TrackingDAOImpl(connectionProvider);
        UserDAO userDAO = new UserDAOImpl(connectionProvider);
        StaffDAO staffDAO = new StaffDAOImpl(connectionProvider);
        SupportRequestDAO supportDAO = new SupportRequestDAOImpl(connectionProvider);

        OrderService orderService = new OrderServiceImpl(
                orderDAO,
                productDAO,
                customerDAO,
                connectionProvider,
                OrderUtils::generateTrackingCode,
                OrderUtils::generateInvoiceCode,
                AppConfig.getBigDecimal("billing.vat-percent", new BigDecimal("10")),
                AppConfig.getInt("order.max-quantity-per-line", 1000),
                AppConfig.getInt("order.max-lines", 50),
                AppConfig.getBigDecimal("order.max-total-weight-kg", new BigDecimal("5000")));

        TrackingService trackingService = new TrackingServiceImpl(trackingDAO);
        AuthService authService = new AuthServiceImpl(userDAO);
        SupportRequestService supportService = new SupportRequestServiceImpl(supportDAO, staffDAO);

        ctx.setAttribute(ATTR_ORDER_SERVICE, orderService);
        ctx.setAttribute(ATTR_TRACKING_SERVICE, trackingService);
        ctx.setAttribute(ATTR_AUTH_SERVICE, authService);
        ctx.setAttribute(ATTR_SUPPORT_SERVICE, supportService);
        ctx.setAttribute(ATTR_PRODUCT_DAO, productDAO);
        ctx.setAttribute(ATTR_CUSTOMER_DAO, customerDAO);
        ctx.setAttribute(ATTR_USER_DAO, userDAO);
        ctx.setAttribute(ATTR_STAFF_DAO, staffDAO);
        ctx.setAttribute(ATTR_SUPPORT_DAO, supportDAO);

        LOG.info("Khởi động ứng dụng QLDAPM-ShippingOrder (UC-01, UC-02, UC-03, Auth) thành công.");
    }

    /**
     * Phương thức contextDestroyed: dùng để lắng nghe sự kiện ứng dụng dừng hoạt động và tiến hành đóng các kết nối, phục vụ dọn dẹp sạch sẽ tài nguyên.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConfig.shutdown();
        LOG.info("Đã đóng ứng dụng QLDAPM-ShippingOrder.");
    }
}
