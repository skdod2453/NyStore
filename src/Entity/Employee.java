package Entity;

import java.util.Date;

public class Employee {
    private String empId;
    private String empPw;
    private String empName;
    private Date loginDate;
    private String logoutDate;

    public Employee() {}

    public Employee(String empId, String empPw, String empName, Date loginDate, String logoutDate) {
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
    public Date getLoginDate() { return loginDate; }
    public String getLogoutDate() { return logoutDate; }

    // setter 메서드
    public void setEmpId(String empId) { this.empId = empId; }
    public void setEmpPw(String empPw) { this.empPw = empPw; }
    public void setEmpName(String empName) { this.empName = empName; }
    public void setLoginDate(Date loginDate) { this.loginDate = loginDate; }
    public void setLogoutDate(String logoutDate) { this.logoutDate = logoutDate; }
}
