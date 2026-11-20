/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: DuplicateKeyException.java
 * Lần cuối cập nhật: 05/10/2026 08:30
 * 
 * Tổng quát chức năng của lớp:
 * Ngoại lệ báo hiệu xung đột trùng khóa UNIQUE (mã vận đơn, mã hóa đơn) trong quá trình ghi dữ liệu vào CSDL.
 */
package com.shipping.dao;

/**
 * Lỗi trùng khóa UNIQUE khi ghi vào CSDL.
 *
 * <p>DAO dịch lỗi SQL (SQLState {@code 23000}, mã lỗi MySQL {@code 1062}) sang
 * exception này để service có thể thử lại với mã mới theo BR-08, thay vì phải
 * phân biệt lỗi hệ thống với lỗi trùng khóa.</p>
 *
 * <p>Exception mang theo tên ràng buộc bị vi phạm (nếu CSDL báo được) để service
 * phân biệt chắc chắn hai trường hợp: trùng mã vận đơn (sinh mã mới) và trùng
 * khóa idempotency (trả lại đơn cũ, BR-13). Nhờ vậy không phải dò chuỗi thông
 * báo lỗi, vốn phụ thuộc cách MySQL diễn đạt.</p>
 */
public class DuplicateKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Tên ràng buộc bị vi phạm, có thể {@code null} nếu không xác định được. */
    private final String constraintName;

    /**
     * Phương thức DuplicateKeyException: dùng để khởi tạo đối tượng DuplicateKeyException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public DuplicateKeyException(String message) {
        this(message, null, null);
    }

    /**
     * Phương thức DuplicateKeyException: dùng để khởi tạo đối tượng DuplicateKeyException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public DuplicateKeyException(String message, Throwable cause) {
        this(message, null, cause);
    }

    /**
     * Phương thức DuplicateKeyException: dùng để khởi tạo đối tượng DuplicateKeyException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public DuplicateKeyException(String message, String constraintName, Throwable cause) {
        super(message, cause);
        this.constraintName = constraintName;
    }

    /**
     * Phương thức getConstraintName: dùng để lấy giá trị của thuộc tính ConstraintName, phục vụ việc truy xuất dữ liệu của đối tượng.
     *
     * Lấy tên ràng buộc UNIQUE bị vi phạm.
     *
     * @return tên ràng buộc, hoặc {@code null} nếu không xác định được
     */
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * Phương thức isConstraint: dùng để lấy giá trị của thuộc tính Constraint, phục vụ việc truy xuất dữ liệu của đối tượng.
     *
     * Kiểm tra lỗi này có phải do vi phạm ràng buộc có tên chứa chuỗi cho trước
     * hay không.
     *
     * <p>So khớp không phân biệt hoa thường và bỏ qua dấu gạch dưới, vì MySQL có
     * thể báo tên ràng buộc ở dạng {@code uq_tblDonHang_idempotencyKey} hoặc
     * {@code tblDonHang.khachHangId}.</p>
     *
     * @param keyword tên cột hoặc tên ràng buộc cần tìm
     * @return {@code true} nếu khớp
     */
    public boolean isConstraint(String keyword) {
        if (constraintName == null || keyword == null) {
            return false;
        }
        String normalized = constraintName.toLowerCase().replace("_", "");
        return normalized.contains(keyword.toLowerCase().replace("_", ""));
    }
}
