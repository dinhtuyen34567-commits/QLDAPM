/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ErrorCode.java
 * Lần cuối cập nhật: 28/09/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Enum định nghĩa danh mục mã lỗi nghiệp vụ, mã trạng thái HTTP và thông điệp lỗi chuẩn hóa hiển thị cho người dùng.
 */
package com.shipping.exception;

/**
 * Mã lỗi nghiệp vụ của hệ thống (UC-01, UC-02, UC-03, Auth).
 *
 * <p>Mỗi mã gắn HTTP status và thông báo hiển thị. Thông báo của các mã
 * phát sinh từ kịch bản phải khớp <b>từng chữ</b> với tài liệu
 * {@code Scenario_TaoDonHang.docx} và tài liệu thiết kế mục 4.2.</p>
 */
public enum ErrorCode {

    /**
     * Phương thức SDT_KHONG_HOP_LE: dùng để thực thi thao tác SDT_KHONG_HOP_LE, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Ngoại lệ 3.1: SĐT người nhận sai định dạng.
     */
    SDT_KHONG_HOP_LE(400, "Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số"),

    /**
     * Phương thức SO_LUONG_KHONG_HOP_LE: dùng để thực thi thao tác SO_LUONG_KHONG_HOP_LE, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Ngoại lệ 7.1: số lượng không phải số nguyên lớn hơn 0.
     */
    SO_LUONG_KHONG_HOP_LE(400, "Số lượng phải lớn hơn 0"),

    /**
     * Phương thức DON_HANG_RONG: dùng để thực thi thao tác DON_HANG_RONG, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Ngoại lệ 9.1: đơn hàng không có mặt hàng nào.
     */
    DON_HANG_RONG(422, "Đơn hàng phải có ít nhất 1 mặt hàng"),

    /**
     * Phương thức THIEU_THONG_TIN: dùng để thực thi thao tác THIEU_THONG_TIN, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Thiếu tên người nhận hoặc địa chỉ giao hàng (BR-03).
     */
    THIEU_THONG_TIN(400, "Thiếu tên người nhận hoặc địa chỉ giao hàng"),

    /**
     * Phương thức DU_LIEU_QUA_DAI: dùng để thực thi thao tác DU_LIEU_QUA_DAI, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Vượt độ dài cột trong CSDL (bổ sung kỹ thuật, khớp kích thước cột).
     */
    DU_LIEU_QUA_DAI(400, "Dữ liệu vượt độ dài cho phép"),

    /**
     * Phương thức VUOT_GIOI_HAN: dùng để thực thi thao tác VUOT_GIOI_HAN, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Vượt giới hạn một đơn hàng (BR-14).
     */
    VUOT_GIOI_HAN(400, "Vượt giới hạn cho phép của một đơn hàng"),

    /**
     * Phương thức HANG_HOA_KHONG_TON_TAI: dùng để thực thi thao tác HANG_HOA_KHONG_TON_TAI, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Hàng hóa trong request không tồn tại trong CSDL.
     */
    HANG_HOA_KHONG_TON_TAI(404, "Hàng hóa không tồn tại"),

    /**
     * Phương thức MA_VAN_DON_KHONG_HOP_LE: dùng để thực thi thao tác MA_VAN_DON_KHONG_HOP_LE, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Ngoại lệ 3.1 của UC-02: mã vận đơn sai định dạng.
     */
    MA_VAN_DON_KHONG_HOP_LE(400, "Mã vận đơn không hợp lệ, vui lòng kiểm tra lại"),

    /**
     * Phương thức KHONG_TIM_THAY_VAN_DON: dùng để thực thi thao tác KHONG_TIM_THAY_VAN_DON, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Ngoại lệ 5.1 của UC-02: không tìm thấy đơn theo mã vận đơn.
     */
    KHONG_TIM_THAY_VAN_DON(404, "Không tìm thấy thông tin cho mã vận đơn này"),

    /**
     * Phương thức VAN_DON_KHONG_TON_TAI: dùng để thực thi thao tác VAN_DON_KHONG_TON_TAI, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03 / BR-15 / BR-20: Không tìm thấy vận đơn (hoặc không thuộc quyền sở hữu).
     */
    VAN_DON_KHONG_TON_TAI(404, "Không tìm thấy vận đơn"),

