/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ErrorResponse.java
 * Lần cuối cập nhật: 04/10/2026 15:00
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO chuẩn hóa cấu trúc phản hồi lỗi dạng JSON (gồm mã lỗi và thông báo lỗi) gửi về cho client.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * Cấu trúc lỗi trả về cho client: {@code {"code": "...", "message": "..."}}.
 *
 * <p>Controller tạo đối tượng này khi bắt được {@link com.shipping.exception.AppException}.
 * Không bao giờ lộ stack trace ra ngoài (CLAUDE.md mục 11).</p>
 */
public class ErrorResponse {

    /** Mã lỗi nghiệp vụ, ví dụ {@code SDT_KHONG_HOP_LE}. */
    @SerializedName("code")
    private String code;

    /** Thông báo hiển thị cho người dùng, dùng nguyên văn theo tài liệu. */
    @SerializedName("message")
    private String message;

    /**
     * Phương thức ErrorResponse: dùng để khởi tạo đối tượng ErrorResponse với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public ErrorResponse() {
        // Dung cho Gson
    }

    /**
     * Phương thức ErrorResponse: dùng để khởi tạo đối tượng ErrorResponse với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Phương thức getCode: dùng để lấy giá trị của thuộc tính Code, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getCode() {
        return code;
    }

    /**
     * Phương thức setCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính Code, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Phương thức getMessage: dùng để lấy giá trị của thuộc tính Message, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Phương thức setMessage: dùng để gán hoặc cập nhật giá trị cho thuộc tính Message, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
