/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: OrderStatus.java
 * Lần cuối cập nhật: 30/09/2026 08:45
 * 
 * Tổng quát chức năng của lớp:
 * Enum định nghĩa các trạng thái của đơn hàng vận chuyển (DANG_CHO, DA_TIEP_NHAN, DANG_VAN_CHUYEN, GIAO_THANH_CONG, HUY_DON, vv).
 */
package com.shipping.model;

/**
 * Trạng thái đơn hàng (BR-17).
 *
 * <p>Giá trị lưu trong CSDL là tên hằng, ví dụ {@code CHO_LAY}.</p>
 */
public enum OrderStatus {

    /**
     * Phương thức CHO_LAY: dùng để thực thi thao tác CHO_LAY, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     */
    CHO_LAY("Chờ lấy hàng"),
    /**
     * Phương thức TRONG_KHO: dùng để thực thi thao tác TRONG_KHO, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     */
    TRONG_KHO("Đang ở kho"),
    /**
     * Phương thức DANG_VAN_CHUYEN: dùng để thực thi thao tác DANG_VAN_CHUYEN, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     */
    DANG_VAN_CHUYEN("Đang vận chuyển"),
    /**
     * Phương thức DA_GIAO: dùng để thực thi thao tác DA_GIAO, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     */
    DA_GIAO("Đã giao"),
    /**
     * Phương thức DA_HUY: dùng để thực thi thao tác DA_HUY, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     */
    DA_HUY("Đã hủy");

    private final String label;

    /**
     * Phương thức OrderStatus: dùng để khởi tạo đối tượng OrderStatus với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    OrderStatus(String label) {
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
     * Phương thức fromDb: dùng để thực thi thao tác fromDb, phục vụ tiến trình xử lý nghiệp vụ của lớp OrderStatus.
     *
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
