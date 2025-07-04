package DAO;

import Entity.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAOImpl implements StockDAO {
    private final Connection conn;

    public StockDAOImpl(Connection conn) {this.conn = conn;}

    @Override
    public void insertStock(Stock stock) {
        String sql = "INSERT INTO stock (PRDID, INDATE, QUANTITY, UPDATEEMP) VALUES (?, SYSDATE, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stock.getPrdId());
            pstmt.setInt(2, stock.getQuantity());
            pstmt.setString(3, stock.getUpdateEmp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "입고 기록 추가 중 오류");
        }
    }

    @Override
    public List<Stock> getAllStockHistory() {
        List<Stock> stockList = new ArrayList<>();
        String sql = "SELECT * FROM c##nystore stock ORDER BY INDATE DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setStockId(rs.getInt("STOCKID"));
                stock.setPrdId(rs.getInt("PRDID"));
                stock.setInDate(rs.getTimestamp("INDATE"));
                stock.setQuantity(rs.getInt("QUANTITY"));
                stock.setUpdateEmp(rs.getString("UPDATEEMP"));
                stockList.add(stock);
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "입고 내역 조회 중 오류");
        }
        return stockList;
    }
}
