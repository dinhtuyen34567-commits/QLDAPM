package com.shipping.dao.impl;

import com.shipping.config.ConnectionProvider;
import com.shipping.dao.TrackingDAO;
import com.shipping.model.TrackingInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai {@link TrackingDAO} bằng JDBC thuần.
 */
public class TrackingDAOImpl implements TrackingDAO {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Giới hạn số điểm tọa độ trả về để nhẹ response và dễ vẽ bản đồ. */
    private static final int MAX_LOCATION_POINTS = 50;

    private static final String SQL_FIND_ORDER =
            "SELECT o.maVanDon, o.ngayTao, o.trangThai, o.tongKhoiLuong, "
            + "o.tenNguoiNhan, o.diaChiGiao, o.chuyenXeId, "
            + "c.maChuyen, c.thoiGianDi, c.xeTaiId, "
            + "x.bienSo, x.trangThaiXe, "
            + "tx.hoTen AS tenTaiXe, "
            + "lt.maLoTrinh, lt.khoangCach "
            + "FROM tblDonHang o "
            + "LEFT JOIN tblChuyenXe c ON c.id = o.chuyenXeId "
            + "LEFT JOIN tblXeTai x ON x.id = c.xeTaiId "
            + "LEFT JOIN tblNguoiDung tx ON tx.id = c.taiXeId "
            + "LEFT JOIN tblLoTrinh lt ON lt.chuyenXeId = c.id "
            + "WHERE o.khachHangId = ? AND o.maVanDon = ?";

    private static final String SQL_FIND_LOCATIONS =
            "SELECT viDo, kinhDo, thoiGian "
            + "FROM tblNhatKyToaDo WHERE xeTaiId = ? "
            + "ORDER BY thoiGian DESC, id DESC LIMIT " + MAX_LOCATION_POINTS;

    private final ConnectionProvider connectionProvider;

    public TrackingDAOImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<TrackingInfo> findByTrackingCode(long customerId, String trackingCode) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_FIND_ORDER)) {
            ps.setLong(1, customerId);
            ps.setString(2, trackingCode);

            TrackingInfo info;
            Integer vehicleId;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                info = mapOrder(rs);
                vehicleId = nullableInt(rs, "xeTaiId");
            }

            // Dong ResultSet cua don hang truoc khi query tiep tren cung connection.
            if (vehicleId != null) {
                loadLocations(c, vehicleId, info);
            }
            return Optional.of(info);
        } catch (SQLException e) {
            throw new IllegalStateException("Loi khi tra cuu van don", e);
        }
    }

    private TrackingInfo mapOrder(ResultSet rs) throws SQLException {
        TrackingInfo info = new TrackingInfo();
        info.setTrackingCode(rs.getString("maVanDon"));
        info.setCreatedAt(formatTimestamp(rs.getTimestamp("ngayTao")));
        info.setStatus(rs.getString("trangThai"));
        info.setTotalWeight(rs.getBigDecimal("tongKhoiLuong"));
        info.setReceiverName(rs.getString("tenNguoiNhan"));
        info.setDeliveryAddress(rs.getString("diaChiGiao"));

        String tripCode = rs.getString("maChuyen");
        if (tripCode != null) {
            TrackingInfo.Trip trip = new TrackingInfo.Trip();
            trip.setTripCode(tripCode);
            trip.setDepartureTime(formatTimestamp(rs.getTimestamp("thoiGianDi")));
            trip.setLicensePlate(rs.getString("bienSo"));
            trip.setDriverName(rs.getString("tenTaiXe"));
            trip.setVehicleStatus(rs.getString("trangThaiXe"));
            info.setTrip(trip);
        }

        String routeCode = rs.getString("maLoTrinh");
        if (routeCode != null) {
            TrackingInfo.Route route = new TrackingInfo.Route();
            route.setRouteCode(routeCode);
            route.setDistanceKm(rs.getBigDecimal("khoangCach"));
            info.setRoute(route);
        }
        return info;
    }

    private void loadLocations(Connection c, int vehicleId, TrackingInfo info)
            throws SQLException {
        List<TrackingInfo.Location> locations = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(SQL_FIND_LOCATIONS)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TrackingInfo.Location location = new TrackingInfo.Location();
                    location.setLatitude(rs.getBigDecimal("viDo"));
                    location.setLongitude(rs.getBigDecimal("kinhDo"));
                    location.setRecordedAt(formatTimestamp(rs.getTimestamp("thoiGian")));
                    locations.add(location);
                }
            }
        }
        info.setLocations(locations);
        if (!locations.isEmpty()) {
            info.setCurrentLocation(locations.get(0));
        }
    }

    private static Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static String formatTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().format(DATE_TIME_FORMAT);
    }
}
