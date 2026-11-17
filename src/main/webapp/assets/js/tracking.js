/* =====================================================================
 * QLDAPM-ShippingOrder - UC-02: Tra cuu trang thai van don
 *
 * Phu trach:
 *   - Buoc 4: kiem tra dinh dang ma van don tuc thoi
 *   - Buoc 5-6: goi API, hien thi trang thai, tien trinh, chuyen xe,
 *               lo trinh, toa do hien tai va nhat ky toa do
 *   - Ngoai le 3.1: ma sai dinh dang
 *   - Ngoai le 5.1: khong tim thay van don
 *   - Ngoai le 5.2: mat mang / timeout
 * ===================================================================== */
(function () {
    'use strict';

    var MSG_INVALID = 'Mã vận đơn không hợp lệ, vui lòng kiểm tra lại';
    var MSG_NOT_FOUND = 'Không tìm thấy thông tin cho mã vận đơn này';
    var MSG_NETWORK = 'Lỗi kết nối máy chủ, vui lòng thử lại sau';

    var TRACKING_REGEX = /^VN[0-9]{8}$/;
    var TIMEOUT_MS = window.REQUEST_TIMEOUT_MS || 5000;

    var els = {};

    function setText(id, value) {
        var el = document.getElementById(id);
        if (el) {
            el.textContent = value === null || value === undefined || value === ''
                ? '--'
                : String(value);
        }
    }

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

    function formatDistance(value) {
        var n = Number(value);
        if (!isFinite(n)) {
            return '--';
        }
        return n.toLocaleString('vi-VN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }) + ' km';
    }

    function normalizeCode() {
        return els.maVanDon.value.trim().toUpperCase();
    }

    function isValidCode(code) {
        return TRACKING_REGEX.test(code);
    }

    function setFieldState(valid, message) {
        els.wrapMaVanDon.classList.remove('valid', 'invalid');
        els.errMaVanDon.classList.remove('show');
        els.errMaVanDon.textContent = '';

        if (valid) {
            els.wrapMaVanDon.classList.add('valid');
        } else if (message) {
            els.wrapMaVanDon.classList.add('invalid');
            els.errMaVanDon.textContent = message;
            els.errMaVanDon.classList.add('show');
        }
    }

    function validate() {
        var code = normalizeCode();
        if (code.length === 0) {
            setFieldState(false, '');
            els.btnTimKiem.disabled = true;
            return false;
        }
        var valid = isValidCode(code);
        setFieldState(valid, valid ? '' : MSG_INVALID);
        els.btnTimKiem.disabled = !valid;
        return valid;
    }

    function hideStates() {
        els.loading.style.display = 'none';
        els.notFound.style.display = 'none';
        els.result.style.display = 'none';
    }

    function showNotFound() {
        hideStates();
        els.notFound.style.display = 'block';
    }

    function showErrorModal(message) {
        els.errorMessage.textContent = message;
        els.errorOverlay.classList.add('show');
    }

    function closeErrorModal() {
        els.errorOverlay.classList.remove('show');
    }

    /**
     * Goi API co timeout bang AbortController (ngoai le 5.2).
     */
    function fetchJson(url) {
        var controller = new AbortController();
        var timeoutId = window.setTimeout(function () {
            controller.abort();
        }, TIMEOUT_MS);

        return fetch(url, {
            method: 'GET',
            headers: {'Accept': 'application/json'},
            signal: controller.signal
        })
            .then(function (response) {
                window.clearTimeout(timeoutId);
                return response.json()
                    .catch(function () {
                        return {};
                    })
                    .then(function (data) {
                        if (!response.ok) {
                            var err = new Error(data.message || MSG_NETWORK);
                            err.status = response.status;
                            err.code = data.code;
                            throw err;
                        }
                        return data;
                    });
            })
            .catch(function (error) {
                window.clearTimeout(timeoutId);
                if (error.name === 'AbortError' || !error.status) {
                    var networkError = new Error(MSG_NETWORK);
                    networkError.isNetwork = true;
                    throw networkError;
                }
                throw error;
            });
    }

    function render(data) {
        hideStates();
        els.result.style.display = 'block';

        var status = data.trangThai || '';
        var statusLabel = data.trangThaiHienThi || status || '--';
        var progress = Number(data.tienDo);
        if (!isFinite(progress)) {
            progress = 0;
        }
        progress = Math.max(0, Math.min(100, progress));

        setText('resultMaVanDon', data.maVanDon);
        setText('infoNgayTao', data.ngayTao);
        setText('infoNguoiNhan', data.tenNguoiNhan);
        setText('infoDiaChi', data.diaChiGiao);
        setText('infoKhoiLuong', formatWeight(data.tongKhoiLuong));

        els.statusBadge.className = 'status-badge' + (status ? ' status-' + status : '');
        els.statusBadge.textContent = statusLabel;

        els.progressBar.style.width = progress + '%';
        els.progressBar.classList.toggle('cancelled', status === 'DA_HUY');
        els.progressWrap.setAttribute('aria-valuenow', String(progress));
        els.progressText.textContent = statusLabel + ' (' + progress + '%)';

        renderTrip(data);
        renderLocations(data);
    }

    function renderTrip(data) {
        var trip = data.chuyenXe;
        if (!trip) {
            els.tripCard.style.display = 'none';
            return;
        }
        els.tripCard.style.display = 'block';
        setText('tripMaChuyen', trip.maChuyen);
        setText('tripThoiGianDi', trip.thoiGianDi);
        setText('tripBienSo', trip.bienSo);
        setText('tripTaiXe', trip.tenTaiXe);
        setText('tripTrangThaiXe', trip.trangThaiXe);

        if (data.loTrinh) {
            setText('routeMa', data.loTrinh.maLoTrinh);
            setText('routeKhoangCach', formatDistance(data.loTrinh.khoangCach));
        } else {
            setText('routeMa', null);
            setText('routeKhoangCach', null);
        }
    }

    function renderLocations(data) {
        var locations = Array.isArray(data.nhatKyToaDo) ? data.nhatKyToaDo : [];
        var tbody = els.locationBody;
        tbody.innerHTML = '';

        if (locations.length === 0) {
            var emptyRow = document.createElement('tr');
            emptyRow.className = 'empty-row';
            var td = document.createElement('td');
            td.colSpan = 3;
            td.textContent = 'Chưa có dữ liệu tọa độ';
            emptyRow.appendChild(td);
            tbody.appendChild(emptyRow);
            els.mapEmpty.classList.remove('hidden');
            els.routePolyline.setAttribute('points', '');
            els.currentPoint.setAttribute('cx', '0');
            els.currentPoint.setAttribute('cy', '0');
            setText('currentLocation', 'Chưa có dữ liệu');
            setText('currentTime', '--');
            return;
        }

        els.mapEmpty.classList.add('hidden');
        setText('currentLocation',
            locations[0].viDo + ', ' + locations[0].kinhDo);
        setText('currentTime', locations[0].thoiGian);

        locations.forEach(function (location) {
            var tr = document.createElement('tr');
            var tdTime = document.createElement('td');
            tdTime.textContent = location.thoiGian || '--';
            tr.appendChild(tdTime);

            var tdLat = document.createElement('td');
            tdLat.textContent = location.viDo;
            tr.appendChild(tdLat);

            var tdLng = document.createElement('td');
            tdLng.textContent = location.kinhDo;
            tr.appendChild(tdLng);

            tbody.appendChild(tr);
        });

        drawRoute(locations);
    }

    /**
     * Ve polyline don gian tu danh sach toa do, khong phu thuoc dich vu ban do.
     */
    function drawRoute(locations) {
        var points = locations.map(function (location) {
            return {
                lat: Number(location.viDo),
                lng: Number(location.kinhDo)
            };
        }).filter(function (point) {
            return isFinite(point.lat) && isFinite(point.lng);
        });

        if (points.length === 0) {
            els.routePolyline.setAttribute('points', '');
            return;
        }

        var width = 600;
        var height = 240;
        var pad = 24;
        var minLat = points[0].lat;
        var maxLat = points[0].lat;
        var minLng = points[0].lng;
        var maxLng = points[0].lng;

        points.forEach(function (point) {
            minLat = Math.min(minLat, point.lat);
            maxLat = Math.max(maxLat, point.lat);
            minLng = Math.min(minLng, point.lng);
            maxLng = Math.max(maxLng, point.lng);
        });

        function x(lng) {
            if (maxLng === minLng) {
                return width / 2;
            }
            return pad + (lng - minLng) / (maxLng - minLng) * (width - 2 * pad);
        }

        function y(lat) {
            if (maxLat === minLat) {
                return height / 2;
            }
            // Vi do cang lon cang ve phia bac -> truc y nguoc.
            return height - pad - (lat - minLat) / (maxLat - minLat) * (height - 2 * pad);
        }

        var svgPoints = points.map(function (point) {
            return x(point.lng).toFixed(1) + ',' + y(point.lat).toFixed(1);
        }).join(' ');

        els.routePolyline.setAttribute('points', svgPoints);
        els.currentPoint.setAttribute('cx', x(points[0].lng).toFixed(1));
        els.currentPoint.setAttribute('cy', y(points[0].lat).toFixed(1));
    }

    function search() {
        var code = normalizeCode();
        els.maVanDon.value = code;

        if (!validate()) {
            return;
        }

        hideStates();
        els.loading.style.display = 'block';
        els.btnTimKiem.disabled = true;

        var url = window.APP_CONTEXT_PATH + 'api/v1/van-don/tra-cuu?maVanDon='
            + encodeURIComponent(code);

        fetchJson(url)
            .then(function (data) {
                render(data);
            })
            .catch(function (error) {
                if (error.status === 404) {
                    showNotFound();
                } else if (error.status === 400) {
                    setFieldState(false, error.message || MSG_INVALID);
                } else if (error.isNetwork) {
                    showErrorModal(MSG_NETWORK);
                } else {
                    showErrorModal(error.message || MSG_NETWORK);
                }
            })
            .then(function () {
                els.loading.style.display = 'none';
                els.btnTimKiem.disabled = !isValidCode(normalizeCode());
            });
    }

    function init() {
        els.maVanDon = document.getElementById('maVanDon');
        els.wrapMaVanDon = document.getElementById('wrapMaVanDon');
        els.errMaVanDon = document.getElementById('errMaVanDon');
        els.btnTimKiem = document.getElementById('btnTimKiem');
        els.loading = document.getElementById('loading');
        els.notFound = document.getElementById('notFound');
        els.result = document.getElementById('result');
        els.statusBadge = document.getElementById('statusBadge');
        els.progressWrap = document.getElementById('progressWrap');
        els.progressBar = document.getElementById('progressBar');
        els.progressText = document.getElementById('progressText');
        els.tripCard = document.getElementById('tripCard');
        els.locationBody = document.getElementById('locationBody');
        els.routePolyline = document.getElementById('routePolyline');
        els.currentPoint = document.getElementById('currentPoint');
        els.mapEmpty = document.getElementById('mapEmpty');
        els.errorOverlay = document.getElementById('errorOverlay');
        els.errorMessage = document.getElementById('errorMessage');
        els.btnDongLoi = document.getElementById('btnDongLoi');

        els.maVanDon.addEventListener('input', function () {
            var code = normalizeCode();
            if (els.maVanDon.value !== code) {
                els.maVanDon.value = code;
            }
            validate();
        });
        els.maVanDon.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                search();
            }
        });
        els.btnTimKiem.addEventListener('click', search);
        els.btnDongLoi.addEventListener('click', closeErrorModal);

        var params = new URLSearchParams(window.location.search);
        var initialCode = params.get('maVanDon');
        if (initialCode) {
            els.maVanDon.value = initialCode.trim().toUpperCase();
            validate();
            if (isValidCode(normalizeCode())) {
                search();
            }
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
