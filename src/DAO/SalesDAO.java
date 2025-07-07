package DAO;

import Entity.Sales;

import java.util.List;

public interface SalesDAO {
    void insertSale(Sales sales);
    List<Sales> getSalesByDate(String date);
}
