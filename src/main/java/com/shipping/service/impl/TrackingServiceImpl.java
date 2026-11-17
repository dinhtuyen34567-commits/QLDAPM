package com.shipping.service.impl;

import com.shipping.dao.TrackingDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.OrderStatus;
import com.shipping.model.TrackingInfo;
import com.shipping.service.TrackingService;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Triển khai {@link TrackingService}.
 *
 * <p>Mã vận đơn do UC-01 sinh theo định dạng {@code VN + 8 chữ số}. Service
 * chuẩn hóa chữ thường/khoảng trắng trước khi kiểm tra và truy vấn.</p>
 */
public class TrackingServiceImpl implements TrackingService {

    private static final Pattern TRACKING_CODE_PATTERN = Pattern.compile("^VN[0-9]{8}$");

    private final TrackingDAO trackingDAO;

    public TrackingServiceImpl(TrackingDAO trackingDAO) {
        this.trackingDAO = trackingDAO;
    }

    @Override
    public TrackingInfo track(long customerId, String trackingCode) {
        String code = normalize(trackingCode);
        if (code == null || !TRACKING_CODE_PATTERN.matcher(code).matches()) {
            throw new AppException(ErrorCode.MA_VAN_DON_KHONG_HOP_LE);
        }

        TrackingInfo info = trackingDAO.findByTrackingCode(customerId, code)
                .orElseThrow(() -> new AppException(ErrorCode.KHONG_TIM_THAY_VAN_DON));

        applyStatus(info);
        return info;
    }

    /**
     * Chuẩn hóa mã vận đơn: bỏ khoảng trắng đầu/cuối, chuyển chữ hoa.
     */
    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    /**
     * Gắn nhãn tiếng Việt và tiến độ hiển thị cho trạng thái đơn.
     */
    private static void applyStatus(TrackingInfo info) {
        String rawStatus = info.getStatus();
        if (rawStatus == null) {
            info.setStatusLabel("");
            info.setProgressPercent(0);
            return;
        }
        try {
            OrderStatus status = OrderStatus.fromDb(rawStatus);
            info.setStatusLabel(status.getLabel());
            info.setProgressPercent(progressOf(status));
        } catch (IllegalArgumentException e) {
            // Dữ liệu cũ/lạ không được làm hỏng trang tra cứu.
            info.setStatusLabel(rawStatus);
            info.setProgressPercent(0);
        }
    }

    private static int progressOf(OrderStatus status) {
        switch (status) {
            case CHO_LAY:
                return 10;
            case TRONG_KHO:
                return 35;
            case DANG_VAN_CHUYEN:
                return 65;
            case DA_GIAO:
                return 100;
            case DA_HUY:
            default:
                return 0;
        }
    }
}
