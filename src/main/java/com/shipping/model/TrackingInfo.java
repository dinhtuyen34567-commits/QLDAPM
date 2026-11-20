/**
 * Người code: Nguyễn Đình Tuyển (UC-02)
 * Tên file: TrackingInfo.java
 * Lần cuối cập nhật: 16/10/2026 09:15
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO đóng gói toàn bộ thông tin tra cứu vận đơn: trạng thái, hành trình, thông tin chuyến xe, tài xế và tọa độ GPS.
 */
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

    /**
     * Phương thức getTrackingCode: dùng để lấy giá trị của thuộc tính TrackingCode, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getTrackingCode() {
        return trackingCode;
    }

    /**
     * Phương thức setTrackingCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính TrackingCode, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    /**
     * Phương thức getStatus: dùng để lấy giá trị của thuộc tính Status, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Phương thức setStatus: dùng để gán hoặc cập nhật giá trị cho thuộc tính Status, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Phương thức getStatusLabel: dùng để lấy giá trị của thuộc tính StatusLabel, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getStatusLabel() {
        return statusLabel;
    }

    /**
     * Phương thức setStatusLabel: dùng để gán hoặc cập nhật giá trị cho thuộc tính StatusLabel, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * Phương thức getCreatedAt: dùng để lấy giá trị của thuộc tính CreatedAt, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Phương thức setCreatedAt: dùng để gán hoặc cập nhật giá trị cho thuộc tính CreatedAt, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Phương thức getReceiverName: dùng để lấy giá trị của thuộc tính ReceiverName, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * Phương thức setReceiverName: dùng để gán hoặc cập nhật giá trị cho thuộc tính ReceiverName, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * Phương thức getDeliveryAddress: dùng để lấy giá trị của thuộc tính DeliveryAddress, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Phương thức setDeliveryAddress: dùng để gán hoặc cập nhật giá trị cho thuộc tính DeliveryAddress, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Phương thức getTotalWeight: dùng để lấy giá trị của thuộc tính TotalWeight, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    /**
     * Phương thức setTotalWeight: dùng để gán hoặc cập nhật giá trị cho thuộc tính TotalWeight, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * Phương thức getProgressPercent: dùng để lấy giá trị của thuộc tính ProgressPercent, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getProgressPercent() {
        return progressPercent;
    }

    /**
     * Phương thức setProgressPercent: dùng để gán hoặc cập nhật giá trị cho thuộc tính ProgressPercent, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    /**
     * Phương thức getTrip: dùng để lấy giá trị của thuộc tính Trip, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Phương thức setTrip: dùng để gán hoặc cập nhật giá trị cho thuộc tính Trip, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    /**
     * Phương thức getRoute: dùng để lấy giá trị của thuộc tính Route, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Phương thức setRoute: dùng để gán hoặc cập nhật giá trị cho thuộc tính Route, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Phương thức getCurrentLocation: dùng để lấy giá trị của thuộc tính CurrentLocation, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Phương thức setCurrentLocation: dùng để gán hoặc cập nhật giá trị cho thuộc tính CurrentLocation, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Phương thức getLocations: dùng để lấy giá trị của thuộc tính Locations, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * Phương thức setLocations: dùng để gán hoặc cập nhật giá trị cho thuộc tính Locations, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
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

        /**
         * Phương thức getTripCode: dùng để lấy giá trị của thuộc tính TripCode, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getTripCode() {
            return tripCode;
        }

        /**
         * Phương thức setTripCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính TripCode, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setTripCode(String tripCode) {
            this.tripCode = tripCode;
        }

        /**
         * Phương thức getDepartureTime: dùng để lấy giá trị của thuộc tính DepartureTime, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getDepartureTime() {
            return departureTime;
        }

        /**
         * Phương thức setDepartureTime: dùng để gán hoặc cập nhật giá trị cho thuộc tính DepartureTime, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        /**
         * Phương thức getLicensePlate: dùng để lấy giá trị của thuộc tính LicensePlate, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getLicensePlate() {
            return licensePlate;
        }

        /**
         * Phương thức setLicensePlate: dùng để gán hoặc cập nhật giá trị cho thuộc tính LicensePlate, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }

        /**
         * Phương thức getDriverName: dùng để lấy giá trị của thuộc tính DriverName, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getDriverName() {
            return driverName;
        }

        /**
         * Phương thức setDriverName: dùng để gán hoặc cập nhật giá trị cho thuộc tính DriverName, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        /**
         * Phương thức getVehicleStatus: dùng để lấy giá trị của thuộc tính VehicleStatus, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getVehicleStatus() {
            return vehicleStatus;
        }

        /**
         * Phương thức setVehicleStatus: dùng để gán hoặc cập nhật giá trị cho thuộc tính VehicleStatus, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
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

        /**
         * Phương thức getRouteCode: dùng để lấy giá trị của thuộc tính RouteCode, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getRouteCode() {
            return routeCode;
        }

        /**
         * Phương thức setRouteCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính RouteCode, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setRouteCode(String routeCode) {
            this.routeCode = routeCode;
        }

        /**
         * Phương thức getDistanceKm: dùng để lấy giá trị của thuộc tính DistanceKm, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getDistanceKm() {
            return distanceKm;
        }

        /**
         * Phương thức setDistanceKm: dùng để gán hoặc cập nhật giá trị cho thuộc tính DistanceKm, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
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

        /**
         * Phương thức getLatitude: dùng để lấy giá trị của thuộc tính Latitude, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getLatitude() {
            return latitude;
        }

        /**
         * Phương thức setLatitude: dùng để gán hoặc cập nhật giá trị cho thuộc tính Latitude, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        /**
         * Phương thức getLongitude: dùng để lấy giá trị của thuộc tính Longitude, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getLongitude() {
            return longitude;
        }

        /**
         * Phương thức setLongitude: dùng để gán hoặc cập nhật giá trị cho thuộc tính Longitude, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }

        /**
         * Phương thức getRecordedAt: dùng để lấy giá trị của thuộc tính RecordedAt, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getRecordedAt() {
            return recordedAt;
        }

        /**
         * Phương thức setRecordedAt: dùng để gán hoặc cập nhật giá trị cho thuộc tính RecordedAt, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setRecordedAt(String recordedAt) {
            this.recordedAt = recordedAt;
        }
    }
}
