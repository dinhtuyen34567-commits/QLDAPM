<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết xử lý yêu cầu khiếu nại</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .badge-moi { background: #e8f0fe; color: #1a73e8; padding: 4px 10px; border-radius: 4px; font-weight: bold; }
        .badge-dangxuly { background: #fef7e0; color: #b06000; padding: 4px 10px; border-radius: 4px; font-weight: bold; }
        .badge-dadong { background: #e6f4ea; color: #137333; padding: 4px 10px; border-radius: 4px; font-weight: bold; }
        .info-section { margin-bottom: 24px; }
        .info-section h3 { margin-top: 0; color: var(--chu-chinh); font-size: 15px; border-bottom: 1px dashed var(--xam-vien); padding-bottom: 6px; }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Xử lý Yêu cầu Khiếu nại</h1>
        <div class="user-bar">
            <span class="user-info">
                Nhân viên CSKH: <c:out value="${sessionScope.userName}" default="Nhân viên CSKH"/>
            </span>
            <a href="<c:url value='/dang-xuat'/>" class="btn btn-secondary" style="padding: 4px 8px; font-size:12px;">Đăng xuất</a>
        </div>
    </div>
</header>

<main class="container">
    <div style="margin-bottom: 12px;">
        <a href="<c:url value='/cskh/yeu-cau'/>" class="btn btn-secondary">&larr; Quay lại danh sách yêu cầu</a>
    </div>

    <div id="alertBox" class="alert" style="display:none;" role="alert"></div>

    <!-- KHỐI 1: THÔNG TIN YÊU CẦU -->
    <div class="card info-section">
        <h3>1. Thông tin Yêu cầu Hỗ trợ</h3>
        <div class="grid-2 info-grid">
            <div class="summary"><span class="label">Mã yêu cầu:</span><strong id="detailMaYeuCau">--</strong></div>
            <div class="summary"><span class="label">Trạng thái:</span><span id="detailTrangThai">--</span></div>
            <div class="summary"><span class="label">Nhân viên phụ trách:</span><span id="detailNhanVien">Chưa có người nhận</span></div>
        </div>
        <div style="margin-top: 12px;">
            <span class="label">Nội dung khiếu nại từ khách hàng:</span>
            <div id="detailNoiDung" style="background:#f9f9f9; padding: 12px; border-radius: 6px; margin-top: 6px; white-space: pre-wrap;">--</div>
        </div>
    </div>

    <!-- KHỐI 2: THÔNG TIN KHÁCH HÀNG -->
    <div class="card info-section">
        <h3>2. Thông tin Khách hàng</h3>
        <div class="grid-2 info-grid">
            <div class="summary"><span class="label">Họ tên khách:</span><span id="detailKhachTen">--</span></div>
            <div class="summary"><span class="label">Số điện thoại:</span><span id="detailKhachSdt">--</span></div>
            <div class="summary"><span class="label">Email:</span><span id="detailKhachEmail">--</span></div>
        </div>
    </div>

    <!-- KHỐI 3: CHI TIẾT ĐƠN HÀNG BỊ KHIẾU NẠI -->
    <div class="card info-section">
        <h3>3. Chi tiết Đơn hàng bị khiếu nại</h3>
        <div class="grid-2 info-grid">
            <div class="summary"><span class="label">Mã vận đơn:</span><strong id="detailMaVanDon">--</strong></div>
            <div class="summary"><span class="label">Trạng thái đơn:</span><span id="detailTrangThaiDon">--</span></div>
            <div class="summary"><span class="label">Người nhận:</span><span id="detailNguoiNhan">--</span></div>
            <div class="summary"><span class="label">SĐT người nhận:</span><span id="detailSdtNhan">--</span></div>
            <div class="summary"><span class="label">Địa chỉ giao:</span><span id="detailDiaChi">--</span></div>
            <div class="summary"><span class="label">Tổng khối lượng:</span><span id="detailKhoiLuong">--</span></div>
        </div>

        <h4 style="margin: 16px 0 8px; font-size: 13px;">Danh sách hàng hóa trong đơn:</h4>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Tên mặt hàng</th>
                    <th style="width: 120px; text-align: center;">Số lượng</th>
                    <th style="width: 150px; text-align: right;">Đơn giá (đ)</th>
                </tr>
                </thead>
                <tbody id="detailItemsBody">
                <tr><td colspan="3">Không có mặt hàng nào</td></tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- KHỐI 4: XỬ LÝ VÀ PHẢN HỒI (BR-24, BR-25, BR-26) -->
    <div class="card">
        <h3>4. Xử lý & Phản hồi cho Khách hàng</h3>
        <form id="replyForm">
            <div class="form-row">
                <label for="phanHoi">Nội dung phản hồi giải quyết <span class="required">*</span></label>
                <div class="input-wrap">
                    <textarea id="phanHoi" rows="4" style="width:100%; padding:8px; border:1px solid var(--xam-vien); border-radius:4px; font-family:inherit;"
                              placeholder="Nhập nội dung phản hồi cho khách hàng..."></textarea>
                </div>
                <p class="hint-text">Ghi chú: Đóng yêu cầu bắt buộc phải có nội dung phản hồi.</p>
            </div>

            <div class="actions" style="margin-top: 16px;">
                <button type="button" class="btn btn-primary" id="btnTiepNhan">
                    Tiếp nhận xử lý (Đang xử lý)
                </button>
                <button type="button" class="btn btn-secondary" id="btnDongYeuCau" style="background:#137333; color:#fff; border-color:#137333;">
                    Đóng yêu cầu (Hoàn tất)
                </button>
            </div>
        </form>
    </div>
</main>

<script>
    window.APP_CONTEXT_PATH = '<c:url value="/"/>';
</script>
<script src="<c:url value='/assets/js/support-manage.js'/>"></script>
</body>
</html>
