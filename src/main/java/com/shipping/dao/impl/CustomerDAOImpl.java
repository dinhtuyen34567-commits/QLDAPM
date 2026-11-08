package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.CustomerDAO;
import com.shipping.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Triển khai {@link CustomerDAO} bằng JDBC thuần.
 *
 * <p>Chỉ người dùng có bản ghi trong {@code tblKhachHang} mới được coi là khách
 * hàng; nếu không có bản ghi đó thì trả về {@link Optional#empty()} để service
 * trả lỗi {@code KHONG_CO_QUYEN}.</p>
 */
public class CustomerDAOImpl implements CustomerDAO {

    private static final String SQL_FIND_BY_ID =
            "SELECT n.id, n.hoTen, n.sdt "
            + "FROM tblNguoiDung n "
            + "INNER JOIN tblKhachHang k ON k.nguoiDungId = n.id "
            + "WHERE n.id = ?";

    private final ConnectionProvider connectionProvider;

    public CustomerDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Customer> findById(long userId) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id"));
                    customer.setFullName(rs.getString("hoTen"));
                    customer.setPhone(rs.getString("sdt"));
                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi lay thong tin khach hang", e);
        }
        return Optional.empty();
    }
}
