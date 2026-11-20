/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: AppException.java
 * Lần cuối cập nhật: 28/09/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Ngoại lệ nghiệp vụ runtime (AppException) mang theo ErrorCode, phục vụ việc ngắt luồng xử lý và chuyển giao thông tin lỗi về tầng Controller.
 */
package com.shipping.exception;

/**
 * Ngoại lệ nghiệp vụ mang theo {@link ErrorCode}.
 *
 * <p>Service ném lỗi này khi vi phạm quy tắc nghiệp vụ; controller bắt lỗi này
 * và ánh xạ thành JSON {@code {"code": "...", "message": "..."}} kèm HTTP status
 * tương ứng. Nhờ vậy controller không chứa logic kiểm tra nghiệp vụ.</p>
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    /**
     * Phương thức AppException: dùng để khởi tạo đối tượng AppException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Phương thức AppException: dùng để khởi tạo đối tượng AppException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Phương thức AppException: dùng để khởi tạo đối tượng AppException với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Phương thức getErrorCode: dùng để lấy giá trị của thuộc tính ErrorCode, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return mã lỗi nghiệp vụ kèm HTTP status và thông báo chuẩn.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
