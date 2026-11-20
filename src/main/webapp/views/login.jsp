<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập hệ thống</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .login-card {
            max-width: 420px;
            margin: 40px auto;
        }
        .demo-accounts {
            margin-top: 20px;
            padding: 12px;
            background: #f8f9fa;
            border-radius: 6px;
            font-size: 13px;
        }
        .demo-accounts table {
            width: 100%;
            margin-top: 6px;
            border-collapse: collapse;
        }
        .demo-accounts td {
            padding: 4px 6px;
            border: 1px solid #ddd;
        }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <h1>Hệ thống Quản lý Logistics Thông minh</h1>
        <span class="user-info">Xác thực người dùng</span>
    </div>
</header>

<main class="container">
    <div class="card login-card">
        <h2>Đăng nhập</h2>
        <div id="loginError" class="alert alert-danger" style="display:none;" role="alert"></div>

        <form id="loginForm">
            <div class="form-row">
                <label for="username">Tên đăng nhập <span class="required">*</span></label>
                <div class="input-wrap">
                    <input type="text" id="username" name="username" required autocomplete="username"
                           placeholder="Nhập tên đăng nhập" spellcheck="false">
                </div>
            </div>

            <div class="form-row">
                <label for="password">Mật khẩu <span class="required">*</span></label>
                <div class="input-wrap">
                    <input type="password" id="password" name="password" required autocomplete="current-password"
                           placeholder="Nhập mật khẩu" style="padding-right: 40px;">
                    <button type="button" id="btnTogglePassword" class="password-toggle" title="Hiện/Ẩn mật khẩu" aria-label="Hiện/Ẩn mật khẩu">
                        👁
                    </button>
                </div>
            </div>

            <div class="actions" style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary" id="btnLogin" style="width:100%;">
                    Đăng nhập
                </button>
            </div>
        </form>

        <div class="demo-accounts">
            <strong>Tài khoản mẫu thử nghiệm (Mật khẩu: Dev@12345):</strong>
            <table>
                <tr>
                    <th>Vai trò</th>
                    <th>Tên đăng nhập</th>
                    <th>Họ tên</th>
                </tr>
                <tr>
                    <td>Khách hàng</td>
                    <td><code>khachhang1</code></td>
                    <td>Nguyễn Văn A</td>
                </tr>
                <tr>
                    <td>Nhân viên CSKH 1</td>
                    <td><code>cskh1</code></td>
                    <td>Nguyễn Thị CSKH</td>
                </tr>
                <tr>
                    <td>Nhân viên CSKH 2</td>
                    <td><code>cskh2</code></td>
                    <td>Trần Văn CSKH</td>
                </tr>
            </table>
        </div>
    </div>
</main>

<script>
    const loginForm = document.getElementById('loginForm');
    const errBox = document.getElementById('loginError');
    const btnLogin = document.getElementById('btnLogin');
    const passInput = document.getElementById('password');
    const btnToggle = document.getElementById('btnTogglePassword');

    // Toggle hien / an mat khau
    if (btnToggle) {
        btnToggle.addEventListener('click', function () {
            if (passInput.type === 'password') {
                passInput.type = 'text';
                btnToggle.textContent = '🔒';
                btnToggle.title = 'Ẩn mật khẩu';
            } else {
                passInput.type = 'password';
                btnToggle.textContent = '👁';
                btnToggle.title = 'Hiện mật khẩu';
            }
        });
    }

    // Click tai khoan mau de dien nhanh
    document.querySelectorAll('.demo-accounts table tr').forEach(function (row, idx) {
        if (idx === 0) return;
        row.style.cursor = 'pointer';
        row.title = 'Nhấp để tự động điền';
        row.addEventListener('click', function () {
            const codeEl = row.querySelector('code');
            if (codeEl) {
                document.getElementById('username').value = codeEl.textContent.trim();
                passInput.value = 'Dev@12345';
                passInput.focus();
            }
        });
    });

    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault();
        errBox.style.display = 'none';
        errBox.textContent = '';
        btnLogin.disabled = true;
        btnLogin.textContent = 'Đang xác thực...';

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        try {
            const resp = await fetch('<c:url value="/api/v1/auth/login"/>', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await resp.json();
            if (!resp.ok) {
                errBox.textContent = data.message || 'Đăng nhập không thành công';
                errBox.style.display = 'block';
                btnLogin.disabled = false;
                btnLogin.textContent = 'Đăng nhập';
                return;
            }

            // Đăng nhập thành công -> chuyển hướng
            window.location.href = data.redirectUrl || '<c:url value="/"/>';
        } catch (err) {
            errBox.textContent = 'Lỗi kết nối máy chủ. Vui lòng kiểm tra lại!';
            errBox.style.display = 'block';
            btnLogin.disabled = false;
            btnLogin.textContent = 'Đăng nhập';
        }
    });
</script>
</body>
</html>
