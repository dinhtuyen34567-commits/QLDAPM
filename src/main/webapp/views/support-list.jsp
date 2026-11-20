<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý CSKH - Danh sách yêu cầu hỗ trợ</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .filter-bar {
            display: flex;
            gap: 12px;
            align-items: center;
            margin-bottom: 16px;
            flex-wrap: wrap;
        }
        .filter-btn {
            padding: 6px 14px;
            border-radius: 16px;
            border: 1px solid var(--xam-vien);
            background: var(--trang);
            cursor: pointer;
            font-size: 13px;
        }
        .filter-btn.active {
            background: var(--xanh-duong);
            color: #fff;
            border-color: var(--xanh-duong);
        }
        .badge-moi { background: #e8f0fe; color: #1a73e8; padding: 3px 8px; border-radius: 4px; font-weight: 500; }
        .badge-dangxuly { background: #fef7e0; color: #b06000; padding: 3px 8px; border-radius: 4px; font-weight: 500; }
        .badge-dadong { background: #e6f4ea; color: #137333; padding: 3px 8px; border-radius: 4px; font-weight: 500; }
        .pagination-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 16px;
        }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Quản lý Yêu cầu & Khiếu nại (CSKH)</h1>
        <div class="user-bar">
            <span class="user-info">
                Nhân viên: <strong id="staffName"><c:out value="${sessionScope.userName}" default="Nhân viên CSKH"/></strong>
            </span>
            <a href="<c:url value='/dang-xuat'/>" class="btn btn-secondary" style="padding: 4px 8px; font-size:12px;">Đăng xuất</a>
        </div>
    </div>
</header>

<main class="container">
    <div class="card">
        <h2>Danh sách yêu cầu hỗ trợ / khiếu nại</h2>

        <div class="filter-bar">
            <span>Trạng thái:</span>
            <button type="button" class="filter-btn active" data-status="">Tất cả</button>
            <button type="button" class="filter-btn" data-status="MOI">Mới</button>
            <button type="button" class="filter-btn" data-status="DANG_XU_LY">Đang xử lý</button>
            <button type="button" class="filter-btn" data-status="DA_DONG">Đã đóng</button>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th style="width: 80px;">STT / ID</th>
                    <th style="width: 140px;">Mã yêu cầu</th>
                    <th style="width: 130px;">Trạng thái</th>
                    <th style="width: 140px;">Mã vận đơn</th>
                    <th>Khách hàng</th>
                    <th style="width: 120px; text-align: center;">Thao tác</th>
                </tr>
                </thead>
                <tbody id="supportListBody">
                <tr>
                    <td colspan="6" style="text-align: center; padding: 20px;">Đang tải danh sách yêu cầu...</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="pagination-bar">
            <span id="pageInfo" class="hint-text">Trang 1 / 1 (Tổng số: 0)</span>
            <div style="display: flex; gap: 8px;">
                <button type="button" class="btn btn-secondary" id="btnPrevPage" disabled>Trang trước</button>
                <button type="button" class="btn btn-secondary" id="btnNextPage" disabled>Trang sau</button>
            </div>
        </div>
    </div>
</main>

<script>
    window.APP_CONTEXT_PATH = '<c:url value="/"/>';
</script>
<script src="<c:url value='/assets/js/support-manage.js'/>"></script>
</body>
</html>
