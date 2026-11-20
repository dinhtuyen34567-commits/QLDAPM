/**
 * Người code: Phan Tiến Dũng (UC-02)
 * Tên file: TrackingServiceImplTest.java
 * Lần cuối cập nhật: 27/10/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Lớp kiểm thử đơn vị cho TrackingServiceImpl, kiểm tra các kịch bản tra cứu thành công, mã không tồn tại hoặc không đúng quyền sở hữu.
 */
package com.shipping.service;

import com.shipping.dao.TrackingDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.TrackingInfo;
import com.shipping.service.impl.TrackingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Kiem thu tang Service cho UC-02: tra cuu van don.
 */
@ExtendWith(MockitoExtension.class)
class TrackingServiceImplTest {

    private static final long CUSTOMER_ID = 1L;
    private static final String VALID_CODE = "VN12345678";

    @Mock
    private TrackingDAO trackingDAO;

    private TrackingServiceImpl service;

    /**
     * Phương thức setUp: dùng để khởi tạo cấu hình môi trường kiểm thử và nạp các mock object trước khi chạy mỗi ca test, phục vụ thiết lập môi trường test độc lập.
     */
    @BeforeEach
    void setUp() {
        service = new TrackingServiceImpl(trackingDAO);
    }

    /**
     * Phương thức tc20_traCuuThanhCong: dùng để thực thi thao tác tc20_traCuuThanhCong, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-20: tra cuu thanh cong, gan nhan va tien do")
    void tc20_traCuuThanhCong() {
        TrackingInfo info = info("DANG_VAN_CHUYEN");
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.of(info));

        TrackingInfo result = service.track(CUSTOMER_ID, VALID_CODE);

        assertEquals(VALID_CODE, result.getTrackingCode());
        assertEquals("Đang vận chuyển", result.getStatusLabel());
        assertEquals(65, result.getProgressPercent());
        verify(trackingDAO).findByTrackingCode(CUSTOMER_ID, VALID_CODE);
    }

    /**
     * Phương thức tc21_chuanHoaMa: dùng để thực thi thao tác tc21_chuanHoaMa, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-21: chuan hoa khoang trang va chu thuong")
    void tc21_chuanHoaMa() {
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.of(info("CHO_LAY")));

        TrackingInfo result = service.track(CUSTOMER_ID, "  vn12345678  ");

        assertEquals("Chờ lấy hàng", result.getStatusLabel());
        assertEquals(10, result.getProgressPercent());
        verify(trackingDAO).findByTrackingCode(CUSTOMER_ID, VALID_CODE);
    }

    /**
     * Phương thức tc22_maSaiDinhDang: dùng để thực thi thao tác tc22_maSaiDinhDang, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-22: ma van don sai dinh dang")
    void tc22_maSaiDinhDang() {
        String[] invalidCodes = {null, "", "   ", "VN123", "AB12345678", "VN1234567A"};

        for (String code : invalidCodes) {
            AppException ex = assertThrows(AppException.class,
                    () -> service.track(CUSTOMER_ID, code));
            assertEquals(ErrorCode.MA_VAN_DON_KHONG_HOP_LE, ex.getErrorCode());
        }
        verifyNoInteractions(trackingDAO);
    }

    /**
     * Phương thức tc23_khongTimThay: dùng để thực thi thao tác tc23_khongTimThay, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-23: khong tim thay van don")
    void tc23_khongTimThay() {
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.track(CUSTOMER_ID, VALID_CODE));

        assertEquals(ErrorCode.KHONG_TIM_THAY_VAN_DON, ex.getErrorCode());
    }

    /**
     * Phương thức tc24_tienDoTheoTrangThai: dùng để thực thi thao tác tc24_tienDoTheoTrangThai, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-24: tien do tuong ung voi tung trang thai")
    void tc24_tienDoTheoTrangThai() {
        assertProgress("CHO_LAY", "Chờ lấy hàng", 10);
        assertProgress("TRONG_KHO", "Đang ở kho", 35);
        assertProgress("DANG_VAN_CHUYEN", "Đang vận chuyển", 65);
        assertProgress("DA_GIAO", "Đã giao", 100);
        assertProgress("DA_HUY", "Đã hủy", 0);
    }

    /**
     * Phương thức tc25_trangThaiLa: dùng để thực thi thao tác tc25_trangThaiLa, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-25: trang thai la khong lam hong trang tra cuu")
    void tc25_trangThaiLa() {
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.of(info("KHONG_RO")));

        TrackingInfo result = service.track(CUSTOMER_ID, VALID_CODE);

        assertEquals("KHONG_RO", result.getStatusLabel());
        assertEquals(0, result.getProgressPercent());
    }

    /**
     * Phương thức tc26_trangThaiNull: dùng để thực thi thao tác tc26_trangThaiNull, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    @Test
    @DisplayName("TC-26: trang thai null duoc xu ly an toan")
    void tc26_trangThaiNull() {
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.of(info(null)));

        TrackingInfo result = service.track(CUSTOMER_ID, VALID_CODE);

        assertEquals("", result.getStatusLabel());
        assertEquals(0, result.getProgressPercent());
    }

    /**
     * Phương thức assertProgress: dùng để thực thi thao tác assertProgress, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    private void assertProgress(String status, String label, int progress) {
        when(trackingDAO.findByTrackingCode(CUSTOMER_ID, VALID_CODE))
                .thenReturn(Optional.of(info(status)));

        TrackingInfo result = service.track(CUSTOMER_ID, VALID_CODE);

        assertEquals(label, result.getStatusLabel());
        assertEquals(progress, result.getProgressPercent());
    }

    /**
     * Phương thức info: dùng để thực thi thao tác info, phục vụ tiến trình xử lý nghiệp vụ của lớp TrackingServiceImplTest.
     */
    private static TrackingInfo info(String status) {
        TrackingInfo info = new TrackingInfo();
        info.setTrackingCode(VALID_CODE);
        info.setStatus(status);
        return info;
    }
}
