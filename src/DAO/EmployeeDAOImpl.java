package DAO;

import Entity.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAOImpl implements EmployeeDAO {
    private final Connection conn;

    public EmployeeDAOImpl(Connection conn) {this.conn = conn;}

    @Override
    public Employee findByIdAndPassword(String empId, String empPw) {
        final String sql =  "SELECT * FROM employee WHERE EMPID = ? AND EMPPW = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            pstmt.setString(2, empPw);
            pstmt.execute();
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmpId(rs.getString("EMPID"));
                    emp.setEmpPw(rs.getString("EMPPW"));
                    emp.setEmpName(rs.getString("EMPNAME"));
                    emp.setLoginDate(rs.getTimestamp("LOGINDATE"));
                    emp.setLogoutDate(rs.getString("LOGOUTDATE"));
                    return emp;
                }
            }
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "로그인 중 오류가 발생하였습니다.");
        }
        return null;
    }

    @Override
    public void updateLoginTime(String empId) {
        final String sql = "UPDATE EMPLOYEE SET LOGINDATE = SYSTIMESTAMP WHERE EMPID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "로그인 시간 업데이트 중 오류가 발생하였습니다.");
        }
    }
}
