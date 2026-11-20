/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: PaymentMethod.java
 * Lần cuối cập nhật: 30/09/2026 11:15
 * 
 * Tổng quát chức năng của lớp:
 * Enum định nghĩa các hình thức thanh toán được hỗ trợ (NGUOI_GUI_THANH_TOAN, NGUOI_NHAN_THANH_TOAN).
 */
package com.shipping.model;

/**
 * Phương thức thanh toán của hóa đơn.
 *
 * <p>Màn hình tạo đơn không có ô chọn phương thức thanh toán, nên hóa đơn mới
 * luôn mang giá trị {@link #CHUA_THANH_TOAN} (BR-12). Thanh toán trực tuyến
 * nằm ngoài phạm vi Sprint này.</p>
 */
public enum PaymentMethod {

    /**
     * Phương thức CHUA_THANH_TOAN: dùng để thực thi thao tác CHUA_THANH_TOAN, phục vụ tiến trình xử lý nghiệp vụ của lớp PaymentMethod.
     */
    CHUA_THANH_TOAN("Chưa thanh toán"),
    /**
     * Phương thức TIEN_MAT: dùng để thực thi thao tác TIEN_MAT, phục vụ tiến trình xử lý nghiệp vụ của lớp PaymentMethod.
     */
    TIEN_MAT("Tiền mặt"),
    /**
     * Phương thức CHUYEN_KHOAN: dùng để thực thi thao tác CHUYEN_KHOAN, phục vụ tiến trình xử lý nghiệp vụ của lớp PaymentMethod.
     */
    CHUYEN_KHOAN("Chuyển khoản"),
    /**
     * Phương thức VI_DIEN_TU: dùng để thực thi thao tác VI_DIEN_TU, phục vụ tiến trình xử lý nghiệp vụ của lớp PaymentMethod.
     */
    VI_DIEN_TU("Ví điện tử");

    private final String label;

    /**
     * Phương thức PaymentMethod: dùng để khởi tạo đối tượng PaymentMethod với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    PaymentMethod(String label) {
        this.label = label;
    }

    /**
     * Phương thức getLabel: dùng để lấy giá trị của thuộc tính Label, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return nhãn tiếng Việt để hiển thị trên giao diện.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Phương thức fromDb: dùng để thực thi thao tác fromDb, phục vụ tiến trình xử lý nghiệp vụ của lớp PaymentMethod.
     *
     * Chuyển chuỗi trong CSDL thành enum.
     *
     * @param value giá trị trong cột {@code phuongThucThanhToan}
     * @return enum tương ứng
     * @throws IllegalArgumentException nếu giá trị không hợp lệ
     */
    public static PaymentMethod fromDb(String value) {
        for (PaymentMethod p : values()) {
            if (p.name().equals(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Phuong thuc thanh toan khong hop le: " + value);
    }
}
