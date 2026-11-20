/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: OrderUtils.java
 * Lần cuối cập nhật: 29/09/2026 10:30
 * 
 * Tổng quát chức năng của lớp:
 * Lớp tiện ích cung cấp các hàm hỗ trợ xác thực số điện thoại, sinh mã vận đơn, mã hóa đơn, mã khiếu nại và làm tròn tiền tệ VND.
 */
package com.shipping.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Các hàm tiện ích dùng chung cho hệ thống: kiểm tra SĐT, sinh mã vận đơn,
 * mã hóa đơn, mã yêu cầu hỗ trợ và làm tròn tiền.
 *
 * <p>Mọi hàm ở đây là hàm tĩnh, không phụ thuộc CSDL nên kiểm thử được độc lập.</p>
 */
public final class OrderUtils {

    /** BR-02: SĐT người nhận chỉ gồm 10-11 chữ số (sau khi trim). */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");

    /** BR-08: mã vận đơn = "VN" + 8 chữ số. */
    private static final String TRACKING_PREFIX = "VN";

    /** Mục 5.6: mã hóa đơn = "HD" + 8 chữ số. */
    private static final String INVOICE_PREFIX = "HD";

    /** BR-21: mã yêu cầu hỗ trợ = "YC" + 8 chữ số. */
    private static final String SUPPORT_PREFIX = "YC";

    private static final int RANDOM_DIGITS = 8;

    /** Nguồn ngẫu nhiên mã hoá, an toàn cho mã định danh cấp cho khách hàng. */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Phương thức OrderUtils: dùng để khởi tạo đối tượng OrderUtils với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    private OrderUtils() {
        // Lop tien ich, khong tao the
    }

    /**
     * Phương thức isValidPhone: dùng để kiểm tra định dạng số điện thoại (từ 10 đến 11 chữ số hợp lệ), phục vụ xác thực số điện thoại người nhận trong UC-01.
     *
     * Kiểm tra SĐT người nhận theo BR-02.
     *
     * @param phone số điện thoại cần kiểm tra (có thể null)
     * @return {@code true} nếu sau khi trim chỉ gồm 10-11 chữ số
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Phương thức generateTrackingCode: dùng để sinh mã vận đơn ngẫu nhiên theo định dạng chuẩn (VN + 8 chữ số), phục vụ định danh đơn hàng trong UC-01.
     *
     * Sinh mã vận đơn ngẫu nhiên theo BR-08.
     *
     * @return chuỗi {@code "VN"} + 8 chữ số
     */
    public static String generateTrackingCode() {
        return generateCode(TRACKING_PREFIX);
    }

    /**
     * Phương thức generateInvoiceCode: dùng để sinh mã hóa đơn ngẫu nhiên theo định dạng chuẩn (HD + 8 chữ số), phục vụ hạch toán tài chính trong UC-01.
     *
     * Sinh mã hóa đơn ngẫu nhiên.
     *
     * @return chuỗi {@code "HD"} + 8 chữ số
     */
    public static String generateInvoiceCode() {
        return generateCode(INVOICE_PREFIX);
    }

    /**
     * Phương thức generateSupportRequestCode: dùng để sinh mã yêu cầu hỗ trợ ngẫu nhiên theo định dạng chuẩn (YC + 8 chữ số), phục vụ định danh khiếu nại trong UC-03.
     *
     * Sinh mã yêu cầu hỗ trợ ngẫu nhiên theo BR-21.
     *
     * @return chuỗi {@code "YC"} + 8 chữ số
     */
    public static String generateSupportRequestCode() {
        return generateCode(SUPPORT_PREFIX);
    }

    /**
     * Phương thức generateCode: dùng để sinh chuỗi mã ngẫu nhiên với tiền tố và độ dài quy định, phục vụ việc tạo mã định danh duy nhất.
     *
     * Sinh chuỗi mã gồm tiền tố và {@value #RANDOM_DIGITS} chữ số ngẫu nhiên.
     *
     * @param prefix tiền tố, ví dụ {@code "VN"} hoặc {@code "YC"}
     * @return mã đầy đủ
     */
    private static String generateCode(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < RANDOM_DIGITS; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Phương thức roundToVnd: dùng để làm tròn giá trị tiền tệ BigDecimal đến hàng đơn vị VND, phục vụ tính toán cước phí chính xác.
     *
     * Làm tròn về đồng ({@link RoundingMode#HALF_UP}).
     *
     * <p>Mọi phép tính tiền trong UC-01 đều đi qua hàm này, nhờ vậy không bao
     * giờ dùng {@code double}/{@code float} cho tiền.</p>
     *
     * @param value giá trị cần làm tròn, có thể null
     * @return giá trị đã làm tròn về đơn vị đồng, hoặc {@link BigDecimal#ZERO} nếu null
     */
    public static BigDecimal roundToVnd(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP);
        }
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Phương thức roundToVndLong: dùng để làm tròn giá trị tiền tệ sang kiểu số nguyên Long, phục vụ lưu trữ cước phí dạng số nguyên trong CSDL.
     *
     * Làm tròn về đồng rồi chuyển thành số nguyên, tiện khi ghi vào cột
     * {@code DECIMAL(15,2)} của CSDL.
     *
     * @param value giá trị cần làm tròn
     * @return {@link Long} tương ứng
     */
    public static Long roundToVndLong(BigDecimal value) {
        return roundToVnd(value).longValueExact();
    }
}
