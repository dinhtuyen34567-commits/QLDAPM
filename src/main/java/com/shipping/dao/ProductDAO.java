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
     * Lấy toàn bộ danh sách hàng hóa.
     *
     * <p>Tương ứng hàm {@code getDSHangHoa()} trong tài liệu thiết kế.</p>
     *
     * @return danh sách hàng hóa, không bao giờ null
     */
    List<Product> findAll();

    /**
     * Lấy nhiều hàng hóa theo danh sách id, dùng cho API tính cước và tạo đơn.
     *
     * @param ids danh sách id cần lấy
     * @return map từ id sang hàng hóa; hàng hóa không tồn tại thì không có trong map
     */
    Map<Integer, Product> findByIds(Collection<Integer> ids);
}
