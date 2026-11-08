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

    public ErrorResponse() {
        // Dung cho Gson
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
