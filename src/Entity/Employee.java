package Entity;

import oracle.sql.TIMESTAMP;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;

public class Employee {
    private String empId;
    private String empPw;
    private String empName;
    private Timestamp loginDate;
    private String logoutDate;

    public Employee() {}

    public Employee(String empId, String empPw, String empName, Timestamp loginDate, String logoutDate) {
        this.empId = empId;
        this.empPw = empPw;
        this.empName = empName;
        this.loginDate = loginDate;
        this.logoutDate = logoutDate;
    }

    // getter 메서드
    public String getEmpId() { return empId; }
    public String getEmpPw() { return empPw; }
    public String getEmpName() { return empName; }
    public Timestamp getLoginDate() { return loginDate; }
    public String getLogoutDate() { return logoutDate; }

    // setter 메서드
    public void setEmpId(String empId) { this.empId = empId; }
    public void setEmpPw(String empPw) { this.empPw = empPw; }
    public void setEmpName(String empName) { this.empName = empName; }
    public void setLoginDate(Timestamp loginDate) { this.loginDate = loginDate; }
    public void setLogoutDate(String logoutDate) { this.logoutDate = logoutDate; }
}
