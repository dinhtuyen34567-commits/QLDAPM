package com.shipping.service.impl;

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
import com.shipping.model.OrderStatus;
import com.shipping.model.PaymentMethod;
import com.shipping.model.Product;
import com.shipping.model.Quote;
import com.shipping.service.OrderService;
import com.shipping.util.OrderUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Triển khai {@link OrderService}.
 *
 * <p>Lớp này giữ toàn bộ nghiệp vụ của UC-01: kiểm tra đầu vào theo đúng
 * thứ tự ở mục 5.3, tính cước theo công thức mục 5.4, chống tạo trùng đơn theo
 * BR-13 và ghi ba bảng trong một giao dịch duy nhất theo BR-07.</p>
 *
 * <p>Mọi phụ thuộc được nhận qua constructor nên có thể kiểm thử bằng Mockito
 * mà không cần MySQL thật.</p>
 */
public class OrderServiceImpl implements OrderService {

    /** BR-08: tối đa 5 lần sinh lại mã vận đơn khi trùng khóa UNIQUE. */
    private static final int MAX_TRACKING_CODE_ATTEMPTS = 5;

    /** BR-03: độ dài tối đa tên người nhận (khớp cột tenNguoiNhan). */
    private static final int MAX_RECEIVER_NAME_LENGTH = 100;

    /** BR-03: độ dài tối đa địa chỉ giao hàng (khớp cột diaChiGiao). */
    private static final int MAX_ADDRESS_LENGTH = 255;

    /** BR-13: độ dài tối đa của khóa idempotency (khớp cột idempotencyKey). */
    private static final int MAX_IDEMPOTENCY_KEY_LENGTH = 64;

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;
    private final ConnectionProvider connectionProvider;

    /** Nguồn sinh mã vận đơn, thay thế được trong test. */
    private final Supplier<String> trackingCodeGen;

    /** Nguồn sinh mã hóa đơn, thay thế được trong test. */
    private final Supplier<String> invoiceCodeGen;

    /** Thuế suất VAT (%), đọc từ cấu hình {@code billing.vat-percent}. */
    private final BigDecimal vatPercent;

    /** BR-14: số lượng tối đa mỗi dòng. */
    private final int maxQuantityPerLine;

    /** BR-14: số dòng tối đa mỗi đơn. */
    private final int maxLines;

    /** BR-14: tổng khối lượng tối đa mỗi đơn (kg). */
    private final BigDecimal maxTotalWeight;

