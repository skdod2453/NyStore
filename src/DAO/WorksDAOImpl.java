package DAO;

import java.sql.*;
import java.time.Duration;

public class WorksDAOImpl implements WorksDAO {
    private final Connection conn;

    public WorksDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertLoginRecord(String empId) {
        String sql = "INSERT INTO works (EMPID, LOGINTIME) VALUES (?, SYSDATE)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "출근 기록 중 오류 발생");
        }
    }

    @Override
    public void updateLogoutRecord(String empId) {
        String selectSql = "SELECT WORKID, LOGINTIME FROM works WHERE EMPID = ? AND LOGOUTTIME IS NULL ORDER BY WORKID DESC FETCH FIRST 1 ROWS ONLY";
        String updateSql = "UPDATE works SET LOGOUTTIME = SYSDATE, TOTALWORK = ?, DAILYPAY = ? WHERE WORKID = ?";

        try (
                PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
                PreparedStatement updatePstmt = conn.prepareStatement(updateSql)
        ) {
            selectPstmt.setString(1, empId);
            ResultSet rs = selectPstmt.executeQuery();

            if (rs.next()) {
                int workId = rs.getInt("workid");
                Timestamp loginTime = rs.getTimestamp("logintime");
                Timestamp logoutTime = new Timestamp(System.currentTimeMillis());

                long minutesWorked = Duration.between(loginTime.toLocalDateTime(), logoutTime.toLocalDateTime()).toMinutes();
                int totalWork = (int) minutesWorked;
                int dailyPay = (int) (minutesWorked * (11000.0 / 60));

                updatePstmt.setTimestamp(1, logoutTime);
                updatePstmt.setInt(2, totalWork);
                updatePstmt.setInt(3, dailyPay);
                updatePstmt.setInt(4, workId);
                updatePstmt.executeUpdate();
            } else {
                System.out.println("출근 기록이 없습니다.");
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "퇴근 기록 중 오류 발생");
        }
    }
}