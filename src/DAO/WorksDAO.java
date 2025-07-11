package DAO;

import Entity.Product;

import java.util.List;

public interface WorksDAO {
    void insertLoginRecord(String empId);
    void updateLogoutRecord(String empId);
}
