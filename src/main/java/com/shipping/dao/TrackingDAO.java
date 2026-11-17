package com.shipping.dao;

import com.shipping.model.TrackingInfo;

import java.util.Optional;

/**
 * Truy vấn dữ liệu phục vụ UC-02 Tra cứu vận đơn.
 */
public interface TrackingDAO {

    /**
     * Tìm đơn hàng thuộc một khách hàng theo mã vận đơn, kèm chuyến xe,
     * lộ trình và nhật ký tọa độ mới nhất.
     *
     * @param customerId   id khách hàng đang đăng nhập
     * @param trackingCode mã vận đơn đã được chuẩn hóa
     * @return thông tin tra cứu, hoặc {@link Optional#empty()} nếu không có
     */
    Optional<TrackingInfo> findByTrackingCode(long customerId, String trackingCode);
}
