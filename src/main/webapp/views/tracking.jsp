<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tra cứu vận đơn</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Tra cứu vận đơn</h1>
        <span class="user-info">
            <c:out value="${sessionScope.userName}" default="Khách hàng"/>
        </span>
    </div>
</header>

<main class="container">

    <!-- ================= O TIM KIEM ================= -->
    <section class="card">
        <h2>Nhập mã vận đơn</h2>
        <div class="form-row">
            <label for="maVanDon">Mã vận đơn <span class="required">*</span></label>
            <div class="input-wrap" id="wrapMaVanDon">
                <input type="text" id="maVanDon" name="maVanDon"
                       maxlength="10" autocomplete="off"
                       placeholder="VD: VN12345678" spellcheck="false">
                <span class="tick" aria-hidden="true">&#10003;</span>
            </div>
            <div class="error-text" id="errMaVanDon"></div>
            <p class="hint-text">Mã vận đơn gồm VN và 8 chữ số, ví dụ VN12345678.</p>
        </div>

        <div class="actions">
            <button type="button" class="btn btn-primary" id="btnTimKiem" disabled>
                Tìm kiếm
            </button>
            <a class="btn btn-secondary" href="<c:url value='/'/>">Về trang chính</a>
        </div>
    </section>

    <!-- ================= DANG TAI ================= -->
    <section class="card" id="loading" style="display:none;" aria-live="polite">
        <div class="loading-box">
            <div class="spinner" role="status" aria-label="Đang tra cứu"></div>
            <p class="spinner-text">Đang tra cứu vận đơn...</p>
        </div>
    </section>

    <!-- ================= KHONG TIM THAY ================= -->
    <section class="card empty-state" id="notFound" style="display:none;">
        <div class="empty-icon" aria-hidden="true">&#128230;</div>
        <p>Không tìm thấy thông tin cho mã vận đơn này</p>
    </section>

    <!-- ================= KET QUA ================= -->
    <div id="result" style="display:none;">

        <section class="card">
            <h2>Trạng thái đơn hàng</h2>

            <div class="status-row">
                <span class="status-badge" id="statusBadge">--</span>
                <span class="tracking-code" id="resultMaVanDon">--</span>
            </div>

            <div class="progress" role="progressbar" aria-valuemin="0"
                 aria-valuemax="100" aria-valuenow="0" id="progressWrap">
                <div class="progress-bar" id="progressBar" style="width:0%;"></div>
            </div>
            <p class="progress-text" id="progressText">--</p>

            <div class="grid-2 info-grid">
                <div class="summary">
                    <span class="label">Ngày tạo</span>
                    <span id="infoNgayTao">--</span>
                </div>
                <div class="summary">
                    <span class="label">Người nhận</span>
                    <span id="infoNguoiNhan">--</span>
                </div>
                <div class="summary">
                    <span class="label">Địa chỉ giao</span>
                    <span id="infoDiaChi">--</span>
                </div>
                <div class="summary">
                    <span class="label">Tổng khối lượng</span>
                    <span id="infoKhoiLuong">--</span>
                </div>
            </div>
        </section>

        <section class="card" id="tripCard" style="display:none;">
            <h2>Chuyến xe</h2>
            <div class="grid-2 info-grid">
                <div class="summary">
                    <span class="label">Mã chuyến</span>
                    <span id="tripMaChuyen">--</span>
                </div>
                <div class="summary">
                    <span class="label">Thời gian đi</span>
                    <span id="tripThoiGianDi">--</span>
                </div>
                <div class="summary">
                    <span class="label">Biển số xe</span>
                    <span id="tripBienSo">--</span>
                </div>
                <div class="summary">
                    <span class="label">Tài xế</span>
                    <span id="tripTaiXe">--</span>
                </div>
                <div class="summary">
                    <span class="label">Trạng thái xe</span>
                    <span id="tripTrangThaiXe">--</span>
                </div>
                <div class="summary">
                    <span class="label">Mã lộ trình</span>
                    <span id="routeMa">--</span>
                </div>
                <div class="summary">
                    <span class="label">Khoảng cách dự kiến</span>
                    <span id="routeKhoangCach">--</span>
                </div>
            </div>
        </section>

        <section class="card">
            <h2>Vị trí và lộ trình</h2>

            <div class="grid-2 info-grid">
                <div class="summary">
                    <span class="label">Tọa độ hiện tại</span>
                    <span id="currentLocation">Chưa có dữ liệu</span>
                </div>
                <div class="summary">
                    <span class="label">Cập nhật lúc</span>
                    <span id="currentTime">--</span>
                </div>
            </div>

            <div class="route-map-wrap">
                <svg class="route-map" id="routeMap" viewBox="0 0 600 240"
                     role="img" aria-label="Sơ đồ lộ trình">
                    <polyline id="routePolyline" points=""></polyline>
                    <circle id="currentPoint" cx="0" cy="0" r="7"></circle>
                </svg>
                <p class="empty-map" id="mapEmpty">Chưa có nhật ký tọa độ của chuyến xe.</p>
            </div>

            <h3 class="sub-title">Nhật ký tọa độ gần nhất</h3>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Thời gian</th>
                        <th>Vĩ độ</th>
                        <th>Kinh độ</th>
                    </tr>
                    </thead>
                    <tbody id="locationBody">
                    <tr class="empty-row">
                        <td colspan="3">Chưa có dữ liệu tọa độ</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</main>

<!-- ================= POP-UP LOI KET NOI ================= -->
<div class="overlay" id="errorOverlay">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="errorTitle">
        <h3 id="errorTitle">Lỗi kết nối</h3>
        <div class="alert alert-danger" id="errorMessage" role="alert"></div>
        <div class="actions">
            <button type="button" class="btn btn-primary" id="btnDongLoi">Đóng</button>
        </div>
    </div>
</div>

<script>
    window.APP_CONTEXT_PATH = '<c:url value="/"/>';
    window.REQUEST_TIMEOUT_MS = 5000;
</script>
<script src="<c:url value='/assets/js/tracking.js'/>"></script>
</body>
</html>
