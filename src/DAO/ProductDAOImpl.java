package DAO;

import Entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private  final Connection conn;

    public ProductDAOImpl(Connection conn) {this.conn = conn;}

    @Override
    public void insertProduct(Product product) {
        final String sql = "INSERT INTO product (PRDNAME, PRDCOMPANY, PRDPRICE, PRDADULT, PRDSTOCk, PRDEXP) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getPrdName());
            pstmt.setString(2, product.getPrdCompany());
            pstmt.setInt(3, product.getPrdPrice());
            pstmt.setString(4, String.valueOf(product.getPrdAdult()));
            pstmt.setInt(5, product.getPrdStock());
            if (product.getPrdExp() != null) {
                pstmt.setDate(6, new java.sql.Date(product.getPrdExp().getTime()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "제품 입력 중 오류 발생");
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        final String sql = "SELECT * FROM product ORDER BY PRDID";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setPrdId(rs.getInt("PRDID"));
                p.setPrdName(rs.getString("PRDNAME"));
                p.setPrdCompany(rs.getString("PRDCOMPANY"));
                p.setPrdPrice(rs.getInt("PRDPRICE"));
                p.setPrdAdult(rs.getString("PRDADULT").charAt(0));
                p.setPrdStock(rs.getInt("PRDSTOCK"));
                p.setPrdExp(rs.getDate("PRDEXP"));
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "상품 목록 조회 중 오류 발생");
        }
        return products;
    }

    @Override
    public Product getProductById(int prdId) {
        Product product = null;
        String sql = "SELECT * FROM PRODUCT WHERE prdid = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, prdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setPrdId(rs.getInt("prdid"));
                    product.setPrdName(rs.getString("prdname"));
                    product.setPrdCompany(rs.getString("prdcompany"));
                    product.setPrdPrice(rs.getInt("prdprice"));
                    product.setPrdAdult(rs.getString("prdadult").charAt(0));
                    product.setPrdStock(rs.getInt("prdstock"));
                    product.setPrdExp(rs.getDate("prdexp"));
                }
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "제품 상세 조회 중 오류 발생");
        }
        return product;
    }

    @Override
    public void updateProduct(int prdId, int newStock) {
        String sql = "UPDATE product SET PRDSTOCK = ? WHERE PRDID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, prdId);
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                System.out.println("재고 업데이트 실패 : 해당 상품 없음.");
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "재고 업데이트 중 오류 발생");
        }
    }

    @Override
    public void deleteProduct(int prdId) {
        String sql = "DELETE FROM product WHERE PRDID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, prdId);
            int deleted = pstmt.executeUpdate();
            if (deleted == 0) {
                System.out.println("제품 삭제 실패 : 해당 상품 없음.");
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "제품 삭제 중 오류 발생");
        }
    }
}
