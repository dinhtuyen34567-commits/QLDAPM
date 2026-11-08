package com.shipping.exception;

/**
 * Mã lỗi nghiệp vụ của UC-01.
 *
 * <p>Mỗi mã gắn HTTP status và thông báo hiển thị. Thông báo của các mã
 * phát sinh từ kịch bản phải khớp <b>từng chữ</b> với tài liệu
 * {@code Scenario_TaoDonHang.docx} và tài liệu thiết kế mục 4.2.</p>
 */
public enum ErrorCode {

    /** Ngoại lệ 3.1: SĐT người nhận sai định dạng. */
    SDT_KHONG_HOP_LE(400, "Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số"),

    /** Ngoại lệ 7.1: số lượng không phải số nguyên lớn hơn 0. */
    SO_LUONG_KHONG_HOP_LE(400, "Số lượng phải lớn hơn 0"),

    /** Ngoại lệ 9.1: đơn hàng không có mặt hàng nào. */
    DON_HANG_RONG(422, "Đơn hàng phải có ít nhất 1 mặt hàng"),

    /** Thiếu tên người nhận hoặc địa chỉ giao hàng (BR-03). */
    THIEU_THONG_TIN(400, "Thiếu tên người nhận hoặc địa chỉ giao hàng"),

    /** Vượt độ dài cột trong CSDL (bổ sung kỹ thuật, khớp kích thước cột). */
    DU_LIEU_QUA_DAI(400, "Dữ liệu vượt độ dài cho phép"),

    /** Vượt giới hạn một đơn hàng (BR-14). */
    VUOT_GIOI_HAN(400, "Vượt giới hạn cho phép của một đơn hàng"),

    /** Hàng hóa trong request không tồn tại trong CSDL. */
    HANG_HOA_KHONG_TON_TAI(404, "Hàng hóa không tồn tại"),

    /** Phiên hết hạn hoặc người dùng không phải khách hàng (BR-01). */
    KHONG_CO_QUYEN(401, "Phiên hết hạn hoặc không phải khách hàng"),

    /** Lỗi hệ thống chung; không lộ stack trace ra ngoài (CLAUDE.md mục 11). */
    LOI_HE_THONG(500, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại");

    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /** @return mã HTTP tương ứng để controller ánh xạ ra ngoài. */
    public int getHttpStatus() {
        return httpStatus;
    }

    /** @return thông báo hiển thị cho người dùng, dùng nguyên văn theo tài liệu. */
    public String getMessage() {
        return message;
    }
}