    /**
     * Khởi tạo service với đầy đủ phụ thuộc.
     *
     * @param orderDAO           truy cập bảng đơn hàng, chi tiết, hóa đơn
     * @param productDAO         truy cập bảng hàng hóa
     * @param customerDAO        truy cập thông tin khách hàng
     * @param connectionProvider nguồn cấp kết nối CSDL
     * @param trackingCodeGen    nguồn sinh mã vận đơn
     * @param invoiceCodeGen     nguồn sinh mã hóa đơn
     * @param vatPercent         thuế suất VAT (%)
     * @param maxQuantityPerLine số lượng tối đa mỗi dòng (BR-14)
     * @param maxLines           số dòng tối đa mỗi đơn (BR-14)
     * @param maxTotalWeight     tổng khối lượng tối đa mỗi đơn (kg) (BR-14)
     */
    public OrderServiceImpl(OrderDAO orderDAO,
                            ProductDAO productDAO,
                            CustomerDAO customerDAO,
                            ConnectionProvider connectionProvider,
                            Supplier<String> trackingCodeGen,
                            Supplier<String> invoiceCodeGen,
                            BigDecimal vatPercent,
                            int maxQuantityPerLine,
                            int maxLines,
                            BigDecimal maxTotalWeight) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.customerDAO = customerDAO;
        this.connectionProvider = connectionProvider;
        this.trackingCodeGen = trackingCodeGen;
        this.invoiceCodeGen = invoiceCodeGen;
        this.vatPercent = vatPercent;
        this.maxQuantityPerLine = maxQuantityPerLine;
        this.maxLines = maxLines;
        this.maxTotalWeight = maxTotalWeight;
    }

    @Override
    public Customer getSender(long customerId) {
        // Khong tim thay khach hang -> nguoi dung khong phai khach hang (BR-01)
        return customerDAO.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.KHONG_CO_QUYEN));
    }

    @Override
    public Quote quote(List<ItemRequest> items) {
        // Tinh cuoc KHONG duoc chua thong tin nguoi nhan, nen chi kiem tra danh sach
        // mat hang va gioi han. Ngoai le 9.1 van ap dung: danh sach rong -> 422.
        CalculatedItems calculated = resolveItems(items);
        return buildQuote(calculated);
    }

    @Override
    public CreateResult createOrder(long customerId, CreateOrderRequest req,
                                    String idempotencyKey) {
        if (req == null) {
            throw new AppException(ErrorCode.THIEU_THONG_TIN);
        }

        // (1) Vai tro khach hang: phai co ban ghi trong tblKhachHang
        getSender(customerId);

        // (2) Ten nguoi nhan va dia chi giao hang
        String receiverName = trimToNull(req.getReceiverName());
        String deliveryAddress = trimToNull(req.getDeliveryAddress());
        if (receiverName == null || deliveryAddress == null) {
            throw new AppException(ErrorCode.THIEU_THONG_TIN);
        }
        if (receiverName.length() > MAX_RECEIVER_NAME_LENGTH
                || deliveryAddress.length() > MAX_ADDRESS_LENGTH) {
            throw new AppException(ErrorCode.DU_LIEU_QUA_DAI);
        }

        // (3) So dien thoai nguoi nhan
        String receiverPhone = trimToNull(req.getReceiverPhone());
        if (receiverPhone == null || !OrderUtils.isValidPhone(receiverPhone)) {
            throw new AppException(ErrorCode.SDT_KHONG_HOP_LE);
        }

        // Kiem tra do dai khoa idempotency truoc khi ghi CSDL (BR-13)
        String key = trimToNull(idempotencyKey);
        if (key != null && key.length() > MAX_IDEMPOTENCY_KEY_LENGTH) {
            throw new AppException(ErrorCode.DU_LIEU_QUA_DAI);
        }

        // (4) Danh sach mat hang rong -> 9.1
        // (5) (6) (7) kiem tra so luong, gioi han va su ton tai cua hang hoa
        CalculatedItems calculated = resolveItems(req.getItems());

        // BR-13: da co don voi cung khoa -> tra lai don cu, khong ghi them
        if (key != null) {
            Optional<Order> existing = orderDAO.findByIdempotencyKey(customerId, key);
            if (existing.isPresent()) {
                Order o = existing.get();
                BigDecimal total = orderDAO.findInvoiceTotalByOrderId(o.getId())
                        .orElse(BigDecimal.ZERO);
                return new CreateResult(o.getId(), o.getTrackingCode(), total);
            }
        }

        return persistOrder(customerId, receiverName, receiverPhone, deliveryAddress,
                key, calculated);
    }

    @Override
    public Optional<Order> findOwnedOrder(long customerId, String trackingCode) {
        String code = trimToNull(trackingCode);
        if (code == null) {
            return Optional.empty();
        }
        return orderDAO.findByTrackingCode(customerId, code);
    }

    @Override
    public Optional<Order> findOwnedOrderByIdempotencyKey(long customerId,
                                                          String idempotencyKey) {
        String key = trimToNull(idempotencyKey);
        if (key == null) {
            return Optional.empty();
        }
        return orderDAO.findByIdempotencyKey(customerId, key);
    }

    // -----------------------------------------------------------------
    // Kiem tra va tinh toan
    // -----------------------------------------------------------------

    /**
     * Kiem tra danh sach mat hang theo thu tu co dinh o muc 5.3:
     * (4) danh sach rong, (5) so luong, (6) gioi han, (7) hang hoa ton tai.
     *
     * @param items danh sách mặt hàng do client gửi
     * @return các dòng đã gộp kèm tổng khối lượng và tổng cước
     */
    private CalculatedItems resolveItems(List<ItemRequest> items) {
        // (4) Danh sach rong
        if (items == null || items.isEmpty()) {
            throw new AppException(ErrorCode.DON_HANG_RONG);
        }

        // BR-09: gop cac mat hang truong hop cung id, cong don so luong
        Map<Integer, Integer> quantityByProduct = new LinkedHashMap<>();
        for (ItemRequest item : items) {
            if (item == null) {
                throw new AppException(ErrorCode.SO_LUONG_KHONG_HOP_LE);
            }
            // (5) So luong phai la so nguyen > 0
            if (item.getQuantity() <= 0) {
                throw new AppException(ErrorCode.SO_LUONG_KHONG_HOP_LE);
            }
            quantityByProduct.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        // (6) Gioi han: so dong, so luong moi dong, tong khoi luong
        if (quantityByProduct.size() > maxLines) {
            throw new AppException(ErrorCode.VUOT_GIOI_HAN);
        }
        for (Map.Entry<Integer, Integer> e : quantityByProduct.entrySet()) {
            if (e.getValue() > maxQuantityPerLine) {
                throw new AppException(ErrorCode.VUOT_GIOI_HAN);
            }
        }

        // (7) Hang hoa ton tai
        Map<Integer, Product> products = productDAO.findByIds(quantityByProduct.keySet());
        for (Integer productId : quantityByProduct.keySet()) {
            if (!products.containsKey(productId)) {
                throw new AppException(ErrorCode.HANG_HOA_KHONG_TON_TAI);
            }
        }

        // Tinh don gia va tong khoi luong server tu du lieu CSDL (BR-06).
        // Don gia duoc lam tron TRUOC khi nhan so luong de chi tiet don
        // khop hoa don (muc 5.4).
        List<OrderDetail> details = new ArrayList<>();
        List<Quote.QuoteLine> lines = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> e : quantityByProduct.entrySet()) {
            Product product = products.get(e.getKey());
            int quantity = e.getValue();

            BigDecimal unitPrice = OrderUtils.roundToVnd(
                    product.getWeight().multiply(product.getFreightRate()));
            BigDecimal lineFee = unitPrice.multiply(BigDecimal.valueOf(quantity));

            details.add(new OrderDetail(0L, product.getId(), quantity, unitPrice));
            lines.add(new Quote.QuoteLine(product.getId(), quantity, unitPrice,
                    product.getWeight()));
            totalWeight = totalWeight.add(
                    product.getWeight().multiply(BigDecimal.valueOf(quantity)));
            totalFee = totalFee.add(lineFee);
        }

        totalWeight = totalWeight.setScale(2, java.math.RoundingMode.HALF_UP);
        if (maxTotalWeight != null && totalWeight.compareTo(maxTotalWeight) > 0) {
            throw new AppException(ErrorCode.VUOT_GIOI_HAN);
        }

        return new CalculatedItems(details, lines, totalWeight, OrderUtils.roundToVnd(totalFee));
    }

    /**
     * Dựng đối tượng {@link Quote} từ kết quả tính toán.
     */
    private Quote buildQuote(CalculatedItems calculated) {
        BigDecimal vatAmount = OrderUtils.roundToVnd(
                calculated.totalFee.multiply(vatPercent)
                        .divide(BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP));
        BigDecimal totalAmount = calculated.totalFee.add(vatAmount);

        Quote quote = new Quote(calculated.totalWeight, calculated.totalFee, vatPercent,
                vatAmount, totalAmount);
        quote.setLines(calculated.lines);
        return quote;
    }

    // -----------------------------------------------------------------
    // Ghi CSDL trong mot giao dich
    // -----------------------------------------------------------------

    /**
     * Ghi đơn hàng, chi tiết và hóa đơn trong một giao dịch.
     *
     * <p>Ba lần ghi thành công cùng nhau hoặc hủy cùng nhau (BR-07).</p>
     */
    private CreateResult persistOrder(long customerId,
                                      String receiverName,
                                      String receiverPhone,
                                      String deliveryAddress,
                                      String idempotencyKey,
                                      CalculatedItems calculated) {
        Connection c = null;
        try {
            c = connectionProvider.getConnection();
            c.setAutoCommit(false);

            // BR-08: sinh ma van don, thu lai toi da 5 lan khi trung khoa UNIQUE
            InsertedOrder inserted = insertOrderWithRetry(c, customerId, receiverName,
                    receiverPhone, deliveryAddress, idempotencyKey, calculated);
            long orderId = inserted.orderId;

            // Gan id don hang vao tung dong chi tiet
            List<OrderDetail> details = new ArrayList<>();
            for (OrderDetail d : calculated.details) {
                details.add(new OrderDetail(orderId, d.getProductId(), d.getQuantity(),
                        d.getUnitPrice()));
            }
            orderDAO.insertDetails(c, orderId, details);

            // Hoa don: tong tien da gom VAT, phuong thuc CHUA_THANH_TOAN (BR-12)
            Quote quote = buildQuote(calculated);
            String invoiceCode = generateInvoiceCodeWithRetry();
            Invoice invoice = new Invoice(0L, invoiceCode, orderId, quote.getTotalAmount(),
                    vatPercent, PaymentMethod.CHUA_THANH_TOAN.name());
            orderDAO.insertInvoice(c, invoice);

            c.commit();

            return new CreateResult(orderId, inserted.trackingCode, quote.getTotalAmount());
        } catch (AppException e) {
            rollbackQuietly(c);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(c);
            throw new AppException(ErrorCode.LOI_HE_THONG,
                    "Da xay ra loi he thong. Vui long thu lai", e);
        } catch (RuntimeException e) {
            rollbackQuietly(c);
            throw e;
        } finally {
            closeQuietly(c);
        }
    }

    /**
     * Thêm đơn hàng, thử lại tối đa 5 lần nếu mã vận đơn trùng (BR-08).
     *
     * <p>Quá 5 lần trả lỗi 500 {@code LOI_HE_THONG}.</p>
     */
    private InsertedOrder insertOrderWithRetry(Connection c,
                                               long customerId,
                                               String receiverName,
                                               String receiverPhone,
                                               String deliveryAddress,
                                               String idempotencyKey,
                                               CalculatedItems calculated) throws SQLException {
        DuplicateKeyException lastDuplicate = null;

        for (int attempt = 1; attempt <= MAX_TRACKING_CODE_ATTEMPTS; attempt++) {
            Order order = new Order();
            order.setTrackingCode(trackingCodeGen.get());
            order.setCreatedAt(LocalDateTime.now());
            // BR-11: don moi luon CHO_LAY va chuyenXeId de trong.
            // Dung hang Enum OrderStatus de tranh sai chuoi (Coding Conventions §1).
            order.setStatus(OrderStatus.CHO_LAY.name());
            order.setTotalWeight(calculated.totalWeight);
            order.setCustomerId(customerId);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setDeliveryAddress(deliveryAddress);
            order.setTripId(null);
            order.setIdempotencyKey(idempotencyKey);

            try {
                long id = orderDAO.insertOrder(c, order);
                return new InsertedOrder(id, order.getTrackingCode());
            } catch (DuplicateKeyException e) {
                lastDuplicate = e;
                // Trung khoa UNIQUE (khachHangId, idempotencyKey) khac voi trung
                // ma van don: khi do client dang gui lai cung khoa, nen tra ve
                // don da co thay vi sinh ma van don moi (BR-13).
                if (idempotencyKey != null && isIdempotencyConflict(e)) {
                    Optional<Order> existing =
                            orderDAO.findByIdempotencyKey(customerId, idempotencyKey);
                    if (existing.isPresent()) {
                        return new InsertedOrder(existing.get().getId(),
                                existing.get().getTrackingCode());
                    }
                }
                // Trung ma van don: sinh ma moi va thu lai (BR-08).
            }
        }
        throw new AppException(ErrorCode.LOI_HE_THONG,
                "Khong the sinh ma van don duy nhat sau "
                        + MAX_TRACKING_CODE_ATTEMPTS + " lan thu", lastDuplicate);
    }

    /**
     * Ket qua chen don hang moi: id va ma van don da sinh.
     *
     * <p>Ma van don duoc giu lai de khong can truy van lai sau khi insert
     * (tiet kiem mot vong SELECT, dong thoi giu service khong phu thuoc
     * JDBC o buoc tra ket qua).</p>
     */
    private static final class InsertedOrder {

        private final long orderId;
        private final String trackingCode;

        private InsertedOrder(long orderId, String trackingCode) {
            this.orderId = orderId;
            this.trackingCode = trackingCode;
        }
    }

    /**
     * Sinh mã hóa đơn chưa dùng.
     *
     * <p>Hàm này chỉ sinh mã, <b>không</b> ghi CSDL: việc ghi hóa đơn do
     * {@code persistOrder} thực hiện đúng một lần. Khi mã trùng khóa UNIQUE
     * {@code maHoaDon} thì sinh lại tối đa 5 lần (mục 5.6).</p>
     */
    private String generateInvoiceCodeWithRetry() {
        DuplicateKeyException lastDuplicate = null;

        for (int attempt = 1; attempt <= MAX_TRACKING_CODE_ATTEMPTS; attempt++) {
            String code = invoiceCodeGen.get();
            if (!orderDAO.invoiceCodeExists(code)) {
                return code;
            }
            lastDuplicate = new DuplicateKeyException(
                    "Trung ma hoa don: " + code);
        }
        throw new AppException(ErrorCode.LOI_HE_THONG,
                "Khong the sinh ma hoa don duy nhat sau "
                        + MAX_TRACKING_CODE_ATTEMPTS + " lan thu", lastDuplicate);
    }

    /**
     * Phân biệt trùng khóa idempotency với trùng mã vận đơn.
     *
     * <p>CSDL có hai ràng buộc UNIQUE trên {@code tblDonHang}:</p>
     * <ul>
     *   <li>{@code maVanDon} — trùng thì sinh mã vận đơn mới (BR-08);</li>
     *   <li>{@code UNIQUE (khachHangId, idempotencyKey)} — trùng thì client đang
     *       gửi lại cùng khóa, cần trả lại đơn cũ (BR-13).</li>
     * </ul>
     *
     * <p>DAO đã trích tên ràng buộc từ thông báo lỗi MySQL, nên phân biệt dựa
     * trên tên ràng buộc thay vì dò chuỗi thông báo. Với ràng buộc nhiều cột,
     * MySQL chỉ nêu cột đầu tiên ({@code khachHangId}), nên: vi phạm
     * {@code maVanDon} là trùng mã vận đơn, còn lại là trùng khóa idempotency.</p>
     *
     * @param e lỗi trùng khóa
     * @return {@code true} nếu là trùng khóa idempotency
     */
    private boolean isIdempotencyConflict(DuplicateKeyException e) {
        // Khong xac dinh duoc ten rang buoc -> gia nhi la trung ma van don,
        // de hanh dong sinh ma moi (BR-08) chay dung.
        if (e.getConstraintName() == null) {
            return false;
        }
        return !e.isConstraint("mavandon");
    }

    // -----------------------------------------------------------------
    // Tien ich
    // -----------------------------------------------------------------

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void rollbackQuietly(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.rollback();
        } catch (SQLException ignored) {
            // Khong nem loi tiep: loi goc dang xu ly moi la thong tin quan trong
        }
    }

    private void closeQuietly(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (SQLException ignored) {
            // Khong nem loi tiep khi dong ket noi
        }
    }

    /**
     * Kết quả trung gian sau khi kiem tra danh sach mat hang.
     */
    private static final class CalculatedItems {

        private final List<OrderDetail> details;
        private final List<Quote.QuoteLine> lines;
        private final BigDecimal totalWeight;
        private final BigDecimal totalFee;

        private CalculatedItems(List<OrderDetail> details, List<Quote.QuoteLine> lines,
                                BigDecimal totalWeight, BigDecimal totalFee) {
            this.details = details;
            this.lines = lines;
            this.totalWeight = totalWeight;
            this.totalFee = totalFee;
        }
    }
}
