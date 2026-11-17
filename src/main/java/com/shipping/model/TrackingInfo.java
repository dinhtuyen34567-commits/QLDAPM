package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Thông tin tra cứu vận đơn (UC-02).
 *
 * <p>Đây là DTO tổng hợp từ đơn hàng, chuyến xe, lộ trình và nhật ký tọa độ.
 * Các trường thời gian để dạng chuỗi đã định dạng sẵn để Gson trả JSON ổn định
 * trên Java 17, không cần bộ chuyển đổi LocalDateTime.</p>
 */
public class TrackingInfo {

    @SerializedName("maVanDon")
    private String trackingCode;

    @SerializedName("trangThai")
    private String status;

    @SerializedName("trangThaiHienThi")
    private String statusLabel;

    @SerializedName("ngayTao")
    private String createdAt;

    @SerializedName("tenNguoiNhan")
    private String receiverName;

    @SerializedName("diaChiGiao")
    private String deliveryAddress;

    @SerializedName("tongKhoiLuong")
    private BigDecimal totalWeight;

    /** Tiến độ 0-100 để vẽ thanh trạng thái trên giao diện. */
    @SerializedName("tienDo")
    private int progressPercent;

    @SerializedName("chuyenXe")
    private Trip trip;

    @SerializedName("loTrinh")
    private Route route;

    @SerializedName("viTriHienTai")
    private Location currentLocation;

    @SerializedName("nhatKyToaDo")
    private List<Location> locations = new ArrayList<>();

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations == null ? new ArrayList<>() : locations;
    }

    /** Thông tin chuyến xe gắn với đơn. */
    public static class Trip {

        @SerializedName("maChuyen")
        private String tripCode;

        @SerializedName("thoiGianDi")
        private String departureTime;

        @SerializedName("bienSo")
        private String licensePlate;

        @SerializedName("tenTaiXe")
        private String driverName;

        @SerializedName("trangThaiXe")
        private String vehicleStatus;

        public String getTripCode() {
            return tripCode;
        }

        public void setTripCode(String tripCode) {
            this.tripCode = tripCode;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getVehicleStatus() {
            return vehicleStatus;
        }

        public void setVehicleStatus(String vehicleStatus) {
            this.vehicleStatus = vehicleStatus;
        }
    }

    /** Lộ trình dự kiến của chuyến xe. */
    public static class Route {

        @SerializedName("maLoTrinh")
        private String routeCode;

        @SerializedName("khoangCach")
        private BigDecimal distanceKm;

        public String getRouteCode() {
            return routeCode;
        }

        public void setRouteCode(String routeCode) {
            this.routeCode = routeCode;
        }

        public BigDecimal getDistanceKm() {
            return distanceKm;
        }

        public void setDistanceKm(BigDecimal distanceKm) {
            this.distanceKm = distanceKm;
        }
    }

    /** Một điểm tọa độ trên hành trình. */
    public static class Location {

        @SerializedName("viDo")
        private BigDecimal latitude;

        @SerializedName("kinhDo")
        private BigDecimal longitude;

        @SerializedName("thoiGian")
        private String recordedAt;

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }

        public String getRecordedAt() {
            return recordedAt;
        }

        public void setRecordedAt(String recordedAt) {
            this.recordedAt = recordedAt;
        }
    }
}
