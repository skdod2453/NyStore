package Entity;

import java.util.Date;

public class Works {
    private int workId;
    private String empId;
    private Date loginTime;
    private Date logoutTime;
    private int totalWork;
    private int dailyPay;

    public Works() {}

    public Works(int workId, String empId, Date loginTime, Date logoutTime, int totalWork, int dailyPay) {
        this.workId = workId;
        this.empId = empId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.totalWork = totalWork;
        this.dailyPay = dailyPay;
    }

    // getter
    public int getWorkId() { return workId; }
    public String getEmpId() { return empId; }
    public Date getLoginTime() { return loginTime; }
    public Date getLogoutTime() { return logoutTime; }
    public int getTotalWork() { return totalWork; }
    public int getDailyPay() { return dailyPay; }

    // setter
    public void setWorkId(int workId) { this.workId = workId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
    public void setLogoutTime(Date logoutTime) { this.logoutTime = logoutTime; }
    public void setTotalWork(int totalWork) { this.totalWork = totalWork; }
    public void setDailyPay(int dailyPay) { this.dailyPay = dailyPay; }
}
