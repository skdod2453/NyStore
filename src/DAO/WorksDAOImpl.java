package DAO;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class WorksDAOImpl implements WorksDAO {
    private final Connection conn;

    public WorksDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertLoginRecord(String empId) {
        // 퇴근 안 한 가장 최근 출근 기록 조회
        String checkSql = "SELECT workid, logintime FROM works WHERE empid = ? AND logouttime IS NULL ORDER BY workid DESC FETCH FIRST 1 ROWS ONLY";
        String autoLogoutSql = "UPDATE works SET logouttime = ?, totalwork = ?, dailypay = ? WHERE workid = ?";
        String insertSql = "INSERT INTO works (EMPID, LOGINTIME) VALUES (?, SYSTIMESTAMP)";

        try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
            checkPstmt.setString(1, empId);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                // 이미 퇴근 안 한 기록이 있으면 자동 퇴근 처리
                int workId = rs.getInt("workid");
                Timestamp loginTime = rs.getTimestamp("logintime");
                Timestamp logoutTime = Timestamp.valueOf(LocalDateTime.now().withNano(0));

                long minutesWorked = Duration.between(
                        loginTime.toLocalDateTime(),
                        logoutTime.toLocalDateTime()
                ).toMinutes();

                int totalWork = (int) minutesWorked;
                int dailyPay = (int) (minutesWorked * (11000.0 / 60));

                try (PreparedStatement autoLogoutPstmt = conn.prepareStatement(autoLogoutSql)) {
                    autoLogoutPstmt.setTimestamp(1, logoutTime);
                    autoLogoutPstmt.setInt(2, totalWork);
                    autoLogoutPstmt.setInt(3, dailyPay);
                    autoLogoutPstmt.setInt(4, workId);
                    autoLogoutPstmt.executeUpdate();
                }
            }

            // 새 출근 기록 추가
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                insertPstmt.setString(1, empId);
                insertPstmt.executeUpdate();
                System.out.println("✅ 새로운 출근 기록이 추가되었습니다.");
            }

        } catch (SQLException e) {
            System.out.printf("❗ 출근 기록 처리 중 오류 발생: %s\r\n", e.getMessage());
        }
    }


    @Override
    public void updateLogoutRecord(String empId) {
        String selectSql = "SELECT workid, logintime FROM works WHERE empid = ? AND logouttime IS NULL ORDER BY workid DESC FETCH FIRST 1 ROWS ONLY";
        String updateSql = "UPDATE works SET logouttime = ?, totalwork = ?, dailypay = ? WHERE workid = ?";
        String nameSql = "SELECT empname FROM employee WHERE empid = ?";

        try (
                PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
                PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
                PreparedStatement namePstmt = conn.prepareStatement(nameSql)
        ) {
            selectPstmt.setString(1, empId);
            ResultSet rs = selectPstmt.executeQuery();

            if (rs.next()) {
                int workId = rs.getInt("workid");

                // ✅ 여기에 이 부분을 넣으세요
                Timestamp loginTime = rs.getTimestamp("logintime");
                Timestamp logoutTime = Timestamp.valueOf(LocalDateTime.now().withNano(0));  // 밀리초 제거

                long minutesWorked = Duration.between(
                        loginTime.toLocalDateTime(),
                        logoutTime.toLocalDateTime()
                ).toMinutes();

                int totalWork = (int) minutesWorked;
                int dailyPay = (int) (minutesWorked * (11000.0 / 60));

                // 로그 확인용
                System.out.println("🕒 출근 시간: " + loginTime.toLocalDateTime());
                System.out.println("🕔 퇴근 시간: " + logoutTime.toLocalDateTime());
                System.out.println("🧮 근무 시간: " + totalWork + "분 / 일당: " + dailyPay + "원");

                updatePstmt.setTimestamp(1, logoutTime);
                updatePstmt.setInt(2, totalWork);
                updatePstmt.setInt(3, dailyPay);
                updatePstmt.setInt(4, workId);
                updatePstmt.executeUpdate();

                // 이름 가져오기
                namePstmt.setString(1, empId);
                ResultSet nameRs = namePstmt.executeQuery();
                String empName = empId;
                if (nameRs.next()) {
                    empName = nameRs.getString("empname");
                }

                // 출력
                System.out.printf("👋 %s 퇴근이 완료되었습니다! \r\n", empName);
                System.out.println("총 근무 시간: " + totalWork + "분");
                System.out.println("오늘의 일당: " + dailyPay + "원\n");

            } else {
                System.out.println("❗ 출근 기록이 없습니다. 퇴근 처리 불가.");
            }
        } catch (SQLException e) {
            System.out.println("❗ 퇴근 기록 중 오류 발생: " + e.getMessage());
        }
    }
}
