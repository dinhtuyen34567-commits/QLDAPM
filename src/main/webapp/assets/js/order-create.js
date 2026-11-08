/* =====================================================================
 * QLDAPM-ShippingOrder - UC-01: Tao don hang van chuyen
 * JavaScript thuan, khong dung framework (CLAUDE.md muc 2).
 *
 * Phu trach:
 *   - Buoc 2: tai thong tin nguoi gui (GET /api/v1/khach-hang/me)
 *   - Buoc 4: kiem tra hop le tuc thoi, hien tick xanh
 *   - Buoc 6: nap danh sach hang hoa (GET /api/v1/hang-hoa)
 *   - Buoc 8: them mat hang, goi API tinh cuoc
 *   - Buoc 9-12: pop-up xac nhan, gui don, chuyen man hinh thanh cong
 *
 * Ngoai le xu ly: 3.1 (SDT sai), 7.1 (so luong), 9.1 (don rong),
 *                 11.1 (mat mang / timeout), 11.2 (huy bo)
 * ===================================================================== */
(function () {
    'use strict';

    // -----------------------------------------------------------------
    // Hang so
    // -----------------------------------------------------------------
    var MSG_SDT_INVALID = 'Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số';
    var MSG_QTY_INVALID = 'Số lượng phải lớn hơn 0';
    var MSG_EMPTY_ORDER = 'Đơn hàng phải có ít nhất 1 mặt hàng';
    var MSG_NETWORK = 'Kết nối máy chủ thất bại. Vui lòng kiểm tra đường truyền!';
    var MSG_REQUIRED = 'Thiếu tên người nhận hoặc địa chỉ giao hàng';

    var PHONE_REGEX = /^[0-9]{10,11}$/;
    var TIMEOUT_MS = window.REQUEST_TIMEOUT_MS || 10000;

    // -----------------------------------------------------------------
    // Trang thai
    // -----------------------------------------------------------------
    /** @type {Array<{productId:number, code:string, name:string, weight:number, quantity:number}>} */
    var items = [];

    /** Ket qua API tinh cuoc gan nhat, dung cho pop-up. */
    var lastQuote = null;

    /** Danh sach hang hoa da nap tu server. */
    var products = [];

    var els = {};

    // -----------------------------------------------------------------
    // Tien ich
    // -----------------------------------------------------------------

    /**
     * Dinh dang so tien theo chuan vi-VN kem dau " đ".
     * @param {number|string} value gia tri can dinh dang
     * @returns {string} chuoi da dinh dang
     */
    function formatMoney(value) {
        var n = Number(value);
        if (!isFinite(n)) {
            n = 0;
        }
        return n.toLocaleString('vi-VN') + ' đ';
    }

    /**
     * Dinh dang khoi luong toi 2 chu so thap phan.
     * @param {number|string} value gia tri can dinh dang
     * @returns {string} chuoi da dinh dang
     */
    function formatWeight(value) {
        var n = Number(value);
        if (!isFinite(n)) {
            n = 0;
        }
        return n.toLocaleString('vi-VN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }) + ' kg';
    }

    /**
     * Ghi thong bao loi cua server len dau man hinh.
     * @param {string} message noi dung loi
     */
    function showServerError(message) {
        els.serverError.textContent = message || '';
        els.serverError.style.display = message ? 'block' : 'none';
    }

    /**
     * Hien thi thong bao toast o goc phai man hinh (ngoai le 9.1).
     * @param {string} message noi dung thong bao
     */
    function showToast(message) {
        els.toast.textContent = message;
        els.toast.classList.add('show');
        window.clearTimeout(showToast._timer);
        showToast._timer = window.setTimeout(function () {
            els.toast.classList.remove('show');
        }, 5000);
    }

    /**
     * Chuyen doi cac the trong form thanh doi tuong don gian.
     * @returns {{tenNguoiNhan:string, sdtNguoiNhan:string, diaChiGiao:string, items:Array}}
     */
    function collectPayload() {
        return {
            tenNguoiNhan: els.tenNguoiNhan.value.trim(),
            sdtNguoiNhan: els.sdtNguoiNhan.value.trim(),
            diaChiGiao: els.diaChiGiao.value.trim(),
            items: items.map(function (it) {
                return {hangHoaId: it.productId, soLuong: it.quantity};
            })
        };
    }

    /**
     * Goi API co timeout 10 giay bang AbortController (ngoai le 11.1).
     * @param {string} url dia chi API
     * @param {object} options tuy chon fetch
     * @returns {Promise<object>} du lieu JSON tra ve
     */
    function fetchJson(url, options) {
        var controller = new AbortController();
        var timeoutId = window.setTimeout(function () {
            controller.abort();
        }, TIMEOUT_MS);

        options = options || {};
        options.signal = controller.signal;
        options.headers = options.headers || {};
        if (options.body) {
            options.headers['Content-Type'] = 'application/json';
        }

        return fetch(url, options)
            .then(function (response) {
                window.clearTimeout(timeoutId);
                return response.json()
                    .catch(function () {
                        return {};
                    })
                    .then(function (data) {
                        if (!response.ok) {
                            var err = new Error(data.message || 'Lỗi không xác định');
                            err.status = response.status;
                            err.code = data.code;
                            throw err;
                        }
                        return data;
                    });
            })
            .catch(function (error) {
                window.clearTimeout(timeoutId);
                if (error.name === 'AbortError') {
                    var timeoutErr = new Error(MSG_NETWORK);
                    timeoutErr.isNetwork = true;
                    throw timeoutErr;
                }
                if (!error.status) {
                    error.isNetwork = true;
                    error.message = MSG_NETWORK;
                }
                throw error;
            });
    }

    // -----------------------------------------------------------------
    // Kiem tra hop le tuc thoi (buoc 4)
    // -----------------------------------------------------------------

    /**
     * Cap nhat trang thai hien thi cua mot o nhap lieu.
     * @param {HTMLElement} wrap the bao quanh o nhap
     * @param {HTMLElement} errEl phan tu hien thi loi
     * @param {boolean} valid o co hop le hay khong
     * @param {string} message noi dung loi (neu co)
     */
    function setFieldState(wrap, errEl, valid, message) {
        wrap.classList.remove('valid', 'invalid');
        errEl.classList.remove('show');
        errEl.textContent = '';

        if (valid) {
            wrap.classList.add('valid');
        } else if (message) {
            wrap.classList.add('invalid');
            errEl.textContent = message;
            errEl.classList.add('show');
        }
    }

    /**
     * Kiem tra va cap nhat truong nguoi nhan.
     */
    function validateReceiver() {
        var name = els.tenNguoiNhan.value.trim();
        var phone = els.sdtNguoiNhan.value.trim();
        var address = els.diaChiGiao.value.trim();

        setFieldState(els.wrapTenNguoiNhan, els.errTenNguoiNhan,
            name.length > 0 && name.length <= 100,
            name.length === 0 ? MSG_REQUIRED : '');

        // Ngoai le 3.1: SĐT sai -> vien do + chu do ben duoi o
        setFieldState(els.wrapSdtNguoiNhan, els.errSdtNguoiNhan,
            PHONE_REGEX.test(phone),
            phone.length === 0 ? MSG_REQUIRED : MSG_SDT_INVALID);

        setFieldState(els.wrapDiaChiGiao, els.errDiaChiGiao,
            address.length > 0 && address.length <= 255,
            address.length === 0 ? MSG_REQUIRED : '');
    }

    /**
     * Kiem tra nguoi nhan hop le chua.
     * @returns {boolean} true neu ca ba o deu hop le
     */
    function isReceiverValid() {
        var name = els.tenNguoiNhan.value.trim();
        var phone = els.sdtNguoiNhan.value.trim();
        var address = els.diaChiGiao.value.trim();

        return name.length > 0 && name.length <= 100
            && PHONE_REGEX.test(phone)
            && address.length > 0 && address.length <= 255;
    }

    /**
     * Cap nhat trang thai nut [Luu don hang] theo BR-05.
     * Nut chi bat khi ca 3 o nguoi nhan hop le VA co it nhat 1 mat hang.
     */
    function updateSaveButton() {
        var enabled = isReceiverValid() && items.length > 0;
        els.btnLuuDonHang.disabled = !enabled;
        els.btnLuuDonHang.classList.toggle('disabled', !enabled);
    }

    // -----------------------------------------------------------------
    // Tai du lieu (buoc 2 va buoc 6)
    // -----------------------------------------------------------------

    /**
     * Tai thong tin nguoi gui va dien vao hai o khoa (buoc 2).
     */
    function loadSenderInfo() {
        fetchJson(window.APP_CONTEXT_PATH + 'api/v1/khach-hang/me')
            .then(function (data) {
                els.tenNguoiGui.value = data.hoTen || '';
                els.sdtNguoiGui.value = data.sdt || '';
                els.tenNguoiGui.setAttribute('value', data.hoTen || '');
                els.sdtNguoiGui.setAttribute('value', data.sdt || '');
            })
            .catch(function (error) {
                showServerError(error.message || MSG_NETWORK);
            });
    }

    /**
     * Nap danh sach hang hoa vao dropdown (buoc 6).
     */
    function loadProducts() {
        fetchJson(window.APP_CONTEXT_PATH + 'api/v1/hang-hoa')
            .then(function (data) {
                products = Array.isArray(data) ? data : [];
                els.productSelect.innerHTML = '';

                var placeholder = document.createElement('option');
                placeholder.value = '';
                placeholder.textContent = '-- Chọn hàng hóa --';
                els.productSelect.appendChild(placeholder);

                products.forEach(function (p) {
                    var opt = document.createElement('option');
                    opt.value = String(p.id);
                    opt.textContent = p.maHang + ' - ' + p.tenHang
                        + ' (' + formatWeight(p.khoiLuong) + ')';
                    // Luu thuoc tinh de dung khi tinh cuoc
                    opt.setAttribute('data-weight', p.khoiLuong);
                    els.productSelect.appendChild(opt);
                });
            })
            .catch(function (error) {
                showServerError(error.message || MSG_NETWORK);
            });
    }

    // -----------------------------------------------------------------
    // Bang chi tiet (buoc 7 va buoc 8)
    // -----------------------------------------------------------------

    /**
     * Ve lai bang chi tiet don hang.
     */
    function renderTable() {
        var tbody = els.detailBody;
        tbody.innerHTML = '';

        if (items.length === 0) {
            var emptyRow = document.createElement('tr');
            emptyRow.className = 'empty-row';
            var td = document.createElement('td');
            td.colSpan = 6;
            td.textContent = 'Chưa có mặt hàng nào';
            emptyRow.appendChild(td);
            tbody.appendChild(emptyRow);
        } else {
            items.forEach(function (it) {
                var tr = document.createElement('tr');

                var tdCode = document.createElement('td');
                tdCode.textContent = it.code;
                tr.appendChild(tdCode);

                var tdName = document.createElement('td');
                tdName.textContent = it.name;
                tr.appendChild(tdName);

                var tdWeight = document.createElement('td');
                tdWeight.textContent = formatWeight(it.weight);
                tr.appendChild(tdWeight);

                var tdQty = document.createElement('td');
                tdQty.textContent = String(it.quantity);
                tr.appendChild(tdQty);

                var tdUnit = document.createElement('td');
                tdUnit.textContent = formatMoney(it.unitPrice);
                tr.appendChild(tdUnit);

                var tdTotal = document.createElement('td');
                tdTotal.textContent = formatMoney(it.unitPrice * it.quantity);
                tr.appendChild(tdTotal);

                tbody.appendChild(tr);
            });
        }
    }

    /**
     * Them mot mat hang vao danh sach. Cung hang hoa thi cong don (BR-09).
     */
    function addItem() {
        var productId = parseInt(els.productSelect.value, 10);
        var rawQty = els.quantityInput.value.trim();

        // Ngoai le 7.1: so luong phai la so nguyen > 0
        var quantity = Number(rawQty);
        var isValid = rawQty !== '' && /^[0-9]+$/.test(rawQty) && quantity > 0;

        if (!productId) {
            els.alertSoLuong.textContent = 'Vui lòng chọn hàng hóa trước khi thêm.';
            els.alertSoLuong.style.display = 'block';
            return;
        }

        if (!isValid) {
            els.alertSoLuong.textContent = MSG_QTY_INVALID;
            els.alertSoLuong.style.display = 'block';
            els.quantityInput.focus();
            return;
        }

        els.alertSoLuong.style.display = 'none';

        var selected = els.productSelect.options[els.productSelect.selectedIndex];
        var product = products.find(function (p) {
            return p.id === productId;
        });
        var weight = product ? Number(product.khoiLuong) : 0;
        var code = product ? product.maHang : '';
        var name = product ? product.tenHang : (selected ? selected.textContent : '');

        var existing = items.find(function (it) {
            return it.productId === productId;
        });

        if (existing) {
            // BR-09: cong don so luong vao dong da co
            existing.quantity += quantity;
        } else {
            items.push({
                productId: productId,
                code: code,
                name: name,
                weight: weight,
                quantity: quantity,
                unitPrice: 0
            });
        }

        els.quantityInput.value = '';
        renderTable();
        updateSaveButton();

        // Buoc 8: goi API tinh cuoc de hien thi tong khoi luong
        refreshQuote();
    }

    /**
     * Goi API tinh cuoc va cap nhat tong khoi luong.
     */
    function refreshQuote() {
        if (items.length === 0) {
            lastQuote = null;
            els.tongKhoiLuong.textContent = formatWeight(0);
            return;
        }

        fetchJson(window.APP_CONTEXT_PATH + 'api/v1/don-hang/tinh-cuoc', {
            method: 'POST',
            body: JSON.stringify({items: collectPayload().items})
        })
            .then(function (quote) {
                lastQuote = quote;
                els.tongKhoiLuong.textContent = formatWeight(quote.tongKhoiLuong);

                // Cap nhat don gia hien thi trong bang
                items.forEach(function (it) {
                    var line = quote.lines ? quote.lines.find(function (l) {
                        return l.hangHoaId === it.productId;
                    }) : null;
                    if (line && line.donGia !== undefined) {
                        it.unitPrice = line.donGia;
                    }
                });
                renderTable();
            })
            .catch(function (error) {
                showServerError(error.message || MSG_NETWORK);
            });
    }

    // -----------------------------------------------------------------
    // Pop-up xac nhan (buoc 10)
    // -----------------------------------------------------------------

    /**
     * Mo pop-up xac nhan va hien thi tom tat cuoc phi.
     */
    function openConfirmPopup() {
        // Handler cung tu kiem tra items rong: khong dua vao thuoc tinh disabled
        // vi nguoi dung co the go khoa bang DevTools (ngoai le 9.1).
        if (items.length === 0) {
            showToast(MSG_EMPTY_ORDER);
            return;
        }

        if (!isReceiverValid()) {
            validateReceiver();
            showToast(MSG_REQUIRED);
            return;
        }

        if (!lastQuote) {
            // Chua co ket qua tinh cuoc: yeu cau server tinh lai truoc khi mo pop-up
            refreshQuote();
            showToast('Vui lòng chờ hệ thống tính cước.');
            return;
        }

        els.popupKhoiLuong.textContent = formatWeight(lastQuote.tongKhoiLuong);
        els.popupCuoc.textContent = formatMoney(lastQuote.tongCuoc);
        els.popupVat.textContent = formatMoney(lastQuote.tienVat);
        els.popupVatPercent.textContent = String(lastQuote.vat);
        els.popupTongThanhToan.textContent = formatMoney(lastQuote.tongThanhToan);

        els.confirmBody.style.display = 'block';
        els.confirmLoading.style.display = 'none';
        els.confirmError.style.display = 'none';
        els.confirmActions.style.display = 'flex';

        els.confirmOverlay.classList.add('show');
    }

    /**
     * Dong pop-up xac nhan va xoa lop phu mo.
     */
    function closeConfirmPopup() {
        els.confirmOverlay.classList.remove('show');
    }

    /**
     * Chuyen pop-up sang trang thai dang gui (icon xoay).
     */
    function showLoadingState() {
        els.confirmBody.style.display = 'none';
        els.confirmError.style.display = 'none';
        els.confirmActions.style.display = 'none';
        els.confirmLoading.style.display = 'block';
    }

    /**
     * Chuyen pop-up sang trang thai loi (ngoai le 11.1).
     * @param {string} message noi dung loi
     */
    function showErrorState(message) {
        els.confirmLoading.style.display = 'none';
        els.confirmBody.style.display = 'none';
        els.confirmActions.style.display = 'none';
        els.confirmError.style.display = 'block';
        els.confirmErrorText.textContent = message;
    }

    // -----------------------------------------------------------------
    // Gui don hang (buoc 11 va buoc 12)
    // -----------------------------------------------------------------

    /**
     * Gui don hang len server.
     */
    function submitOrder() {
        showLoadingState();

        var payload = collectPayload();

        fetchJson(window.APP_CONTEXT_PATH + 'api/v1/don-hang', {
            method: 'POST',
            headers: {
                'Idempotency-Key': window.IDEMPOTENCY_KEY
            },
            body: JSON.stringify(payload)
        })
            .then(function (result) {
                closeConfirmPopup();
                // Buoc 12: chuyen man hinh thanh cong, hien thi ma van don
                window.location.href = window.APP_CONTEXT_PATH
                    + 'don-hang/thanh-cong?maVanDon='
                    + encodeURIComponent(result.maVanDon);
            })
            .catch(function (error) {
                if (error.isNetwork || error.status >= 500) {
                    // Ngoai le 11.1: pop-up loi, giu nguyen toan bo du lieu da nhap
                    showErrorState(MSG_NETWORK);
                    return;
                }
                // Loi 4xx: hien thi thong bao cua server theo dung noi loi
                showErrorState(error.message || MSG_NETWORK);
                showServerError(error.message || '');
            });
    }

    // -----------------------------------------------------------------
    // Khoi tao
    // -----------------------------------------------------------------

    /**
     * Lay cac phan tu can thiet va gan su kien.
     */
    function init() {
        els = {
            tenNguoiGui: document.getElementById('tenNguoiGui'),
            sdtNguoiGui: document.getElementById('sdtNguoiGui'),
            tenNguoiNhan: document.getElementById('tenNguoiNhan'),
            sdtNguoiNhan: document.getElementById('sdtNguoiNhan'),
            diaChiGiao: document.getElementById('diaChiGiao'),
            wrapTenNguoiNhan: document.getElementById('wrapTenNguoiNhan'),
            wrapSdtNguoiNhan: document.getElementById('wrapSdtNguoiNhan'),
            wrapDiaChiGiao: document.getElementById('wrapDiaChiGiao'),
            errTenNguoiNhan: document.getElementById('errTenNguoiNhan'),
            errSdtNguoiNhan: document.getElementById('errSdtNguoiNhan'),
            errDiaChiGiao: document.getElementById('errDiaChiGiao'),
            productSelect: document.getElementById('productSelect'),
            quantityInput: document.getElementById('quantityInput'),
            btnThemMatHang: document.getElementById('btnThemMatHang'),
            btnLuuDonHang: document.getElementById('btnLuuDonHang'),
            alertSoLuong: document.getElementById('alertSoLuong'),
            detailBody: document.getElementById('detailBody'),
            tongKhoiLuong: document.getElementById('tongKhoiLuong'),
            confirmOverlay: document.getElementById('confirmOverlay'),
            confirmBody: document.getElementById('confirmBody'),
            confirmLoading: document.getElementById('confirmLoading'),
            confirmError: document.getElementById('confirmError'),
            confirmErrorText: document.getElementById('confirmErrorText'),
            confirmActions: document.getElementById('confirmActions'),
            popupKhoiLuong: document.getElementById('popupKhoiLuong'),
            popupCuoc: document.getElementById('popupCuoc'),
            popupVat: document.getElementById('popupVat'),
            popupVatPercent: document.getElementById('popupVatPercent'),
            popupTongThanhToan: document.getElementById('popupTongThanhToan'),
            btnXacNhan: document.getElementById('btnXacNhan'),
            btnHuyBo: document.getElementById('btnHuyBo'),
            btnDongLoi: document.getElementById('btnDongLoi'),
            toast: document.getElementById('toast'),
            serverError: document.getElementById('serverError')
        };

        // Kiem tra tuc thoi tren su kien input va blur
        [els.tenNguoiNhan, els.sdtNguoiNhan, els.diaChiGiao].forEach(function (input) {
            input.addEventListener('input', function () {
                validateReceiver();
                updateSaveButton();
            });
            input.addEventListener('blur', function () {
                validateReceiver();
                updateSaveButton();
            });
        });

        els.btnThemMatHang.addEventListener('click', addItem);
        els.btnLuuDonHang.addEventListener('click', openConfirmPopup);
        els.btnXacNhan.addEventListener('click', submitOrder);

        // Ngoai le 11.2: huy bo -> dong pop-up, giu nguyen du lieu, khong ghi CSDL
        els.btnHuyBo.addEventListener('click', closeConfirmPopup);

        // Ngoai le 11.1: bam Dong -> dong pop-up loi, giu nguyen du lieu da nhap
        els.btnDongLoi.addEventListener('click', closeConfirmPopup);

        renderTable();
        updateSaveButton();
        loadSenderInfo();
        loadProducts();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
