/**
 * Người code: Nguyễn Đình Tuyển (UC-02)
 * Tên file: TrackingDAO.java
 * Lần cuối cập nhật: 18/10/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện khai báo các thao tác truy vấn CSDL phục vụ chức năng tra cứu thông tin hành trình đơn hàng theo mã vận đơn.
 */
package com.shipping.dao;

import com.shipping.model.TrackingInfo;

import java.util.Optional;

/**
 * Truy vấn dữ liệu phục vụ UC-02 Tra cứu vận đơn.
 */
public interface TrackingDAO {

    /**
     * Phương thức findByTrackingCode: dùng để tìm kiếm thông tin đơn hàng theo mã vận đơn và mã khách hàng, phục vụ việc tra cứu và đối soát đơn hàng.
     *
     * Tìm đơn hàng thuộc một khách hàng theo mã vận đơn, kèm chuyến xe,
     * lộ trình và nhật ký tọa độ mới nhất.
     *
     * @param customerId   id khách hàng đang đăng nhập
     * @param trackingCode mã vận đơn đã được chuẩn hóa
     * @return thông tin tra cứu, hoặc {@link Optional#empty()} nếu không có
     */
    Optional<TrackingInfo> findByTrackingCode(long customerId, String trackingCode);
}
