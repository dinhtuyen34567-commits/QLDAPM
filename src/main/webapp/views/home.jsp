<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chính - Vận chuyển</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Hệ thống đơn hàng vận chuyển</h1>
        <span class="user-info">Khách hàng</span>
    </div>
</header>

<main class="container">
    <div class="card">
        <h2>Trang chính khách hàng</h2>
        <p>Bạn có thể tạo đơn hàng vận chuyển mới.</p>

        <div class="actions">
            <a class="btn btn-primary" href="<c:url value='/don-hang/tao'/>">Tạo đơn hàng mới</a>
        </div>
    </div>
</main>
</body>
</html>
