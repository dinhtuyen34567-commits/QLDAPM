<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tạo đơn hàng vận chuyển</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Tạo đơn hàng vận chuyển</h1>
        <span class="user-info">
            <c:out value="${sessionScope.userName}" default="Khách hàng"/>
        </span>
    </div>
</header>

<main class="container">

    <%-- Thong bao chung tu server (loi 4xx khi goi API) --%>
    <div id="serverError" class="alert alert-danger" style="display:none;" role="alert"></div>

    <!-- ================= NGUOI GUI ================= -->
    <section class="card">
        <h2>Thông tin người gửi</h2>
        <div class="grid-2">
            <div class="form-row">
                <label for="tenNguoiGui">Tên người gửi</label>
                <input type="text" id="tenNguoiGui" name="tenNguoiGui"
                       value="" readonly disabled>
            </div>
            <div class="form-row">
                <label for="sdtNguoiGui">SĐT người gửi</label>
                <input type="tel" id="sdtNguoiGui" name="sdtNguoiGui"
                       value="" readonly disabled>
            </div>
        </div>
    </section>

    <!-- ================= NGUOI NHAN ================= -->
    <section class="card">
        <h2>Thông tin người nhận</h2>

        <div class="form-row">
            <label for="tenNguoiNhan">Tên người nhận <span class="required">*</span></label>
            <div class="input-wrap" id="wrapTenNguoiNhan">
                <input type="text" id="tenNguoiNhan" name="tenNguoiNhan"
                       maxlength="100" autocomplete="off">
                <span class="tick" aria-hidden="true">&#10003;</span>
            </div>
            <div class="error-text" id="errTenNguoiNhan"></div>
        </div>

        <div class="form-row">
            <label for="sdtNguoiNhan">SĐT người nhận <span class="required">*</span></label>
            <div class="input-wrap" id="wrapSdtNguoiNhan">
                <input type="tel" id="sdtNguoiNhan" name="sdtNguoiNhan"
                       maxlength="11" autocomplete="off" inputmode="numeric">
                <span class="tick" aria-hidden="true">&#10003;</span>
            </div>
            <div class="error-text" id="errSdtNguoiNhan"></div>
        </div>

        <div class="form-row">
            <label for="diaChiGiao">Địa chỉ giao hàng <span class="required">*</span></label>
            <div class="input-wrap" id="wrapDiaChiGiao">
                <input type="text" id="diaChiGiao" name="diaChiGiao"
                       maxlength="255" autocomplete="off">
                <span class="tick" aria-hidden="true">&#10003;</span>
            </div>
            <div class="error-text" id="errDiaChiGiao"></div>
        </div>
    </section>

    <!-- ================= HANG HOA ================= -->
    <section class="card">
        <h2>Hàng hóa</h2>

        <div class="alert alert-danger" id="alertSoLuong" style="display:none;" role="alert">
            Số lượng phải lớn hơn 0
        </div>

        <div class="grid-2">
            <div class="form-row">
                <label for="productSelect">Chọn hàng hóa</label>
                <select id="productSelect">
                    <option value="">-- Chọn hàng hóa --</option>
                </select>
            </div>
            <div class="form-row">
                <label for="quantityInput">Số lượng</label>
                <input type="number" id="quantityInput" value="" min="1" step="1"
                       placeholder="Ví dụ: 2">
            </div>
        </div>

        <div class="actions" style="margin-bottom:20px;">
            <button type="button" class="btn btn-secondary" id="btnThemMatHang">
                Thêm mặt hàng
            </button>
        </div>

        <div class="table-wrap">
            <table id="detailTable">
                <thead>
                <tr>
                    <th>Mã hàng</th>
                    <th>Tên hàng</th>
                    <th>Khối lượng (kg)</th>
                    <th>Số lượng</th>
                    <th>Đơn giá (đ)</th>
                    <th>Thành tiền (đ)</th>
                </tr>
                </thead>
                <tbody id="detailBody">
                <tr class="empty-row" id="emptyRow">
                    <td colspan="6">Chưa có mặt hàng nào</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="summary">
            <span class="label">Tổng khối lượng</span>
            <span id="tongKhoiLuong">0 kg</span>
        </div>
    </section>

    <!-- ================= CHOT DON ================= -->
    <section class="card">
        <h2>Chốt đơn</h2>
        <div class="actions">
            <button type="button" class="btn btn-primary" id="btnLuuDonHang" disabled>
                Lưu đơn hàng
            </button>
            <a class="btn btn-secondary" href="<c:url value='/'/>">Hủy</a>
        </div>
    </section>
</main>

<!-- ================= POP-UP XAC NHAN ================= -->
<div class="overlay" id="confirmOverlay" role="dialog" aria-modal="true"
     aria-labelledby="confirmTitle">
    <div class="modal">
        <h3 id="confirmTitle">Xác nhận tạo đơn</h3>

        <div id="confirmBody">
            <div class="summary">
                <span class="label">Tổng khối lượng</span>
                <span id="popupKhoiLuong">0 kg</span>
            </div>
            <div class="summary">
                <span class="label">Tổng cước</span>
                <span id="popupCuoc">0 đ</span>
            </div>
            <div class="summary">
                <span class="label">VAT (<span id="popupVatPercent">10</span>%)</span>
                <span id="popupVat">0 đ</span>
            </div>
            <div class="summary total">
                <span class="label">Tổng thanh toán</span>
                <span id="popupTongThanhToan">0 đ</span>
            </div>
        </div>

        <%-- Trang thái dang gui: bien icon xoay (ngoai le 11.1) --%>
        <div id="confirmLoading" style="display:none;">
            <div class="spinner" role="status" aria-label="Đang xử lý"></div>
            <p class="spinner-text">Đang xử lý, vui lòng chờ...</p>
        </div>

        <%-- Trang thai loi: pop-up loi kem nut Dong (ngoai le 11.1) --%>
        <div id="confirmError" style="display:none;">
            <div class="alert alert-danger" id="confirmErrorText" role="alert"></div>
            <div class="actions">
                <button type="button" class="btn btn-primary" id="btnDongLoi">Đóng</button>
            </div>
        </div>

        <div class="actions" id="confirmActions">
            <button type="button" class="btn btn-secondary" id="btnHuyBo">Hủy bỏ</button>
            <button type="button" class="btn btn-primary" id="btnXacNhan">Xác nhận</button>
        </div>
    </div>
</div>

<!-- ================= TOAST (ngoai le 9.1) ================= -->
<div class="toast" id="toast" role="alert"></div>

<script>
    // Khoa idempotency sinh mot lan khi mo man hinh, giu nguyen khi thu lai
    // sau loi mang (BR-13).
    window.IDEMPOTENCY_KEY = (crypto && crypto.randomUUID)
        ? crypto.randomUUID()
        : 'key-' + Date.now() + '-' + Math.random().toString(16).slice(2);

    window.APP_CONTEXT_PATH = '<c:url value="/"/>';
    window.REQUEST_TIMEOUT_MS = 10000;
</script>
<script src="<c:url value='/assets/js/order-create.js'/>"></script>
</body>
</html>
