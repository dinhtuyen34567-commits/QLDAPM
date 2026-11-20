/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: support-manage.js
 * Lần cuối cập nhật: 15/11/2026 15:00
 * 
 * Tổng quát chức năng của file:
 * Xử lý tương tác phía người dùng cho màn hình quản lý CSKH: lọc danh sách theo trạng thái, phân trang và cập nhật trạng thái khiếu nại.
 */
document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.APP_CONTEXT_PATH || '/';
    const isDetailPage = document.getElementById('detailMaYeuCau') !== null;

    if (isDetailPage) {
        initDetailPage(contextPath);
    } else {
        initListPage(contextPath);
    }
});

/**
 * Hàm initListPage: dùng để khởi tạo sự kiện và quản lý logic cho màn hình danh sách yêu cầu khiếu nại CSKH, phục vụ UC-03.
 *
 * Điều khiển màn hình danh sách yêu cầu (GDDanhSachYeuCau)
 */
function initListPage(contextPath) {
    let currentStatus = '';
    let currentPage = 1;
    let totalPages = 1;

    const tbody = document.getElementById('supportListBody');
    const pageInfo = document.getElementById('pageInfo');
    const btnPrev = document.getElementById('btnPrevPage');
    const btnNext = document.getElementById('btnNextPage');
    const filterBtns = document.querySelectorAll('.filter-btn');

    /**
     * Hàm loadData: dùng để gọi API lấy danh sách yêu cầu khiếu nại theo bộ lọc trạng thái và trang hiện tại, phục vụ hiển thị dữ liệu bảng.
     */
    async function loadData() {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:20px;">Đang tải danh sách yêu cầu...</td></tr>';

        try {
            let url = contextPath + 'api/v1/yeu-cau?trang=' + currentPage;
            if (currentStatus) {
                url += '&trangThai=' + encodeURIComponent(currentStatus);
            }

            const resp = await fetch(url);
            if (!resp.ok) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:red; padding:20px;">Không thể tải danh sách yêu cầu.</td></tr>';
                return;
            }

            const data = await resp.json();
            totalPages = Math.max(1, data.totalPages || 1);
            pageInfo.textContent = 'Trang ' + data.page + ' / ' + totalPages + ' (Tổng số: ' + data.total + ')';

            btnPrev.disabled = (currentPage <= 1);
            btnNext.disabled = (currentPage >= totalPages);

            if (!data.items || data.items.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:20px; color:#666;">Chưa có yêu cầu hỗ trợ nào.</td></tr>';
                return;
            }

            let html = '';
            data.items.forEach(function (item, idx) {
                const rowNum = (currentPage - 1) * (data.pageSize || 20) + idx + 1;
                let badgeClass = 'badge-moi';
                let badgeText = 'Mới';
                if (item.trangThai === 'DANG_XU_LY') {
                    badgeClass = 'badge-dangxuly';
                    badgeText = 'Đang xử lý';
                } else if (item.trangThai === 'DA_DONG') {
                    badgeClass = 'badge-dadong';
                    badgeText = 'Đã đóng';
                }

                html += '<tr>'
                    + '<td>' + rowNum + '</td>'
                    + '<td><strong>' + escapeHtml(item.maYeuCau) + '</strong></td>'
                    + '<td><span class="' + badgeClass + '">' + badgeText + '</span></td>'
                    + '<td><code>' + escapeHtml(item.maVanDon) + '</code></td>'
                    + '<td>' + escapeHtml(item.hoTenKhachHang) + '</td>'
                    + '<td style="text-align:center;">'
                    + '<a class="btn btn-primary" style="padding:4px 8px; font-size:12px;" href="'
                    + contextPath + 'cskh/yeu-cau/chi-tiet?id=' + item.id + '">Xem chi tiết</a>'
                    + '</td>'
                    + '</tr>';
            });
            tbody.innerHTML = html;
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:red; padding:20px;">Lỗi kết nối máy chủ.</td></tr>';
        }
    }

    filterBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            filterBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentStatus = btn.getAttribute('data-status') || '';
            currentPage = 1;
            loadData();
        });
    });

    btnPrev.addEventListener('click', function () {
        if (currentPage > 1) {
            currentPage--;
            loadData();
        }
    });

    btnNext.addEventListener('click', function () {
        if (currentPage < totalPages) {
            currentPage++;
            loadData();
        }
    });

    loadData();
}

