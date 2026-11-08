<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tạo đơn thành công</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Tạo đơn thành công</h1>
    </div>
</header>

<main class="container">
    <section class="card success-box">
        <div class="success-icon" aria-hidden="true">&#10003;</div>

        <h2 style="margin-bottom:4px;">Đơn hàng đã được tạo thành công</h2>
        <p style="color:var(--chu-phu);margin:0 0 8px;">
            Vui lòng lưu lại mã vận đơn để theo dõi hàng.
        </p>

        <div class="tracking-code" id="maVanDon">
            <c:out value="${donHang.trackingCode}"/>
        </div>

        <div style="text-align:left;max-width:520px;margin:0 auto 24px;">
            <div class="summary">
                <span class="label">Người nhận</span>
                <span><c:out value="${donHang.receiverName}"/></span>
            </div>
            <div class="summary">
                <span class="label">SĐT người nhận</span>
                <span><c:out value="${donHang.receiverPhone}"/></span>
            </div>
            <div class="summary">
                <span class="label">Địa chỉ giao hàng</span>
                <span><c:out value="${donHang.deliveryAddress}"/></span>
            </div>
            <div class="summary">
                <span class="label">Tổng khối lượng</span>
                <span><c:out value="${donHang.totalWeight}"/> kg</span>
            </div>
            <div class="summary">
                <span class="label">Trạng thái</span>
                <span>Chờ lấy hàng</span>
            </div>
        </div>

        <div class="actions" style="justify-content:center;">
            <a class="btn btn-primary" href="<c:url value='/'/>">Về màn hình chính</a>
            <a class="btn btn-secondary" href="<c:url value='/don-hang/tao'/>">
                Tạo đơn hàng mới
            </a>
        </div>
    </section>
</main>
</body>
</html>
