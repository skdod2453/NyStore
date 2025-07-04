package DAO;

import Entity.Product;
import java.util.List;

public interface ProductDAO {
    void insertProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(int prdId);
    void updateProduct(int prdId, int newStock);
    void deleteProduct(int prdId);
}
