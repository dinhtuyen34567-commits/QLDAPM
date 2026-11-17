package com.shipping.service;

import com.shipping.model.TrackingInfo;

/**
 * Nghiệp vụ UC-02: tra cứu trạng thái vận đơn.
 */
public interface TrackingService {

    /**
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
