package com.shipping.controller;

import com.shipping.config.AppInitializer;
import com.shipping.config.SessionUtils;
import com.shipping.dao.ProductDAO;
import com.shipping.exception.AppException;
import com.shipping.model.Product;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller phuc vu API danh sach hang hoa.
 *
 * <p>API {@code GET /api/v1/hang-hoa} chi tra ve cac truong cong khai
 * ({@code id}, {@code maHang}, {@code tenHang}, {@code loaiHang}, {@code khoiLuong}).
 * Truong {@code donGiaCuoc} <b>khong</b> duoc tra ve cho client, vi don gia
 * phai duoc server tu xac dinh theo cong thuc muc 3.4.</p>
 */
@WebServlet(urlPatterns = "/api/v1/hang-hoa")
public class ProductController extends BaseController {

    private ProductDAO productDAO;

    @Override
    public void init() {
        // @WebServlet yeu cau constructor khong tham so nen DAO duoc lay
        // tu ServletContext sau khi AppInitializer da khoi tao.
        productDAO = (ProductDAO) getServletContext()
                .getAttribute(AppInitializer.ATTR_PRODUCT_DAO);
    }

    /**
     * Lay DAO tu ServletContext, dam bao da duoc khoi tao.
     *
     * @return DAO truy cap bang hang hoa
     */
    private ProductDAO getProductDAO() {
        if (productDAO == null) {
            productDAO = (ProductDAO) getServletContext()
                    .getAttribute(AppInitializer.ATTR_PRODUCT_DAO);
        }
        if (productDAO == null) {
            throw new IllegalStateException("Chua khoi tao ProductDAO");
        }
        return productDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            SessionUtils.requireCustomerId(request);

            List<Product> products = getProductDAO().findAll();
            List<ProductPublic> result = new ArrayList<>();
            for (Product p : products) {
                result.add(new ProductPublic(p));
            }
            writeJson(response, HttpServletResponse.SC_OK, result);
        } catch (AppException e) {
            writeError(response, e);
        } catch (Exception e) {
            writeSystemError(response, e);
        }
    }

    /**
     * Hinh dang phan hoi cua hang hoa: chi giu cac truong cho phep.
     */
    private static final class ProductPublic {

        private final int id;
        private final String maHang;
        private final String tenHang;
        private final String loaiHang;
        private final String khoiLuong;

        private ProductPublic(Product p) {
            this.id = p.getId();
            this.maHang = p.getCode();
            this.tenHang = p.getName();
            this.loaiHang = p.getCategory();
            this.khoiLuong = p.getWeight() == null ? null : p.getWeight().toPlainString();
        }
    }
}
