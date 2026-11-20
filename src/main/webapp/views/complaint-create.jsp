<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gửi khiếu nại / yêu cầu hỗ trợ</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Gửi khiếu nại & Yêu cầu hỗ trợ</h1>
        <div class="user-bar">
            <span class="user-info">
                Khách hàng: <c:out value="${sessionScope.userName}" default="Khách hàng"/>
            </span>
            <a href="<c:url value='/dang-xuat'/>" class="btn btn-secondary" style="padding: 4px 8px; font-size:12px;">Đăng xuất</a>
        </div>
    </div>
</header>

<main class="container">
    <div class="card" style="max-width: 650px; margin: 0 auto;">
        <h2>Thông tin khiếu nại đơn hàng</h2>
        <p>Quý khách vui lòng cung cấp mã vận đơn và nội dung cần khiếu nại hoặc hỗ trợ.</p>

        <div id="alertSuccess" class="alert alert-success" style="display:none;" role="alert"></div>
        <div id="alertError" class="alert alert-danger" style="display:none;" role="alert"></div>

        <form id="complaintForm">
            <div class="form-row">
                <label for="maVanDon">Mã vận đơn cần khiếu nại <span class="required">*</span></label>
                <div class="input-wrap" id="wrapMaVanDon">
                    <input type="text" id="maVanDon" name="maVanDon" maxlength="10" required
                           placeholder="VD: VN12345678" spellcheck="false" autocomplete="off">
                </div>
                <div class="error-text" id="errMaVanDon"></div>
            </div>

            <div class="form-row">
                <label for="noiDung">Nội dung khiếu nại / yêu cầu hỗ trợ <span class="required">*</span></label>
                <div class="input-wrap" id="wrapNoiDung">
                    <textarea id="noiDung" name="noiDung" rows="5" maxlength="2000" required
                              placeholder="Mô tả cụ thể sự cố đơn hàng (chậm giao, hàng hư hỏng, sai địa chỉ...)"
                              style="width: 100%; padding: 8px; border: 1px solid var(--xam-vien); border-radius: 4px; font-family: inherit; font-size: 14px; box-sizing: border-box;"></textarea>
                </div>
                <div style="display: flex; justify-content: space-between; margin-top: 4px;">
                    <div class="error-text" id="errNoiDung"></div>
                    <span id="charCount" class="hint-text">0 / 2000 ký tự</span>
                </div>
            </div>

            <div class="actions" style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary" id="btnGuiYeuCau">
                    Gửi yêu cầu
                </button>
                <a class="btn btn-secondary" href="<c:url value='/'/>">Về trang chính</a>
                <a class="btn btn-secondary" href="<c:url value='/tra-cuu'/>">Tra cứu vận đơn</a>
            </div>
        </form>
    </div>
</main>

<script>
    window.APP_CONTEXT_PATH = '<c:url value="/"/>';
</script>
<script src="<c:url value='/assets/js/complaint-create.js'/>"></script>
</body>
</html>
