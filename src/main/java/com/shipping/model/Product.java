/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: Product.java
 * Lần cuối cập nhật: 01/10/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể đại diện cho thông tin mặt hàng / sản phẩm được chọn gửi trong danh mục của hệ thống vận chuyển.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Hàng hóa ({@code tblHangHoa}).
 *
 * <p>Trường {@code freightRate} là đơn giá cước trên 1 kg, dùng để tính đơn giá
 * của một đơn vị hàng theo công thức {@code khoiLuong × donGiaCuoc} (mục 5.4).
 * Trường này <b>không</b> được trả về cho client qua API danh sách hàng hóa.</p>
 */
public class Product {

    @SerializedName("id")
    private int id;

    @SerializedName("maHang")
    private String code;

    @SerializedName("tenHang")
    private String name;

    @SerializedName("loaiHang")
    private String category;

    @SerializedName("khoiLuong")
    private BigDecimal weight;

    @SerializedName("donGiaCuoc")
    private BigDecimal freightRate;

    /**
     * Phương thức Product: dùng để khởi tạo đối tượng Product với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Product() {
        // Dung cho Jackson/Gson va test
    }

    /**
     * Phương thức Product: dùng để khởi tạo đối tượng Product với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Product(int id, String code, String name, String category,
                   BigDecimal weight, BigDecimal freightRate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.weight = weight;
        this.freightRate = freightRate;
    }

    /**
     * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getId() {
        return id;
    }

    /**
     * Phương thức setId: dùng để gán hoặc cập nhật giá trị cho thuộc tính Id, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Phương thức getCode: dùng để lấy giá trị của thuộc tính Code, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getCode() {
        return code;
    }

    /**
     * Phương thức setCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính Code, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Phương thức getName: dùng để lấy giá trị của thuộc tính Name, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getName() {
        return name;
    }

    /**
     * Phương thức setName: dùng để gán hoặc cập nhật giá trị cho thuộc tính Name, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Phương thức getCategory: dùng để lấy giá trị của thuộc tính Category, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Phương thức setCategory: dùng để gán hoặc cập nhật giá trị cho thuộc tính Category, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Phương thức getWeight: dùng để lấy giá trị của thuộc tính Weight, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return khối lượng (kg) của một đơn vị hàng.
     */
    public BigDecimal getWeight() {
        return weight;
    }

    /**
     * Phương thức setWeight: dùng để gán hoặc cập nhật giá trị cho thuộc tính Weight, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * Phương thức getFreightRate: dùng để lấy giá trị của thuộc tính FreightRate, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return đơn giá cước (đ/kg).
     */
    public BigDecimal getFreightRate() {
        return freightRate;
    }

    /**
     * Phương thức setFreightRate: dùng để gán hoặc cập nhật giá trị cho thuộc tính FreightRate, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setFreightRate(BigDecimal freightRate) {
        this.freightRate = freightRate;
    }
}
