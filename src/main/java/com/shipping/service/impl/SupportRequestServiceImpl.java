/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequestServiceImpl.java
 * Lần cuối cập nhật: 11/11/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp hiện thực nghiệp vụ UC-03: kiểm tra quyền sở hữu đơn hàng của khách, xác thực chuyển đổi trạng thái hợp lệ và ghi nhận phản hồi xử lý.
 */
package com.shipping.service.impl;

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
import com.shipping.service.SupportRequestService;
import com.shipping.util.OrderUtils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Triển khai {@link SupportRequestService}.
 *
 * <p>Cài đặt toàn bộ quy tắc nghiệp vụ BR-20 đến BR-27 cho UC-03.</p>
 */
public class SupportRequestServiceImpl implements SupportRequestService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int PAGE_SIZE = 20;

    private final SupportRequestDAO supportRequestDAO;
    private final StaffDAO staffDAO;
    private final Supplier<String> requestCodeGen;

    /**
     * Phương thức SupportRequestServiceImpl: dùng để khởi tạo đối tượng SupportRequestServiceImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportRequestServiceImpl(SupportRequestDAO supportRequestDAO, StaffDAO staffDAO) {
        this(supportRequestDAO, staffDAO, OrderUtils::generateSupportRequestCode);
    }

    /**
     * Phương thức SupportRequestServiceImpl: dùng để khởi tạo đối tượng SupportRequestServiceImpl với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportRequestServiceImpl(SupportRequestDAO supportRequestDAO, StaffDAO staffDAO,
                                     Supplier<String> requestCodeGen) {
        this.supportRequestDAO = supportRequestDAO;
        this.staffDAO = staffDAO;
        this.requestCodeGen = requestCodeGen;
    }

    /**
     * Phương thức createRequest: dùng để thực thi thao tác createRequest, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceImpl.
     */
    @Override
    public SupportRequest createRequest(long customerId, CreateSupportRequest req) {
        if (req == null) {
            throw new AppException(ErrorCode.THIEU_THONG_TIN);
        }

        // BR-20: Nội dung bắt buộc, tối đa 2.000 ký tự
        String content = req.getNoiDung();
        if (content == null || content.trim().isEmpty()) {
            throw new AppException(ErrorCode.NOI_DUNG_RONG);
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new AppException(ErrorCode.NOI_DUNG_QUA_DAI);
        }

        // BR-15 & BR-20: Gắn với đơn hàng của chính khách hàng đó
        String trackingCode = req.getMaVanDon();
        if (trackingCode == null || trackingCode.trim().isEmpty()) {
            throw new AppException(ErrorCode.VAN_DON_KHONG_TON_TAI);
        }

        long orderId = supportRequestDAO.findOrderIdByTrackingCode(trackingCode.trim(), customerId)
                .orElseThrow(() -> new AppException(ErrorCode.VAN_DON_KHONG_TON_TAI));

        // BR-21: Yêu cầu mới có trangThai = MOI, nhanVienId trống, sinh mã YC thử lại tối đa 5 lần
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String requestCode = requestCodeGen.get();
            SupportRequest entity = new SupportRequest();
            entity.setMaYeuCau(requestCode);
            entity.setNoiDung(content.trim());
            entity.setTrangThai(SupportStatus.MOI.name());
            entity.setPhanHoi(null);
            entity.setKhachHangId(customerId);
            entity.setDonHangId(orderId);
            entity.setNhanVienId(null);

            try {
                supportRequestDAO.insert(null, entity);
                return entity;
            } catch (DuplicateKeyException e) {
                if (attempt == MAX_ATTEMPTS) {
                    throw new AppException(ErrorCode.LOI_HE_THONG);
                }
            }
        }

        throw new AppException(ErrorCode.LOI_HE_THONG);
    }

    /**
     * Phương thức getStaffMe: dùng để lấy giá trị của thuộc tính StaffMe, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    @Override
    public StaffDTO getStaffMe(long staffId) {
        return requireStaff(staffId);
    }

    /**
     * Phương thức getSupportList: dùng để lấy giá trị của thuộc tính SupportList, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    @Override
    public SupportPageResult getSupportList(long staffId, String status, int page) {
        requireStaff(staffId);

        int validPage = Math.max(1, page);
        int offset = (validPage - 1) * PAGE_SIZE;

        int total = supportRequestDAO.count(status);
        var items = supportRequestDAO.findByPage(status, offset, PAGE_SIZE);

        return new SupportPageResult(validPage, PAGE_SIZE, total, items);
    }

    /**
     * Phương thức getSupportDetail: dùng để lấy thông tin chi tiết đầy đủ của một yêu cầu khiếu nại kèm thông tin đơn hàng và khách hàng, phục vụ UC-03.
     */
    @Override
    public SupportDetailDTO getSupportDetail(long staffId, long requestId) {
        requireStaff(staffId);

        return supportRequestDAO.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.YEU_CAU_KHONG_TON_TAI));
    }

    /**
     * Phương thức updateSupportRequest: dùng để kiểm tra điều kiện chuyển trạng thái và cập nhật nội dung xử lý/phản hồi yêu cầu khiếu nại của nhân viên CSKH, phục vụ UC-03.
     */
    @Override
    public SupportDetailDTO updateSupportRequest(long staffId, long requestId, UpdateSupportRequest req) {
        requireStaff(staffId);

        if (req == null || req.getTrangThai() == null || req.getTrangThai().trim().isEmpty()) {
            throw new AppException(ErrorCode.TRANG_THAI_KHONG_HOP_LE);
        }

        SupportDetailDTO current = supportRequestDAO.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.YEU_CAU_KHONG_TON_TAI));

        // BR-24: Yêu cầu DA_DONG không thể sửa
        if (SupportStatus.DA_DONG.name().equals(current.getTrangThai())) {
            throw new AppException(ErrorCode.YEU_CAU_DA_DONG);
        }

        String targetStatus = req.getTrangThai().trim();
        String currentStatus = current.getTrangThai();
        String reply = (req.getPhanHoi() != null) ? req.getPhanHoi().trim() : null;

        // BR-24: Luồng trạng thái MOI -> DANG_XU_LY -> DA_DONG (không quay ngược, không nhảy cóc)
        if (SupportStatus.MOI.name().equals(currentStatus)) {
            if (!SupportStatus.DANG_XU_LY.name().equals(targetStatus)) {
                throw new AppException(ErrorCode.TRANG_THAI_KHONG_HOP_LE);
            }
        } else if (SupportStatus.DANG_XU_LY.name().equals(currentStatus)) {
            if (!SupportStatus.DANG_XU_LY.name().equals(targetStatus)
                    && !SupportStatus.DA_DONG.name().equals(targetStatus)) {
                throw new AppException(ErrorCode.TRANG_THAI_KHONG_HOP_LE);
            }
        }

        // BR-26: Đóng yêu cầu (DA_DONG) bắt buộc có phản hồi không rỗng
        if (SupportStatus.DA_DONG.name().equals(targetStatus)) {
            if (reply == null || reply.isEmpty()) {
                throw new AppException(ErrorCode.PHAN_HOI_BAT_BUOC);
            }
        }

        // BR-25: Kiểm tra nhân viên phụ trách
        if (current.getNhanVienId() != null && !Objects.equals(current.getNhanVienId(), staffId)) {
            throw new AppException(ErrorCode.YEU_CAU_DA_CO_NGUOI_XU_LY);
        }

        // Thực hiện cập nhật với điều kiện tương tranh (Concurrency UPDATE)
        boolean updated = supportRequestDAO.updateWithCondition(requestId, targetStatus, reply, staffId);
        if (!updated) {
            throw new AppException(ErrorCode.YEU_CAU_DA_CO_NGUOI_XU_LY);
        }

        return supportRequestDAO.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.YEU_CAU_KHONG_TON_TAI));
    }

    /**
     * Phương thức requireStaff: dùng để thực thi thao tác requireStaff, phục vụ tiến trình xử lý nghiệp vụ của lớp SupportRequestServiceImpl.
     */
    private StaffDTO requireStaff(long staffId) {
        return staffDAO.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.KHONG_CO_QUYEN));
    }
}
