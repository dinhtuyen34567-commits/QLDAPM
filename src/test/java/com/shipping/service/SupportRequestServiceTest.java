/**
 * Người code: Phan Tiến Dũng (UC-03)
 * Tên file: SupportRequestServiceTest.java
 * Lần cuối cập nhật: 14/11/2026 15:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp kiểm thử đơn vị toàn diện cho SupportRequestServiceImpl, kiểm tra các luồng tạo khiếu nại, phân trang danh sách và cập nhật xử lý.
 */
package com.shipping.service;

import com.shipping.dao.DuplicateKeyException;
import com.shipping.dao.StaffDAO;
import com.shipping.dao.SupportRequestDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.CreateSupportRequest;
import com.shipping.model.StaffDTO;
import com.shipping.model.SupportDetailDTO;
import com.shipping.model.SupportPageResult;
import com.shipping.model.SupportRequest;
import com.shipping.model.SupportStatus;
import com.shipping.model.UpdateSupportRequest;
import com.shipping.service.impl.SupportRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử SupportRequestService - Nghiệp vụ UC-03 (TC-23 đến TC-33)")
class SupportRequestServiceTest {

    @Mock
    private SupportRequestDAO supportDAO;

    @Mock
    private StaffDAO staffDAO;

    private SupportRequestService service;
    private final StaffDTO sampleStaff = new StaffDTO(2L, "Nguyễn Thị CSKH", "Chăm sóc khách hàng");

    /**
     * Phương thức setUp: dùng để khởi tạo cấu hình môi trường kiểm thử và nạp các mock object trước khi chạy mỗi ca test, phục vụ thiết lập môi trường test độc lập.
     */
    @BeforeEach
    void setUp() {
        service = new SupportRequestServiceImpl(supportDAO, staffDAO, () -> "YC12345678");
    }

