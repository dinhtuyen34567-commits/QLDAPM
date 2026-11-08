package com.shipping.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Các hàm tiện ích dùng chung cho UC-01: kiểm tra SĐT, sinh mã vận đơn / mã
 * hóa đơn và làm tròn tiền.
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

    private static final int RANDOM_DIGITS = 8;

    /** Nguồn ngẫu nhiên mã hoá, an toàn cho mã định danh cấp cho khách hàng. */
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderUtils() {
        // Lop tien ich, khong tao the
    }

    /**
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
     * Sinh mã vận đơn ngẫu nhiên theo BR-08.
     *
     * @return chuỗi {@code "VN"} + 8 chữ số
     */
    public static String generateTrackingCode() {
        return generateCode(TRACKING_PREFIX);
    }

    /**
     * Sinh mã hóa đơn ngẫu nhiên (mục 5.6, tài liệu chưa quy định nên dùng quy ước).
     *
     * @return chuỗi {@code "HD"} + 8 chữ số
     */
    public static String generateInvoiceCode() {
        return generateCode(INVOICE_PREFIX);
    }

    /**
     * Sinh chuỗi mã gồm tiền tố và {@value #RANDOM_DIGITS} chữ số ngẫu nhiên.
     *
     * @param prefix tiền tố, ví dụ {@code "VN"}
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
     * Làm tròn về đồng ({@link RoundingMode#HALF_UP}).
     *
     * <p>Mọi phép tính tiền trong UC-01 đều đi qua hàm này, nhờ vậy không bao
     * giờ dùng {@code double}/{@code float} cho tiền (CLAUDE.md mục 5.4).</p>
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
