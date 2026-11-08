package com.shipping.service;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.CustomerDAO;
import com.shipping.dao.OrderDAO;
import com.shipping.dao.ProductDAO;
import com.shipping.model.Customer;
import com.shipping.model.Product;
import com.shipping.service.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Tien ich dung chung cho {@link OrderServiceTest}: dung sanh mot service với
 * cac dependency gia lap de moi test chi tap trung vao nhanh vi logic can kiem tra.
 */
final class OrderServiceTestSupport {

    static final int CUSTOMER_ID = 1;
    static final BigDecimal VAT_PERCENT = new BigDecimal("10");
    static final int MAX_QUANTITY_PER_LINE = 1000;
    static final int MAX_LINES = 50;
    static final BigDecimal MAX_TOTAL_WEIGHT = new BigDecimal("5000");

    private OrderServiceTestSupport() {
        // Lop tien ich, khong tao the
    }

    /**
     * Tao service voi cac dependency gia lap.
     *
     * @param orderDAO           dao don hang gia lap
     * @param productDAO         dao hang hoa gia lap
     * @param customerDAO        dao khach hang gia lap
     * @param connectionProvider nguon cap ket noi gia lap
     * @param trackingCodeGen    nguon sinh ma van don
     * @param invoiceCodeGen     nguon sinh ma hoa don
     * @return service san dung
     */
    static OrderServiceImpl newService(OrderDAO orderDAO,
                                       ProductDAO productDAO,
                                       CustomerDAO customerDAO,
                                       ConnectionProvider connectionProvider,
                                       Supplier<String> trackingCodeGen,
                                       Supplier<String> invoiceCodeGen) {
        return new OrderServiceImpl(orderDAO, productDAO, customerDAO, connectionProvider,
                trackingCodeGen, invoiceCodeGen, VAT_PERCENT, MAX_QUANTITY_PER_LINE,
                MAX_LINES, MAX_TOTAL_WEIGHT);
    }

    /**
     * Tao service mac dinh: khach hang id 1 luon ton tai, ma van don va ma hoa
     * don sinh tu dong.
     */
    static OrderServiceImpl newService(OrderDAO orderDAO,
                                       ProductDAO productDAO,
                                       CustomerDAO customerDAO,
                                       ConnectionProvider connectionProvider) {
        return newService(orderDAO, productDAO, customerDAO, connectionProvider,
                () -> "VN00000001", () -> "HD00000001");
    }

    /**
     * Tao khach hang gia lap.
     */
    static Customer customer(int id) {
        return new Customer(id, "Nguyễn Văn A", "0912345678");
    }

    /**
     * Tao hang hoa gia lap.
     *
     * @param id          id hang hoa
     * @param weight      khoi luong (kg)
     * @param freightRate don gia cuoc (d/kg)
     */
    static Product product(int id, String weight, String freightRate) {
        return new Product(id, "H" + id, "Hàng " + id, "Loại " + id,
                new BigDecimal(weight), new BigDecimal(freightRate));
    }

    /**
     * Tao map hang hoa gia lap cho {@code ProductDAO.findByIds}.
     */
    static Map<Integer, Product> productMap(Product... products) {
        Map<Integer, Product> map = new HashMap<>();
        for (Product p : products) {
            map.put(p.getId(), p);
        }
        return map;
    }
}
