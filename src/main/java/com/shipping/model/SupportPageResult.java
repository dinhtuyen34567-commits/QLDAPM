/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportPageResult.java
 * Lần cuối cập nhật: 06/11/2026 09:15
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO đóng gói kết quả phân trang danh sách yêu cầu khiếu nại (danh sách dữ liệu, trang hiện tại, tổng số trang, tổng số bản ghi).
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DTO kết quả phân trang danh sách yêu cầu hỗ trợ (API 8).
 */
public class SupportPageResult {

    @SerializedName("page")
    private int page;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("total")
    private int total;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("items")
    private List<SupportListItemDTO> items;

    /**
     * Phương thức SupportPageResult: dùng để khởi tạo đối tượng SupportPageResult với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportPageResult() {
    }

    /**
     * Phương thức SupportPageResult: dùng để khởi tạo đối tượng SupportPageResult với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportPageResult(int page, int pageSize, int total, List<SupportListItemDTO> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.items = items;
    }

    /**
     * Phương thức getPage: dùng để lấy giá trị của thuộc tính Page, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getPage() {
        return page;
    }

    /**
     * Phương thức getPageSize: dùng để lấy giá trị của thuộc tính PageSize, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Phương thức getTotal: dùng để lấy giá trị của thuộc tính Total, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Phương thức getTotalPages: dùng để lấy giá trị của thuộc tính TotalPages, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Phương thức getItems: dùng để lấy giá trị của thuộc tính Items, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public List<SupportListItemDTO> getItems() {
        return items;
    }
}