    /**
     * Phương thức NOI_DUNG_RONG: dùng để thực thi thao tác NOI_DUNG_RONG, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Nội dung khiếu nại bị rỗng (BR-20).
     */
    NOI_DUNG_RONG(400, "Vui lòng nhập nội dung yêu cầu"),

    /**
     * Phương thức NOI_DUNG_QUA_DAI: dùng để thực thi thao tác NOI_DUNG_QUA_DAI, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Nội dung khiếu nại vượt quá 2.000 ký tự (BR-20).
     */
    NOI_DUNG_QUA_DAI(400, "Nội dung yêu cầu tối đa 2.000 ký tự"),

    /**
     * Phương thức PHAN_HOI_BAT_BUOC: dùng để thực thi thao tác PHAN_HOI_BAT_BUOC, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Đóng yêu cầu nhưng chưa có phản hồi (BR-26).
     */
    PHAN_HOI_BAT_BUOC(400, "Vui lòng nhập phản hồi trước khi đóng yêu cầu"),

    /**
     * Phương thức YEU_CAU_KHONG_TON_TAI: dùng để thực thi thao tác YEU_CAU_KHONG_TON_TAI, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Không tìm thấy yêu cầu hỗ trợ theo ID.
     */
    YEU_CAU_KHONG_TON_TAI(404, "Không tìm thấy yêu cầu"),

    /**
     * Phương thức TRANG_THAI_KHONG_HOP_LE: dùng để thực thi thao tác TRANG_THAI_KHONG_HOP_LE, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Chuyển trạng thái không hợp lệ (BR-24).
     */
    TRANG_THAI_KHONG_HOP_LE(409, "Không thể chuyển yêu cầu sang trạng thái này"),

    /**
     * Phương thức YEU_CAU_DA_DONG: dùng để thực thi thao tác YEU_CAU_DA_DONG, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Yêu cầu đã đóng không thể sửa đổi (BR-24).
     */
    YEU_CAU_DA_DONG(409, "Yêu cầu đã đóng, không thể cập nhật"),

    /**
     * Phương thức YEU_CAU_DA_CO_NGUOI_XU_LY: dùng để thực thi thao tác YEU_CAU_DA_CO_NGUOI_XU_LY, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * UC-03: Yêu cầu đang do nhân viên khác xử lý (BR-25, concurrency conflict).
     */
    YEU_CAU_DA_CO_NGUOI_XU_LY(409, "Yêu cầu đang được nhân viên khác xử lý"),

    /**
     * Phương thức TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG: dùng để thực thi thao tác TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Auth: Tên đăng nhập hoặc mật khẩu không chính xác.
     */
    TAI_KHOAN_HOAC_MAT_KHAU_KHONG_DUNG(401, "Tên đăng nhập hoặc mật khẩu không chính xác"),

    /**
     * Phương thức TAI_KHOAN_KHONG_CO_VAI_TRO: dùng để thực thi thao tác TAI_KHOAN_KHONG_CO_VAI_TRO, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Auth: Tài khoản chưa được phân quyền.
     */
    TAI_KHOAN_KHONG_CO_VAI_TRO(403, "Tài khoản chưa được phân quyền trong hệ thống"),

    /**
     * Phương thức KHONG_CO_QUYEN: dùng để thực thi thao tác KHONG_CO_QUYEN, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Phiên hết hạn hoặc không đủ quyền truy cập (BR-01, BR-22).
     */
    KHONG_CO_QUYEN(401, "Phiên hết hạn hoặc không có quyền truy cập"),

    /**
     * Phương thức LOI_HE_THONG: dùng để thực thi thao tác LOI_HE_THONG, phục vụ tiến trình xử lý nghiệp vụ của lớp ErrorCode.
     * Lỗi hệ thống chung; không lộ stack trace ra ngoài.
     */
    LOI_HE_THONG(500, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại");

    private final int httpStatus;
    private final String message;

    /**
     * Phương thức ErrorCode: dùng để khởi tạo đối tượng ErrorCode với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * Phương thức getHttpStatus: dùng để lấy giá trị của thuộc tính HttpStatus, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return mã HTTP tương ứng để controller ánh xạ ra ngoài.
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Phương thức getMessage: dùng để lấy giá trị của thuộc tính Message, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return thông báo hiển thị cho người dùng, dùng nguyên văn theo tài liệu.
     */
    public String getMessage() {
        return message;
    }
}
