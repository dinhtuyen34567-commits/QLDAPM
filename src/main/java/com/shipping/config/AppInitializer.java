package com.shipping.config;

import com.shipping.dao.CustomerDAO;
import com.shipping.dao.OrderDAO;
import com.shipping.dao.ProductDAO;
import com.shipping.dao.impl.CustomerDAOImpl;
import com.shipping.dao.impl.OrderDAOImpl;
import com.shipping.dao.impl.ProductDAOImpl;
import com.shipping.service.OrderService;
import com.shipping.service.impl.OrderServiceImpl;
import com.shipping.util.OrderUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Khoi dong ung dung: tao pool ket noi, tao DAO va service, gan vao
 * {@link ServletContext} de cac servlet lay dung instance.
 *
 * <p>Vien {@code @WebServlet} duoc Tomcat khoi tao bang constructor khong tham so,
 * nen thuoc tinh cua servlet phai duoc gan sau khi listener da chay. Cac servlet
 * doc dependency tu {@link ServletContext} trong {@code init()}.</p>
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppInitializer.class.getName());

    /** Khoa cong dung chua tao cac bean. */
    public static final String ATTR_ORDER_SERVICE = "orderService";
    public static final String ATTR_PRODUCT_DAO = "productDAO";
    public static final String ATTR_CUSTOMER_DAO = "customerDAO";

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

        ctx.setAttribute(ATTR_ORDER_SERVICE, orderService);
        ctx.setAttribute(ATTR_PRODUCT_DAO, productDAO);
        ctx.setAttribute(ATTR_CUSTOMER_DAO, customerDAO);

        LOG.info("Khoi dong ung dung QLDAPM-ShippingOrder (UC-01) thanh cong.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConfig.shutdown();
        LOG.info("Da dong ung dung QLDAPM-ShippingOrder.");
    }
}
