/**
 * Người code: Nguyễn Đình Tuyển (UC-02)
 * Tên file: TrackingService.java
 * Lần cuối cập nhật: 22/10/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện định nghĩa nghiệp vụ tra cứu hành trình và trạng thái vận đơn cho khách hàng theo mã vận đơn.
 */
package com.shipping.service;

import com.shipping.model.TrackingInfo;

/**
 * Nghiệp vụ UC-02: tra cứu trạng thái vận đơn.
 */
public interface TrackingService {

    /**
     * Phương thức track: dùng để thực hiện nghiệp vụ tra cứu trạng thái và toàn bộ hành trình vận chuyển của đơn hàng theo mã vận đơn, phục vụ UC-02.
     *
     * Tra cứu vận đơn của khách hàng đang đăng nhập.
     *
     * @param customerId   id khách hàng lấy từ phiên
     * @param trackingCode mã vận đơn người dùng nhập
     * @return thông tin đơn hàng, chuyến xe, lộ trình và tọa độ
     * @throws com.shipping.exception.AppException nếu mã sai định dạng
     *         hoặc không tìm thấy đơn thuộc khách hàng này
     */
    TrackingInfo track(long customerId, String trackingCode);
}
