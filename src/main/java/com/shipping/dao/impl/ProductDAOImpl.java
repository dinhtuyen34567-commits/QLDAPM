/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ProductDAOImpl.java
 * Lần cuối cập nhật: 06/10/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực ProductDAO, truy vấn danh sách mặt hàng và lấy thông tin chi tiết các mặt hàng theo danh sách ID.
 */
package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.ProductDAO;
import com.shipping.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Triển khai {@link ProductDAO} bằng JDBC thuần.
 */
public class ProductDAOImpl implements ProductDAO {

    private static final String SQL_FIND_ALL =
            "SELECT id, maHang, tenHang, loaiHang, khoiLuong, donGiaCuoc "
            + "FROM tblHangHoa ORDER BY id";

    private static final String SQL_FIND_BY_IDS =
            "SELECT id, maHang, tenHang, loaiHang, khoiLuong, donGiaCuoc "
            /**
             * Phương thức IN: dùng để thực thi thao tác IN, phục vụ tiến trình xử lý nghiệp vụ của lớp ProductDAOImpl.
             */
            + "FROM tblHangHoa WHERE id IN (%s)";

    private final ConnectionProvider connectionProvider;

    /**
     * Phương thức ProductDAOImpl: dùng để khởi tạo đối tượng ProductDAOImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public ProductDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /**
     * Phương thức findAll: dùng để truy vấn toàn bộ danh sách bản ghi có trong CSDL, phục vụ hiển thị danh mục cho người dùng.
     */
    @Override
    public List<Product> findAll() {
        List<Product> result = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi lay danh sach hang hoa", e);
        }
        return result;
    }

    /**
     * Phương thức findByIds: dùng để truy vấn danh sách mặt hàng dựa trên danh sách các mã định danh ID, phục vụ tính toán khối lượng và cước phí đơn hàng.
     */
    @Override
    public Map<Integer, Product> findByIds(Collection<Integer> ids) {
        Map<Integer, Product> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        // Chi lay cac id la so nguyen duoc client gui len; tao placeholder thong qua
        // vi tri de tranh ghep chuoi SQL truc tiep (CLAUDE.md muc 11).
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                placeholders.append(',');
            }
            placeholders.append('?');
        }
        String sql = String.format(SQL_FIND_BY_IDS, placeholders);

        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int index = 1;
            for (Integer id : ids) {
                ps.setInt(index++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapRow(rs);
                    result.put(p.getId(), p);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi lay hang hoa theo danh sach id", e);
        }
        return result;
    }

    /**
     * Phương thức mapRow: dùng để thực thi thao tác mapRow, phục vụ tiến trình xử lý nghiệp vụ của lớp ProductDAOImpl.
     *
     * Chuyển một dòng kết quả truy vấn sang đối tượng {@link Product}.
     */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("maHang"));
        p.setName(rs.getString("tenHang"));
        p.setCategory(rs.getString("loaiHang"));
        p.setWeight(rs.getBigDecimal("khoiLuong"));
        p.setFreightRate(rs.getBigDecimal("donGiaCuoc"));
        return p;
    }
}