    /**
     * Phương thức tc23_createSupportRequest_Success: dùng để thực thi thao tác tc23_createSupportRequest_Success, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-23: Khách hàng gửi yêu cầu hỗ trợ hợp lệ (BR-20, BR-21)")
    void tc23_createSupportRequest_Success() {
        long customerId = 1L;
        String trackingCode = "VN12345678";
        CreateSupportRequest req = new CreateSupportRequest(trackingCode, "Hàng giao chậm trễ 2 ngày");

        when(supportDAO.findOrderIdByTrackingCode(trackingCode, customerId)).thenReturn(Optional.of(9001L));
        when(supportDAO.insert(isNull(), any(SupportRequest.class))).thenReturn(100L);

        SupportRequest result = service.createRequest(customerId, req);

        assertNotNull(result);
        assertEquals("YC12345678", result.getMaYeuCau());
        assertEquals("Hàng giao chậm trễ 2 ngày", result.getNoiDung());
        assertEquals(SupportStatus.MOI.name(), result.getTrangThai());
        assertEquals(customerId, result.getKhachHangId());
        assertEquals(9001L, result.getDonHangId());
        assertNull(result.getNhanVienId());
        verify(supportDAO).insert(isNull(), any(SupportRequest.class));
    }

    /**
     * Phương thức tc24a_createSupportRequest_EmptyContent: dùng để thực thi thao tác tc24a_createSupportRequest_EmptyContent, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-24a: Gửi khiếu nại với nội dung rỗng (BR-20)")
    void tc24a_createSupportRequest_EmptyContent() {
        CreateSupportRequest req1 = new CreateSupportRequest("VN12345678", "");
        CreateSupportRequest req2 = new CreateSupportRequest("VN12345678", "   ");

        AppException ex1 = assertThrows(AppException.class, () -> service.createRequest(1L, req1));
        assertEquals(ErrorCode.NOI_DUNG_RONG, ex1.getErrorCode());

        AppException ex2 = assertThrows(AppException.class, () -> service.createRequest(1L, req2));
        assertEquals(ErrorCode.NOI_DUNG_RONG, ex2.getErrorCode());

        verify(supportDAO, never()).insert(any(), any());
    }

    /**
     * Phương thức tc24b_createSupportRequest_ContentTooLong: dùng để thực thi thao tác tc24b_createSupportRequest_ContentTooLong, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-24b: Gửi khiếu nại với nội dung vượt quá 2.000 ký tự (BR-20)")
    void tc24b_createSupportRequest_ContentTooLong() {
        String longContent = "A".repeat(2001);
        CreateSupportRequest req = new CreateSupportRequest("VN12345678", longContent);

        AppException ex = assertThrows(AppException.class, () -> service.createRequest(1L, req));
        assertEquals(ErrorCode.NOI_DUNG_QUA_DAI, ex.getErrorCode());
        verify(supportDAO, never()).insert(any(), any());
    }

    /**
     * Phương thức tc25_createSupportRequest_OtherCustomerOrder: dùng để thực thi thao tác tc25_createSupportRequest_OtherCustomerOrder, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-25: Khiếu nại mã vận đơn không tồn tại hoặc của khách hàng khác (BR-15, BR-20)")
    void tc25_createSupportRequest_OtherCustomerOrder() {
        String trackingCode = "VN99999999";
        CreateSupportRequest req = new CreateSupportRequest(trackingCode, "Hàng vỡ hộp");

        when(supportDAO.findOrderIdByTrackingCode(trackingCode, 1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> service.createRequest(1L, req));
        assertEquals(ErrorCode.VAN_DON_KHONG_TON_TAI, ex.getErrorCode());
        verify(supportDAO, never()).insert(any(), any());
    }

    /**
     * Phương thức tc26_staffEndpoints_ForbiddenForNonStaff: dùng để thực thi thao tác tc26_staffEndpoints_ForbiddenForNonStaff, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-26: Tài khoản không có vai trò nhân viên gọi chức năng CSKH (BR-22)")
    void tc26_staffEndpoints_ForbiddenForNonStaff() {
        long nonStaffId = 999L;
        when(staffDAO.findById(nonStaffId)).thenReturn(Optional.empty());

        AppException ex1 = assertThrows(AppException.class, () -> service.getStaffMe(nonStaffId));
        assertEquals(ErrorCode.KHONG_CO_QUYEN, ex1.getErrorCode());

        AppException ex2 = assertThrows(AppException.class, () -> service.getSupportList(nonStaffId, null, 1));
        assertEquals(ErrorCode.KHONG_CO_QUYEN, ex2.getErrorCode());

        AppException ex3 = assertThrows(AppException.class, () -> service.getSupportDetail(nonStaffId, 1L));
        assertEquals(ErrorCode.KHONG_CO_QUYEN, ex3.getErrorCode());
    }

    /**
     * Phương thức tc27_getSupportList_PaginationAndFilter: dùng để thực thi thao tác tc27_getSupportList_PaginationAndFilter, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-27: Nhân viên lấy danh sách yêu cầu có phân trang và lọc (BR-23)")
    void tc27_getSupportList_PaginationAndFilter() {
        when(staffDAO.findById(2L)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.count("MOI")).thenReturn(45);
        when(supportDAO.findByPage("MOI", 0, 20)).thenReturn(Collections.emptyList());

        SupportPageResult result = service.getSupportList(2L, "MOI", 1);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(20, result.getPageSize());
        assertEquals(45, result.getTotal());
        assertEquals(3, result.getTotalPages());
        verify(supportDAO).findByPage("MOI", 0, 20);
    }

    /**
     * Phương thức tc28_updateSupportRequest_ClaimSuccess: dùng để thực thi thao tác tc28_updateSupportRequest_ClaimSuccess, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-28: Nhân viên nhận xử lý yêu cầu trạng thái MOI (BR-24, BR-25)")
    void tc28_updateSupportRequest_ClaimSuccess() {
        long staffId = 2L;
        long reqId = 10L;
        UpdateSupportRequest updateReq = new UpdateSupportRequest("DANG_XU_LY", "Đã tiếp nhận kiểm tra");

        SupportDetailDTO existing = createMockDetail(reqId, "MOI", null, null);
        SupportDetailDTO updated = createMockDetail(reqId, "DANG_XU_LY", staffId, "Đã tiếp nhận kiểm tra");

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(existing)).thenReturn(Optional.of(updated));
        when(supportDAO.updateWithCondition(reqId, "DANG_XU_LY", "Đã tiếp nhận kiểm tra", staffId)).thenReturn(true);

        SupportDetailDTO res = service.updateSupportRequest(staffId, reqId, updateReq);

        assertNotNull(res);
        assertEquals("DANG_XU_LY", res.getTrangThai());
        assertEquals(staffId, res.getNhanVienId());
        assertEquals("Đã tiếp nhận kiểm tra", res.getPhanHoi());
    }

    /**
     * Phương thức tc29_updateSupportRequest_ConflictOtherStaff: dùng để thực thi thao tác tc29_updateSupportRequest_ConflictOtherStaff, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-29: Nhân viên khác cập nhật yêu cầu đã có người phụ trách (BR-25)")
    void tc29_updateSupportRequest_ConflictOtherStaff() {
        long staffA = 2L;
        long staffB = 3L;
        long reqId = 10L;

        UpdateSupportRequest updateReq = new UpdateSupportRequest("DANG_XU_LY", "Tôi muốn nhận xử lý");
        SupportDetailDTO assignedToA = createMockDetail(reqId, "DANG_XU_LY", staffA, "Đang xử lý");

        when(staffDAO.findById(staffB)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(assignedToA));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateSupportRequest(staffB, reqId, updateReq));

        assertEquals(ErrorCode.YEU_CAU_DA_CO_NGUOI_XU_LY, ex.getErrorCode());
        verify(supportDAO, never()).updateWithCondition(anyLong(), any(), any(), anyLong());
    }

    /**
     * Phương thức tc30_closeSupportRequest_EmptyFeedback: dùng để thực thi thao tác tc30_closeSupportRequest_EmptyFeedback, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-30: Đóng yêu cầu (DA_DONG) nhưng bỏ trống phản hồi (BR-26)")
    void tc30_closeSupportRequest_EmptyFeedback() {
        long staffId = 2L;
        long reqId = 10L;
        UpdateSupportRequest emptyReplyReq = new UpdateSupportRequest("DA_DONG", "   ");

        SupportDetailDTO existing = createMockDetail(reqId, "DANG_XU_LY", staffId, null);

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(existing));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateSupportRequest(staffId, reqId, emptyReplyReq));

        assertEquals(ErrorCode.PHAN_HOI_BAT_BUOC, ex.getErrorCode());
        verify(supportDAO, never()).updateWithCondition(anyLong(), any(), any(), anyLong());
    }

    /**
     * Phương thức tc31a_updateSupportRequest_InvalidTransition: dùng để thực thi thao tác tc31a_updateSupportRequest_InvalidTransition, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-31a: Chuyển ngược trạng thái từ DANG_XU_LY về MOI (BR-24)")
    void tc31a_updateSupportRequest_InvalidTransition() {
        long staffId = 2L;
        long reqId = 10L;
        UpdateSupportRequest invalidReq = new UpdateSupportRequest("MOI", "Quay lại mới");

        SupportDetailDTO existing = createMockDetail(reqId, "DANG_XU_LY", staffId, "Ghi chú");

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(existing));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateSupportRequest(staffId, reqId, invalidReq));

        assertEquals(ErrorCode.TRANG_THAI_KHONG_HOP_LE, ex.getErrorCode());
    }

    /**
     * Phương thức tc31b_updateSupportRequest_AlreadyClosed: dùng để thực thi thao tác tc31b_updateSupportRequest_AlreadyClosed, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-31b: Cố sửa yêu cầu đã ở trạng thái DA_DONG (BR-24)")
    void tc31b_updateSupportRequest_AlreadyClosed() {
        long staffId = 2L;
        long reqId = 10L;
        UpdateSupportRequest req = new UpdateSupportRequest("DANG_XU_LY", "Sửa lại");

        SupportDetailDTO closed = createMockDetail(reqId, "DA_DONG", staffId, "Đã hoàn thành");

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(closed));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateSupportRequest(staffId, reqId, req));

        assertEquals(ErrorCode.YEU_CAU_DA_DONG, ex.getErrorCode());
    }

    /**
     * Phương thức tc32_concurrencyUpdateFailed: dùng để thực thi thao tác tc32_concurrencyUpdateFailed, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-32: Concurrency - DB updateWithCondition báo thất bại (BR-25, TC-32)")
    void tc32_concurrencyUpdateFailed() {
        long staffId = 2L;
        long reqId = 10L;
        UpdateSupportRequest updateReq = new UpdateSupportRequest("DANG_XU_LY", "Nhận đơn");

        SupportDetailDTO existing = createMockDetail(reqId, "MOI", null, null);

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(existing));
        when(supportDAO.updateWithCondition(reqId, "DANG_XU_LY", "Nhận đơn", staffId)).thenReturn(false);

        AppException ex = assertThrows(AppException.class,
                () -> service.updateSupportRequest(staffId, reqId, updateReq));

        assertEquals(ErrorCode.YEU_CAU_DA_CO_NGUOI_XU_LY, ex.getErrorCode());
    }

    /**
     * Phương thức tc33_getSupportDetail_CompleteData: dùng để thực thi thao tác tc33_getSupportDetail_CompleteData, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    @Test
    @DisplayName("TC-33: Lấy chi tiết yêu cầu hỗ trợ đủ 3 khối thông tin (BR-27)")
    void tc33_getSupportDetail_CompleteData() {
        long staffId = 2L;
        long reqId = 10L;
        SupportDetailDTO detail = createMockDetail(reqId, "MOI", null, null);

        when(staffDAO.findById(staffId)).thenReturn(Optional.of(sampleStaff));
        when(supportDAO.findById(reqId)).thenReturn(Optional.of(detail));

        SupportDetailDTO res = service.getSupportDetail(staffId, reqId);

        assertNotNull(res);
        assertEquals("YC12345678", res.getMaYeuCau());
        assertNotNull(res.getKhachHang());
        assertEquals("Nguyễn Văn A", res.getKhachHang().getHoTen());
        assertNotNull(res.getDonHang());
        assertEquals("VN12345678", res.getDonHang().getMaVanDon());
        assertFalse(res.getDonHang().getItems().isEmpty());
    }

    /**
     * Phương thức testCreateSupportRequest_RetryOnDuplicateKey: dùng để kiểm thử kịch bản CreateSupportRequest RetryOnDuplicateKey, phục vụ việc đảm bảo tính đúng đắn và độ tin cậy của mã nguồn.
     */
    @Test
    @DisplayName("Thử lại tối đa 5 lần khi trùng mã yêu cầu YC######## (BR-21)")
    void testCreateSupportRequest_RetryOnDuplicateKey() {
        AtomicInteger counter = new AtomicInteger(0);
        SupportRequestService customService = new SupportRequestServiceImpl(
                supportDAO, staffDAO, () -> "YC" + counter.incrementAndGet());

        when(supportDAO.findOrderIdByTrackingCode("VN12345678", 1L)).thenReturn(Optional.of(9001L));
        when(supportDAO.insert(isNull(), any(SupportRequest.class)))
                .thenThrow(new DuplicateKeyException("Duplicate YC1"))
                .thenThrow(new DuplicateKeyException("Duplicate YC2"))
                .thenReturn(105L);

        SupportRequest res = customService.createRequest(1L, new CreateSupportRequest("VN12345678", "Khiếu nại"));

        assertNotNull(res);
        assertEquals("YC3", res.getMaYeuCau());
        verify(supportDAO, times(3)).insert(isNull(), any(SupportRequest.class));
    }

    /**
     * Phương thức createMockDetail: dùng để thực thi thao tác createMockDetail, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceTest.
     */
    private SupportDetailDTO createMockDetail(long reqId, String status, Long staffId, String reply) {
        SupportDetailDTO.CustomerInfo kh = new SupportDetailDTO.CustomerInfo(1L, "Nguyễn Văn A", "0912345678", "kh1@example.com");
        List<SupportDetailDTO.OrderItemInfo> items = List.of(
                new SupportDetailDTO.OrderItemInfo("Điện thoại di động", 2, new BigDecimal("6000"))
        );
        SupportDetailDTO.OrderInfo dh = new SupportDetailDTO.OrderInfo(
                9001L, "VN12345678", "DANG_VAN_CHUYEN", "Trần Văn B", "0987654321",
                "123 Cầu Giấy, Hà Nội", new BigDecimal("1.00"), items
        );
        return new SupportDetailDTO(reqId, "YC12345678", "Nội dung mẫu", status, reply, staffId,
                (staffId != null ? "Nguyễn Thị CSKH" : null), kh, dh);
    }
}
