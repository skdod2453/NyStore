package DAO;

import Entity.Sales;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesDAOImpl implements SalesDAO {
    private final Connection conn;

    public SalesDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertSale(Sales sale) {
        String sql = "INSERT INTO SALES (PRDID, QUANTITY, TOTALPRICE, PAYMENT, CARDNUM, CASH, CASHCHANGE, SOLDEMP) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sale.getPrdId());
            pstmt.setInt(2, sale.getQuantity());
            pstmt.setInt(3, sale.getTotalPrice());
            pstmt.setString(4, sale.getPayment());
            if (sale.getCardNum() != null && !sale.getCardNum().isBlank()) {
                pstmt.setString(5, sale.getCardNum());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }
            if (sale.getCash() > 0) {
                pstmt.setInt(6, sale.getCash());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            if (sale.getCashChange() > 0) {
                pstmt.setInt(7, sale.getCashChange());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            pstmt.setString(8, sale.getSoldEmp());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("매출 등록 중 오류 발생: " + e.getMessage());
        }
    }

    @Override
    public List<Sales> getSalesByDate(String date) {
        List<Sales> salesList = new ArrayList<>();
        String sql = "SELECT * FROM SALES WHERE TO_CHAR(SALEDATE, 'YYYY-MM-DD') = ? ORDER BY SALEDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Sales s = new Sales();
                    s.setSalesId(rs.getInt("SALESID"));
                    s.setSaleDate(rs.getDate("SALEDATE"));
                    s.setPrdId(rs.getInt("PRDID"));
                    s.setQuantity(rs.getInt("QUANTITY"));
                    s.setTotalPrice(rs.getInt("TOTALPRICE"));
                    s.setPayment(rs.getString("PAYMENT"));
                    s.setCardNum(rs.getString("CARDNUM"));
                    s.setCash(rs.getInt("CASH"));
                    s.setCashChange(rs.getInt("CASHCHANGE"));
                    s.setSoldEmp(rs.getString("SOLDEMP"));
                    salesList.add(s);
                }
            }
        } catch (SQLException e) {
            System.out.println("매출 조회 중 오류 발생: " + e.getMessage());
        }
        return salesList;
    }
}
