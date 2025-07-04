package DAO;

import Entity.Stock;

import java.util.List;

public interface StockDAO {
    void insertStock(Stock stock);
    List<Stock> getAllStockHistory();
}
