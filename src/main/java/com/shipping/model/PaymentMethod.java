package com.shipping.model;

/**
 * Phương thức thanh toán của hóa đơn.
 *
 * <p>Màn hình tạo đơn không có ô chọn phương thức thanh toán, nên hóa đơn mới
 * luôn mang giá trị {@link #CHUA_THANH_TOAN} (BR-12). Thanh toán trực tuyến
 * nằm ngoài phạm vi Sprint này.</p>
 */
public enum PaymentMethod {

    CHUA_THANH_TOAN("Chưa thanh toán"),
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản"),
    VI_DIEN_TU("Ví điện tử");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    /** @return nhãn tiếng Việt để hiển thị trên giao diện. */
    public String getLabel() {
        return label;
    }

    /**
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