/**
 * Hàm initDetailPage: dùng để khởi tạo sự kiện và quản lý logic cho màn hình chi tiết và xử lý khiếu nại CSKH, phục vụ UC-03.
 *
 * Điều khiển màn hình chi tiết xử lý (GDXuLyYeuCau)
 */
function initDetailPage(contextPath) {
    const urlParams = new URLSearchParams(window.location.search);
    const requestId = urlParams.get('id');

    const alertBox = document.getElementById('alertBox');
    const btnTiepNhan = document.getElementById('btnTiepNhan');
    const btnDongYeuCau = document.getElementById('btnDongYeuCau');
    const textareaPhanHoi = document.getElementById('phanHoi');

    if (!requestId) {
        alertBox.className = 'alert alert-danger';
        alertBox.textContent = 'Không tìm thấy ID yêu cầu cần xử lý.';
        alertBox.style.display = 'block';
        return;
    }

    /**
     * Hàm loadDetail: dùng để gọi API lấy toàn bộ thông tin chi tiết của một yêu cầu khiếu nại kèm đơn hàng, phục vụ nạp dữ liệu chi tiết.
     */
    async function loadDetail() {
        try {
            const resp = await fetch(contextPath + 'api/v1/yeu-cau/' + requestId);
            if (!resp.ok) {
                const err = await resp.json();
                alertBox.className = 'alert alert-danger';
                alertBox.textContent = err.message || 'Không thể lấy thông tin chi tiết yêu cầu.';
                alertBox.style.display = 'block';
                return;
            }

            const d = await resp.json();
            renderDetail(d);
        } catch (err) {
            alertBox.className = 'alert alert-danger';
            alertBox.textContent = 'Lỗi kết nối máy chủ khi lấy dữ liệu.';
            alertBox.style.display = 'block';
        }
    }

    /**
     * Hàm renderDetail: dùng để kết xuất thông tin khiếu nại, khách hàng và chi tiết đơn hàng lên giao diện xử lý, phục vụ nhân viên xem xét.
     */
    function renderDetail(d) {
        document.getElementById('detailMaYeuCau').textContent = d.maYeuCau || '';
        document.getElementById('detailNoiDung').textContent = d.noiDung || '';
        document.getElementById('detailNhanVien').textContent = d.tenNhanVien || 'Chưa có người nhận';

        // Badge trạng thái
        const stSpan = document.getElementById('detailTrangThai');
        if (d.trangThai === 'MOI') {
            stSpan.className = 'badge-moi';
            stSpan.textContent = 'Mới';
        } else if (d.trangThai === 'DANG_XU_LY') {
            stSpan.className = 'badge-dangxuly';
            stSpan.textContent = 'Đang xử lý';
        } else if (d.trangThai === 'DA_DONG') {
            stSpan.className = 'badge-dadong';
            stSpan.textContent = 'Đã đóng';
        }

        // Khách hàng
        if (d.khachHang) {
            document.getElementById('detailKhachTen').textContent = d.khachHang.hoTen || '';
            document.getElementById('detailKhachSdt').textContent = d.khachHang.sdt || '';
            document.getElementById('detailKhachEmail').textContent = d.khachHang.email || 'Không có';
        }

        // Đơn hàng
        if (d.donHang) {
            document.getElementById('detailMaVanDon').textContent = d.donHang.maVanDon || '';
            document.getElementById('detailTrangThaiDon').textContent = d.donHang.trangThai || '';
            document.getElementById('detailNguoiNhan').textContent = d.donHang.tenNguoiNhan || '';
            document.getElementById('detailSdtNhan').textContent = d.donHang.sdtNguoiNhan || '';
            document.getElementById('detailDiaChi').textContent = d.donHang.diaChiGiao || '';
            document.getElementById('detailKhoiLuong').textContent = (d.donHang.tongKhoiLuong || '0') + ' kg';

            const itemsBody = document.getElementById('detailItemsBody');
            if (d.donHang.items && d.donHang.items.length > 0) {
                let rows = '';
                d.donHang.items.forEach(function (it) {
                    rows += '<tr>'
                        + '<td>' + escapeHtml(it.tenHang) + '</td>'
                        + '<td style="text-align:center;">' + it.soLuong + '</td>'
                        + '<td style="text-align:right;">' + Number(it.donGia).toLocaleString('vi-VN') + ' đ</td>'
                        + '</tr>';
                });
                itemsBody.innerHTML = rows;
            }
        }

        // Phản hồi
        if (d.phanHoi) {
            textareaPhanHoi.value = d.phanHoi;
        }

        // Nếu đã đóng -> khóa toàn bộ thao tác
        if (d.trangThai === 'DA_DONG') {
            textareaPhanHoi.disabled = true;
            btnTiepNhan.style.display = 'none';
            btnDongYeuCau.style.display = 'none';
            alertBox.className = 'alert alert-success';
            alertBox.textContent = 'Yêu cầu này đã được đóng hoàn tất và không thể chỉnh sửa.';
            alertBox.style.display = 'block';
        }
    }

    /**
     * Hàm sendUpdate: dùng để gửi yêu cầu cập nhật trạng thái (tiếp nhận hoặc đóng yêu cầu) kèm nội dung phản hồi lên máy chủ, phục vụ xử lý khiếu nại.
     */
    async function sendUpdate(targetStatus) {
        alertBox.style.display = 'none';
        const reply = textareaPhanHoi.value.trim();

        if (targetStatus === 'DA_DONG' && !reply) {
            alertBox.className = 'alert alert-danger';
            alertBox.textContent = 'Vui lòng nhập nội dung phản hồi trước khi đóng yêu cầu.';
            alertBox.style.display = 'block';
            textareaPhanHoi.focus();
            return;
        }

        btnTiepNhan.disabled = true;
        btnDongYeuCau.disabled = true;

        try {
            const resp = await fetch(contextPath + 'api/v1/yeu-cau/' + requestId, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    trangThai: targetStatus,
                    phanHoi: reply
                })
            });

            const data = await resp.json();

            btnTiepNhan.disabled = false;
            btnDongYeuCau.disabled = false;

            if (!resp.ok) {
                alertBox.className = 'alert alert-danger';
                alertBox.textContent = data.message || 'Không thể cập nhật yêu cầu.';
                alertBox.style.display = 'block';
                return;
            }

            alertBox.className = 'alert alert-success';
            alertBox.textContent = (targetStatus === 'DA_DONG') ? 'Đã đóng yêu cầu thành công!' : 'Đã tiếp nhận và cập nhật phản hồi thành công!';
            alertBox.style.display = 'block';

            renderDetail(data);
        } catch (err) {
            btnTiepNhan.disabled = false;
            btnDongYeuCau.disabled = false;
            alertBox.className = 'alert alert-danger';
            alertBox.textContent = 'Lỗi kết nối máy chủ.';
            alertBox.style.display = 'block';
        }
    }

    btnTiepNhan.addEventListener('click', function () {
        sendUpdate('DANG_XU_LY');
    });

    btnDongYeuCau.addEventListener('click', function () {
        if (confirm('Bạn có chắc chắn muốn đóng yêu cầu hỗ trợ này?')) {
            sendUpdate('DA_DONG');
        }
    });

    loadDetail();
}

/**
 * Hàm escapeHtml: dùng để mã hóa ký tự HTML chống lỗ hổng XSS khi hiển thị dữ liệu người dùng, phục vụ an toàn bảo mật.
 */
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
