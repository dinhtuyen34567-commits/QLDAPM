/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ProductDAO.java
 * Lần cuối cập nhật: 06/10/2026 09:15
 * 
 * Tổng quát chức năng của lớp:
 * Giao diện khai báo các phương thức truy xuất dữ liệu danh mục hàng hóa trong CSDL phục vụ tạo đơn hàng.
 */
package com.shipping.dao;

import com.shipping.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Truy cập dữ liệu bảng {@code tblHangHoa}.
 *
 * <p>DAO chỉ dùng {@code PreparedStatement}, không chứa logic nghiệp vụ.</p>
 */
public interface ProductDAO {

    /**
     * Phương thức findAll: dùng để truy vấn toàn bộ danh sách bản ghi có trong CSDL, phục vụ hiển thị danh mục cho người dùng.
     *
     * Lấy toàn bộ danh sách hàng hóa.
     *
     * <p>Tương ứng hàm {@code getDSHangHoa()} trong tài liệu thiết kế.</p>
     *
     * @return danh sách hàng hóa, không bao giờ null
     */
    List<Product> findAll();

    /**
     * Phương thức findByIds: dùng để truy vấn danh sách mặt hàng dựa trên danh sách các mã định danh ID, phục vụ tính toán khối lượng và cước phí đơn hàng.
     *
     * Lấy nhiều hàng hóa theo danh sách id, dùng cho API tính cước và tạo đơn.
     *
     * @param ids danh sách id cần lấy
     * @return map từ id sang hàng hóa; hàng hóa không tồn tại thì không có trong map
     */
    Map<Integer, Product> findByIds(Collection<Integer> ids);
}
