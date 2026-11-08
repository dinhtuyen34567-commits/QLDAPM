package com.shipping.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiem thu {@link OrderUtils}: kiem tra SĐT, sinh mã và làm tròn tiền.
 */
class OrderUtilsTest {

    @Test
    @DisplayName("TC-U-01: SĐT 10 hoac 11 chu so hop le")
    void tcU01_sdtHopLe() {
        assertTrue(OrderUtils.isValidPhone("0987654321"));
        assertTrue(OrderUtils.isValidPhone("09876543210"));
    }

    @Test
    @DisplayName("TC-U-02: SĐT sai dinh dang bi tu choi")
    void tcU02_sdtSaiDinhDang() {
        assertFalse(OrderUtils.isValidPhone(null));
        assertFalse(OrderUtils.isValidPhone(""));
        assertFalse(OrderUtils.isValidPhone("0987abc@"));
        assertFalse(OrderUtils.isValidPhone("123456789"));      // 9 chu so
        assertFalse(OrderUtils.isValidPhone("123456789012"));   // 12 chu so
        assertFalse(OrderUtils.isValidPhone("0987 654 321"));
        assertFalse(OrderUtils.isValidPhone("+84987654321"));
    }

    @Test
    @DisplayName("TC-U-03: SĐT co khoang trang duoc trim truoc khi kiem tra")
    void tcU03_sdtCoKhoangTrang() {
        assertTrue(OrderUtils.isValidPhone("  0987654321  "));
        assertTrue(OrderUtils.isValidPhone("\t0987654321\n"));
    }

    @Test
    @DisplayName("TC-U-04: ma van don co dinh dang VN + 8 chu so")
    void tcU04_maVanDon() {
        String code = OrderUtils.generateTrackingCode();

        assertTrue(code.startsWith("VN"));
        assertEquals(10, code.length());
        assertTrue(code.substring(2).matches("[0-9]{8}"), "Phan chu so phai la 8 chu so");
    }

    @Test
    @DisplayName("TC-U-05: ma hoa don co dinh dang HD + 8 chu so")
    void tcU05_maHoaDon() {
        String code = OrderUtils.generateInvoiceCode();

        assertTrue(code.startsWith("HD"));
        assertEquals(10, code.length());
        assertTrue(code.substring(2).matches("[0-9]{8}"), "Phan chu so phai la 8 chu so");
    }

    @Test
    @DisplayName("TC-U-06: sinh nhieu ma van don duoc khac nhau")
    void tcU06_maVanDonKhacNhau() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            codes.add(OrderUtils.generateTrackingCode());
        }
        // 500 ma ngau nhien 8 chu so: xac suat trung o 500 lan rat thap
        assertTrue(codes.size() > 490, "So ma van don trung khong duoc nhieu");
    }

    @Test
    @DisplayName("TC-U-07: lam tron ve dong dung HALF_UP")
    void tcU07_lamTronVeDong() {
        assertEquals(0, new BigDecimal("6000")
                .compareTo(OrderUtils.roundToVnd(new BigDecimal("6000.00"))));
        // 4.320,75 -> 4.321 (HALF_UP)
        assertEquals(0, new BigDecimal("4321")
                .compareTo(OrderUtils.roundToVnd(new BigDecimal("4320.75"))));
        // 4.320,50 -> 4.321 (HALF_UP, khong phai HALF_EVEN)
        assertEquals(0, new BigDecimal("4321")
                .compareTo(OrderUtils.roundToVnd(new BigDecimal("4320.50"))));
        // 4.320,49 -> 4.320
        assertEquals(0, new BigDecimal("4320")
                .compareTo(OrderUtils.roundToVnd(new BigDecimal("4320.49"))));
    }

    @Test
    @DisplayName("TC-U-08: lam tron null tra ve 0")
    void tcU08_lamTronNull() {
        assertEquals(0, BigDecimal.ZERO.compareTo(OrderUtils.roundToVnd(null)));
        assertEquals(0L, OrderUtils.roundToVndLong(null));
    }

    @Test
    @DisplayName("TC-U-09: roundToVndLong tra ve so nguyen")
    void tcU09_roundToVndLong() {
        assertEquals(6000L, OrderUtils.roundToVndLong(new BigDecimal("6000.00")));
        assertEquals(4321L, OrderUtils.roundToVndLong(new BigDecimal("4320.75")));
    }
}
