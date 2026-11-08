package com.shipping.service;

import com.google.gson.Gson;
import com.shipping.config.ConnectionProvider;
import com.shipping.dao.CustomerDAO;
import com.shipping.dao.DuplicateKeyException;
import com.shipping.dao.OrderDAO;
import com.shipping.dao.ProductDAO;
import com.shipping.exception.AppException;
import com.shipping.exception.ErrorCode;
import com.shipping.model.CreateOrderRequest;
import com.shipping.model.CreateResult;
import com.shipping.model.Customer;
import com.shipping.model.Invoice;
import com.shipping.model.ItemRequest;
import com.shipping.model.Order;
import com.shipping.model.OrderDetail;
import com.shipping.model.Product;
import com.shipping.model.Quote;
import com.shipping.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Kiem thu tang Service cho UC-01.
 *
 * <p>Ten test theo quy uoc {@code tcNN_moTaNgan} (CLAUDE.md muc 10).</p>
 */
class OrderServiceTest {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private ConnectionProvider connectionProvider;
    private Connection connection;

    /** Hang hoa H001: 0,50 kg x 12.000 d/kg -> don gia 6.000 d. */
    private Product phone;

    /** Hang hoa H006: 0,35 kg x 12.345 d/kg -> don gia 4.321 d (lam tron). */
    private Product cosmetic;

    @BeforeEach
    void setUp() throws SQLException {
        orderDAO = mock(OrderDAO.class);
        productDAO = mock(ProductDAO.class);
        customerDAO = mock(CustomerDAO.class);
        connectionProvider = mock(ConnectionProvider.class);
        connection = mock(Connection.class);

        when(connectionProvider.getConnection()).thenReturn(connection);
        when(customerDAO.findById(anyLong()))
                .thenReturn(Optional.of(OrderServiceTestSupport.customer(1)));

        phone = OrderServiceTestSupport.product(1, "0.50", "12000");
        cosmetic = OrderServiceTestSupport.product(6, "0.35", "12345");

        when(productDAO.findByIds(any())).thenReturn(new HashMap<>());
    }

    // -----------------------------------------------------------------
    // TC-01: luu chinh
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-01: luu chinh - ghi 3 bang, trangThai CHO_LAY, tripId null, tra ma van don")
    void tc01_luuChinhGhiBaBangVaTraMaVanDon() throws SQLException {
        Map<Integer, Product> products = OrderServiceTestSupport.productMap(phone);
        when(productDAO.findByIds(any())).thenReturn(products);
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);
        when(orderDAO.findByIdempotencyKey(anyLong(), anyString())).thenReturn(Optional.empty());

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        CreateOrderRequest req = new CreateOrderRequest(
                "Trần Văn B", "0987654321", "123 Cầu Giấy, Hà Nội",
                List.of(new ItemRequest(1, 2)));

        CreateResult result = service.createOrder(1, req, null);

        assertEquals(100L, result.getOrderId());
        assertEquals("VN00000001", result.getTrackingCode());
        assertEquals(0, new BigDecimal("13200").compareTo(result.getTotalAmount()));

