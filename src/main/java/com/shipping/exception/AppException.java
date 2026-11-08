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

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /** @return mã lỗi nghiệp vụ kèm HTTP status và thông báo chuẩn. */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
