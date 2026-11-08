package com.shipping.model;

/**
 * Trạng thái đơn hàng (BR-17).
 *
 * <p>Giá trị lưu trong CSDL là tên hằng, ví dụ {@code CHO_LAY}.</p>
 */
public enum OrderStatus {

    CHO_LAY("Chờ lấy hàng"),
    TRONG_KHO("Đang ở kho"),
    DANG_VAN_CHUYEN("Đang vận chuyển"),
    DA_GIAO("Đã giao"),
    DA_HUY("Đã hủy");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    /** @return nhãn tiếng Việt để hiển thị trên giao diện. */
    public String getLabel() {
        return label;
    }

    /**
     * Chuyển chuỗi trong CSDL thành enum.
     *
     * @param value giá trị trong cột {@code trangThai}
     * @return enum tương ứng
     * @throws IllegalArgumentException nếu giá trị không hợp lệ
     */
    public static OrderStatus fromDb(String value) {
        for (OrderStatus s : values()) {
            if (s.name().equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Trang thai don hang khong hop le: " + value);
    }
}