        // Don hang ghi voi trangThai CHO_LAY va chuyenXeId null (BR-11)
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).insertOrder(eq(connection), orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals("CHO_LAY", saved.getStatus());
        assertEquals(null, saved.getTripId());
        assertEquals("Trần Văn B", saved.getReceiverName());
        assertEquals("0987654321", saved.getReceiverPhone());
        assertEquals(1, saved.getCustomerId());
        assertEquals(0, new BigDecimal("1.00").compareTo(saved.getTotalWeight()));

        // Chi tiet va hoa don cung duoc ghi
        verify(orderDAO).insertDetails(eq(connection), eq(100L), any());
        verify(orderDAO).insertInvoice(eq(connection), any(Invoice.class));

        // Giao dich: tat autoCommit, commit roi moi close
        InOrder order = inOrder(connection);
        order.verify(connection).setAutoCommit(false);
        order.verify(connection).commit();
        order.verify(connection).close();
    }

    @Test
    @DisplayName("TC-01b: hoa don luon CHUA_THANH_TOAN va tong tien da gom VAT")
    void tc01b_hoaDonChuaThanhToan() throws SQLException {
        Map<Integer, Product> products = OrderServiceTestSupport.productMap(phone);
        when(productDAO.findByIds(any())).thenReturn(products);
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        service.createOrder(1, new CreateOrderRequest("Trần Văn B", "0987654321",
                "123 Cầu Giấy, Hà Nội", List.of(new ItemRequest(1, 2))), null);

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(orderDAO).insertInvoice(eq(connection), captor.capture());
        Invoice invoice = captor.getValue();

        assertEquals("CHUA_THANH_TOAN", invoice.getPaymentMethod());
        assertEquals(0, new BigDecimal("13200").compareTo(invoice.getTotalAmount()));
        assertEquals(0, new BigDecimal("10").compareTo(invoice.getVatPercent()));
        assertEquals(100L, invoice.getOrderId());
    }

    // -----------------------------------------------------------------
    // TC-02: so dien thoai sai
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-02: SDT sai dinh dang - nem SDT_KHONG_HOP_LE va khong ghi CSDL")
    void tc02_sdtSaiDinhDang() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        CreateOrderRequest req = new CreateOrderRequest(
                "Trần Văn B", "0987abc@", "123 Cầu Giấy, Hà Nội",
                List.of(new ItemRequest(1, 2)));

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, req, null));

        assertEquals(ErrorCode.SDT_KHONG_HOP_LE, ex.getErrorCode());
        assertEquals("Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số",
                ex.getErrorCode().getMessage());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-02b: SDT 10 va 11 chu so deu hop le")
    void tc02b_sdtHopLe() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        service.createOrder(1, new CreateOrderRequest("A", "1234567890", "B",
                List.of(new ItemRequest(1, 1))), null);
        service.createOrder(1, new CreateOrderRequest("A", "12345678901", "B",
                List.of(new ItemRequest(1, 1))), null);

        verify(orderDAO, times(2)).insertOrder(any(Connection.class), any(Order.class));
    }

    // -----------------------------------------------------------------
    // TC-03: so luong khong hop le
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-03: so luong 0 hoac am - nem SO_LUONG_KHONG_HOP_LE va khong ghi CSDL")
    void tc03_soLuongKhongHopLe() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex0 = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 0))), null));
        AppException exAm = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, -5))), null));

        assertEquals(ErrorCode.SO_LUONG_KHONG_HOP_LE, ex0.getErrorCode());
        assertEquals(ErrorCode.SO_LUONG_KHONG_HOP_LE, exAm.getErrorCode());
        assertEquals("Số lượng phải lớn hơn 0", exAm.getErrorCode().getMessage());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    // -----------------------------------------------------------------
    // TC-04: don hang rong
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-04: items rong - nem DON_HANG_RONG va khong ghi CSDL")
    void tc04_donHangRong() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of()), null));

        assertEquals(ErrorCode.DON_HANG_RONG, ex.getErrorCode());
        assertEquals("Đơn hàng phải có ít nhất 1 mặt hàng",
                ex.getErrorCode().getMessage());
        assertEquals(422, ex.getErrorCode().getHttpStatus());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-04b: quote voi danh sach rong cung nem DON_HANG_RONG")
    void tc04b_quoteDanhSachRong() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.quote(List.of()));

        assertEquals(ErrorCode.DON_HANG_RONG, ex.getErrorCode());
    }

    // -----------------------------------------------------------------
    // TC-07: rollback khi ghi hoa don that bai
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-07: loi khi ghi hoa don - rollback duoc goi, commit khong duoc goi")
    void tc07_loiGhiHoaDonThiRollback() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);
        when(orderDAO.insertInvoice(any(Connection.class), any(Invoice.class)))
                .thenThrow(new IllegalStateException("Loi ghi hoa don"));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        assertThrows(RuntimeException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 2))), null));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }

    // -----------------------------------------------------------------
    // TC-08: client gui khachHangId gia
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-08: server bo qua gia tri nguoi gui do client gui, lay tu phien")
    void tc08_serverBoQuaKhachHangIdTuClient() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        // Client gui them khoa khachHangId gia (kieu khong an toan) trong body.
        // DTO khong khai bao truong nay nen gia tri bi bo qua khi phan tich JSON,
        // va server luon lay customerId tu phien.
        String json = "{\"tenNguoiNhan\":\"A\",\"sdtNguoiNhan\":\"0987654321\","
                + "\"diaChiGiao\":\"B\",\"khachHangId\":999,"
                + "\"items\":[{\"hangHoaId\":1,\"soLuong\":1}]}";

        CreateOrderRequest req = new Gson().fromJson(json, CreateOrderRequest.class);

        service.createOrder(1, req, null);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).insertOrder(eq(connection), captor.capture());
        // customerId lay tu phien (1), khong phai 999 tu body
        assertEquals(1, captor.getValue().getCustomerId());
    }

    @Test
    @DisplayName("TC-08b: nguoi dung khong phai khach hang - nem KHONG_CO_QUYEN")
    void tc08b_khongPhaiKhachHang() {
        when(customerDAO.findById(anyLong())).thenReturn(Optional.empty());

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(999, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), null));

        assertEquals(ErrorCode.KHONG_CO_QUYEN, ex.getErrorCode());
    }

    // -----------------------------------------------------------------
    // TC-10: thieu thong tin
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-10: thieu ten nguoi nhan - nem THIEU_THONG_TIN")
    void tc10_thieuTenNguoiNhan() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest(null, "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), null));

        assertEquals(ErrorCode.THIEU_THONG_TIN, ex.getErrorCode());
        assertEquals("Thiếu tên người nhận hoặc địa chỉ giao hàng",
                ex.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("TC-10b: thieu dia chi giao hang - nem THIEU_THONG_TIN")
    void tc10b_thieuDiaChiGiaoHang() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "  ",
                        List.of(new ItemRequest(1, 1))), null));

        assertEquals(ErrorCode.THIEU_THONG_TIN, ex.getErrorCode());
    }

    @Test
    @DisplayName("TC-10c: ten qua dai - nem DU_LIEU_QUA_DAI")
    void tc10c_tenQuaDai() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        String tenQuaDai = "A".repeat(101);
        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest(tenQuaDai, "0987654321",
                        "B", List.of(new ItemRequest(1, 1))), null));

        assertEquals(ErrorCode.DU_LIEU_QUA_DAI, ex.getErrorCode());
    }

    // -----------------------------------------------------------------
    // TC-11: gop mat hang trung
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-11: cung hang hoa hai lan - gop mot dong va cong don so luong")
    void tc11_gopMatHangTrung() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                List.of(new ItemRequest(1, 2), new ItemRequest(1, 3))), null);

        ArgumentCaptor<List<OrderDetail>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderDAO).insertDetails(eq(connection), eq(100L), captor.capture());

        List<OrderDetail> details = captor.getValue();
        assertEquals(1, details.size());
        assertEquals(1, details.get(0).getProductId());
        assertEquals(5, details.get(0).getQuantity());
    }

    // -----------------------------------------------------------------
    // TC-12: tong khoi luong
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-12: 0,5 kg x 2 - tongKhoiLuong = 1,00 kg do server tinh")
    void tc12_tongKhoiLuong() {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        Quote quote = service.quote(List.of(new ItemRequest(1, 2)));

        assertEquals(0, new BigDecimal("1.00").compareTo(quote.getTotalWeight()));
        assertEquals(0, new BigDecimal("12000").compareTo(quote.getTotalFee()));
        assertEquals(0, new BigDecimal("1200").compareTo(quote.getVatAmount()));
        assertEquals(0, new BigDecimal("13200").compareTo(quote.getTotalAmount()));
    }

    @Test
    @DisplayName("TC-12b: server khong tin tong khoi luong do client gui")
    void tc12b_serverKhongTinTongKhoiLuongTuClient() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                List.of(new ItemRequest(1, 2))), null);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDAO).insertOrder(eq(connection), captor.capture());
        assertEquals(0, new BigDecimal("1.00").compareTo(captor.getValue().getTotalWeight()));
    }

    // -----------------------------------------------------------------
    // TC-13: ma van don trung
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-13: ma van don trung 2 lan roi thanh cong - sinh lai ma va ghi duy nhat")
    void tc13_maVanDonTrungSinhLai() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class)))
                .thenThrow(new DuplicateKeyException(
                        "Duplicate entry 'VN00000001' for key 'tblDonHang.maVanDon'",
                        "tblDonHang.maVanDon", null))
                .thenThrow(new DuplicateKeyException(
                        "Duplicate entry 'VN00000001' for key 'tblDonHang.maVanDon'",
                        "tblDonHang.maVanDon", null))
                .thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider,
                () -> "VN00000001", () -> "HD00000001");

        CreateResult result = service.createOrder(1,
                new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), null);

        assertEquals(100L, result.getOrderId());
        verify(orderDAO, times(3)).insertOrder(any(Connection.class), any(Order.class));
        verify(connection).commit();
    }

    @Test
    @DisplayName("TC-13b: ma van don trung 6 lan lien tiep - nem LOI_HE_THONG")
    void tc13b_maVanDonTrungQuaNamLan() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class)))
                .thenThrow(new DuplicateKeyException("Duplicate entry for key 'maVanDon'"));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider,
                () -> "VN00000001", () -> "HD00000001");

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), null));

        assertEquals(ErrorCode.LOI_HE_THONG, ex.getErrorCode());
        assertEquals(500, ex.getErrorCode().getHttpStatus());
        verify(orderDAO, times(5)).insertOrder(any(Connection.class), any(Order.class));
        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    // -----------------------------------------------------------------
    // TC-14: idempotency
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-14: gui lai cung Idempotency-Key - tra don cu, khong ghi them")
    void tc14_guiLaiCungIdempotencyKey() throws SQLException {
        Order donCu = new Order();
        donCu.setId(55L);
        donCu.setTrackingCode("VN99887766");

        when(orderDAO.findByIdempotencyKey(1L, "key-abc")).thenReturn(Optional.of(donCu));
        when(orderDAO.findInvoiceTotalByOrderId(55L))
                .thenReturn(Optional.of(new BigDecimal("13200")));
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        CreateResult result = service.createOrder(1,
                new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), "key-abc");

        assertEquals(55L, result.getOrderId());
        assertEquals("VN99887766", result.getTrackingCode());
        assertEquals(0, new BigDecimal("13200").compareTo(result.getTotalAmount()));
        // Khong ghi them vao CSDL
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-14c: hai request cung khoa chay song song - request thua tra don da co")
    void tc14c_haiRequestCungKhoaChaySongSong() throws SQLException {
        Order donCu = new Order();
        donCu.setId(77L);
        donCu.setTrackingCode("VN55667788");

        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));
        // Request thua vi pham UNIQUE (khachHangId, idempotencyKey)
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class)))
                .thenThrow(new DuplicateKeyException(
                        "Duplicate entry '1-key-abc' for key 'tblDonHang.khachHangId'",
                        "tblDonHang.khachHangId", null));
        // Sau khi that bai, service tra lai don da co
        when(orderDAO.findByIdempotencyKey(1L, "key-abc")).thenReturn(Optional.of(donCu));
        when(orderDAO.findInvoiceTotalByOrderId(77L))
                .thenReturn(Optional.of(new BigDecimal("13200")));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        CreateResult result = service.createOrder(1,
                new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 2))), "key-abc");

        assertEquals(77L, result.getOrderId());
        assertEquals("VN55667788", result.getTrackingCode());
        assertEquals(0, new BigDecimal("13200").compareTo(result.getTotalAmount()));
        // Khong sinh ma van don moi: chi thu ghi mot lan
        verify(orderDAO, times(1)).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-14b: khoa idempotency qua dai 64 ky tu - nem DU_LIEU_QUA_DAI")
    void tc14b_khoaIdempotencyQuaDai() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        String keyQuaDai = "k".repeat(65);
        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1))), keyQuaDai));

        assertEquals(ErrorCode.DU_LIEU_QUA_DAI, ex.getErrorCode());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    // -----------------------------------------------------------------
    // TC-15: cong thuc tien
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-15: vi du 1 - 0,5 kg x 12.000 d/kg x 2 -> tong 13.200 d")
    void tc15_viDu1() {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(phone));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        Quote quote = service.quote(List.of(new ItemRequest(1, 2)));

        assertEquals(0, new BigDecimal("6000").compareTo(quote.getTotalFee()
                .divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP)));
        assertEquals(0, new BigDecimal("12000").compareTo(quote.getTotalFee()));
        assertEquals(0, new BigDecimal("1200").compareTo(quote.getVatAmount()));
        assertEquals(0, new BigDecimal("13200").compareTo(quote.getTotalAmount()));
    }

    @Test
    @DisplayName("TC-15b: vi du 2 - 0,35 kg x 12.345 d/kg x 3 -> don gia 4.321, tong 14.259 d")
    void tc15b_viDu2() {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(cosmetic));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        Quote quote = service.quote(List.of(new ItemRequest(6, 3)));

        // donGia = 0,35 x 12.345 = 4.320,75 -> lam tron 4.321
        assertEquals(0, new BigDecimal("12963").compareTo(quote.getTotalFee()));
        assertEquals(0, new BigDecimal("1296").compareTo(quote.getVatAmount()));
        assertEquals(0, new BigDecimal("14259").compareTo(quote.getTotalAmount()));
        // 0,35 x 3 = 1,05 kg
        assertEquals(0, new BigDecimal("1.05").compareTo(quote.getTotalWeight()));
    }

    @Test
    @DisplayName("TC-15c: don gia duoc lam tron truoc khi nhan so luong")
    void tc15c_donGiaLamTronTruoc() throws SQLException {
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(cosmetic));
        when(orderDAO.insertOrder(any(Connection.class), any(Order.class))).thenReturn(100L);

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                List.of(new ItemRequest(6, 3))), null);

        ArgumentCaptor<List<OrderDetail>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderDAO).insertDetails(eq(connection), eq(100L), captor.capture());
        OrderDetail detail = captor.getValue().get(0);

        assertEquals(0, new BigDecimal("4321").compareTo(detail.getUnitPrice()));
        assertEquals(3, detail.getQuantity());
    }

    // -----------------------------------------------------------------
    // TC-16: vuot gioi han
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-16: so luong 1.001 - nem VUOT_GIOI_HAN")
    void tc16_soLuongQuaGioiHan() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(1, 1001))), null));

        assertEquals(ErrorCode.VUOT_GIOI_HAN, ex.getErrorCode());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-16b: 51 dong mat hang - nem VUOT_GIOI_HAN")
    void tc16b_51Dong() {
        List<ItemRequest> items = new ArrayList<>();
        for (int i = 1; i <= 51; i++) {
            items.add(new ItemRequest(i, 1));
        }

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        items), null));

        assertEquals(ErrorCode.VUOT_GIOI_HAN, ex.getErrorCode());
        verify(orderDAO, never()).insertOrder(any(Connection.class), any(Order.class));
    }

    @Test
    @DisplayName("TC-16c: tong khoi luong vuot 5.000 kg - nem VUOT_GIOI_HAN")
    void tc16c_tongKhoiLuongVuotGioiHan() {
        // 6.000 kg / don vi, so luong 1 -> vuot 5.000 kg
        Product nang = OrderServiceTestSupport.product(5, "6000.00", "10000");
        when(productDAO.findByIds(any()))
                .thenReturn(OrderServiceTestSupport.productMap(nang));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(5, 1))), null));

        assertEquals(ErrorCode.VUOT_GIOI_HAN, ex.getErrorCode());
    }

    // -----------------------------------------------------------------
    // Hang hoa khong ton tai
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-16d: hang hoa khong ton tai - nem HANG_HOA_KHONG_TON_TAI")
    void tc16d_hangHoaKhongTonTai() {
        when(productDAO.findByIds(any())).thenReturn(new HashMap<>());

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        AppException ex = assertThrows(AppException.class,
                () -> service.createOrder(1, new CreateOrderRequest("A", "0987654321", "B",
                        List.of(new ItemRequest(999, 1))), null));

        assertEquals(ErrorCode.HANG_HOA_KHONG_TON_TAI, ex.getErrorCode());
        assertEquals(404, ex.getErrorCode().getHttpStatus());
    }

    // -----------------------------------------------------------------
    // Cac ham khac
    // -----------------------------------------------------------------

    @Test
    @DisplayName("TC-17: getSender tra thong tin khach hang theo phien")
    void tc17_getSender() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        Customer customer = service.getSender(1);

        assertNotNull(customer);
        assertEquals("Nguyễn Văn A", customer.getFullName());
        assertEquals("0912345678", customer.getPhone());
    }

    @Test
    @DisplayName("TC-18: findOwnedOrder chi tra don thuoc chinh khach hang")
    void tc18_findOwnedOrder() {
        Order don = new Order();
        don.setId(10L);
        don.setTrackingCode("VN99887766");
        when(orderDAO.findByTrackingCode(1L, "VN99887766")).thenReturn(Optional.of(don));

        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        Optional<Order> result = service.findOwnedOrder(1, "VN99887766");

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    @DisplayName("TC-19: findOwnedOrder voi ma rong - tra Optional rong")
    void tc19_findOwnedOrderMaRong() {
        OrderServiceImpl service = OrderServiceTestSupport.newService(
                orderDAO, productDAO, customerDAO, connectionProvider);

        assertTrue(service.findOwnedOrder(1, "   ").isEmpty());
    }
}
