<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chính - Hệ thống Logistics Thông minh</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 16px;
            margin-top: 16px;
        }
        .menu-card {
            border: 1px solid var(--xam-vien);
            border-radius: 8px;
            padding: 16px;
            background: #fff;
            transition: transform 0.15s, box-shadow 0.15s;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .menu-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        .menu-card h3 {
            margin: 0 0 8px;
            font-size: 16px;
            color: var(--xanh-duong);
        }
        .menu-card p {
            margin: 0 0 16px;
            color: var(--chu-phu);
            font-size: 13px;
        }
        .header-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Hệ thống Đơn hàng Vận chuyển</h1>
        <div class="header-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <span class="user-info">
                        Xin chào, <strong><c:out value="${sessionScope.userName}"/></strong>
                        (<c:choose>
                            <c:when test="${sessionScope.role == 'STAFF'}">Nhân viên CSKH</c:when>
                            <c:otherwise>Khách hàng</c:otherwise>
                        </c:choose>)
                    </span>
                    <a href="<c:url value='/dang-xuat'/>" class="btn btn-secondary" style="padding: 4px 8px; font-size: 12px;">Đăng xuất</a>
                </c:when>
                <c:otherwise>
                    <a href="<c:url value='/dang-nhap'/>" class="btn btn-primary" style="padding: 4px 12px; font-size: 13px;">Đăng nhập</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<main class="container">
    <div class="card">
        <h2>Chào mừng đến với Cổng dịch vụ vận chuyển</h2>
        <p>Hệ thống cung cấp đầy đủ các tiện ích tạo đơn hàng, tra cứu hành trình vận đơn trực tuyến và giải quyết khiếu nại.</p>

        <div class="menu-grid">
            <div class="menu-card">
                <div>
                    <h3>1. Tạo đơn hàng mới</h3>
                    <p>Khởi tạo vận đơn vận chuyển, tính cước phí tự động và xác nhận mã vận đơn.</p>
                </div>
                <a class="btn btn-primary" href="<c:url value='/don-hang/tao'/>">Tạo đơn hàng</a>
            </div>

            <div class="menu-card">
                <div>
                    <h3>2. Tra cứu vận đơn</h3>
                    <p>Tra cứu tiến trình giao hàng, lộ trình di chuyển và tọa độ GPS của xe tải theo thời gian thực.</p>
                </div>
                <a class="btn btn-secondary" href="<c:url value='/tra-cuu'/>">Tra cứu vận đơn</a>
            </div>

            <div class="menu-card">
                <div>
                    <h3>3. Khiếu nại & Hỗ trợ</h3>
                    <p>Gửi phản hồi, sự cố hoặc yêu cầu hỗ trợ liên quan đến đơn hàng của bạn.</p>
                </div>
                <a class="btn btn-secondary" href="<c:url value='/ho-tro/tao'/>">Gửi khiếu nại</a>
            </div>

            <c:if test="${sessionScope.role == 'STAFF'}">
                <div class="menu-card" style="border-left: 4px solid var(--xanh-duong);">
                    <div>
                        <h3>4. Quản lý CSKH</h3>
                        <p>Khu vực dành riêng cho Nhân viên CSKH tiếp nhận, xử lý và phản hồi khiếu nại.</p>
                    </div>
                    <a class="btn btn-primary" href="<c:url value='/cskh/yeu-cau'/>">Vào quản lý CSKH</a>
                </div>
            </c:if>
        </div>
    </div>
</main>
</body>
</html>
