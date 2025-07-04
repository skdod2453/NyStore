package DAO;

import Entity.Employee;

public interface EmployeeDAO {
    Employee findByIdAndPassword(String empId, String empPw);
    void updateLoginTime(String empId);
}
