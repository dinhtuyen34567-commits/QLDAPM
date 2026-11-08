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

    public Product() {
        // Dung cho Jackson/Gson va test
    }

    public Product(int id, String code, String name, String category,
                   BigDecimal weight, BigDecimal freightRate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.category = category;
        this.weight = weight;
        this.freightRate = freightRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /** @return khối lượng (kg) của một đơn vị hàng. */
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /** @return đơn giá cước (đ/kg). */
    public BigDecimal getFreightRate() {
        return freightRate;
    }

    public void setFreightRate(BigDecimal freightRate) {
        this.freightRate = freightRate;
    }
}
