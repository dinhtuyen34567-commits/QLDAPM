/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: complaint-create.js
 * Lần cuối cập nhật: 15/11/2026 09:30
 * 
 * Tổng quát chức năng của file:
 * Xử lý tương tác phía người dùng cho màn hình tạo khiếu nại: đếm ký tự thời gian thực, kiểm tra dữ liệu và gửi yêu cầu đến máy chủ.
 */
document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.APP_CONTEXT_PATH || '/';
    const form = document.getElementById('complaintForm');
    const inputMaVanDon = document.getElementById('maVanDon');
    const textareaNoiDung = document.getElementById('noiDung');
    const charCount = document.getElementById('charCount');
    const btnGui = document.getElementById('btnGuiYeuCau');
    const alertSuccess = document.getElementById('alertSuccess');
    const alertError = document.getElementById('alertError');
    const errMaVanDon = document.getElementById('errMaVanDon');
    const errNoiDung = document.getElementById('errNoiDung');

    // 1. Tự động điền mã vận đơn từ query param nếu có (?maVanDon=VN...)
    const urlParams = new URLSearchParams(window.location.search);
    const paramMaVanDon = urlParams.get('maVanDon');
    if (paramMaVanDon) {
        inputMaVanDon.value = paramMaVanDon.trim().toUpperCase();
    }

    // 2. Đếm ký tự realtime (tối đa 2.000 ký tự)
    textareaNoiDung.addEventListener('input', function () {
        const len = textareaNoiDung.value.length;
        charCount.textContent = len + ' / 2000 ký tự';
        if (len > 2000) {
            charCount.style.color = 'var(--do)';
        } else {
            charCount.style.color = 'var(--chu-phu)';
        }
    });

    // 3. Xử lý submit form
    form.addEventListener('submit', async function (e) {
        e.preventDefault();
        alertSuccess.style.display = 'none';
        alertError.style.display = 'none';
        errMaVanDon.textContent = '';
        errNoiDung.textContent = '';

        const maVanDon = inputMaVanDon.value.trim().toUpperCase();
        const noiDung = textareaNoiDung.value.trim();

        let hasError = false;
        if (!maVanDon) {
            errMaVanDon.textContent = 'Vui lòng nhập mã vận đơn';
            hasError = true;
        }
        if (!noiDung) {
            errNoiDung.textContent = 'Vui lòng nhập nội dung yêu cầu';
            hasError = true;
        } else if (noiDung.length > 2000) {
            errNoiDung.textContent = 'Nội dung yêu cầu tối đa 2.000 ký tự';
            hasError = true;
        }

        if (hasError) {
            return;
        }

        btnGui.disabled = true;
        btnGui.textContent = 'Đang gửi...';

        try {
            const resp = await fetch(contextPath + 'api/v1/yeu-cau', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ maVanDon, noiDung })
            });

            const data = await resp.json();

            if (!resp.ok) {
                alertError.textContent = data.message || 'Không thể tạo yêu cầu hỗ trợ';
                alertError.style.display = 'block';
                btnGui.disabled = false;
                btnGui.textContent = 'Gửi yêu cầu';
                return;
            }

            // Thành công -> hiển thị mã yêu cầu YC########
            alertSuccess.innerHTML = '<strong>Gửi yêu cầu thành công!</strong><br/>Mã yêu cầu hỗ trợ của bạn là: <strong>'
                + (data.maYeuCau || '') + '</strong>.<br/>Bộ phận CSKH sẽ tiếp nhận và phản hồi sớm nhất có thể.';
            alertSuccess.style.display = 'block';

            // Reset form
            form.reset();
            charCount.textContent = '0 / 2000 ký tự';
            btnGui.disabled = false;
            btnGui.textContent = 'Gửi yêu cầu';
        } catch (err) {
            alertError.textContent = 'Lỗi kết nối máy chủ. Vui lòng kiểm tra đường truyền!';
            alertError.style.display = 'block';
            btnGui.disabled = false;
            btnGui.textContent = 'Gửi yêu cầu';
        }
    });
});
