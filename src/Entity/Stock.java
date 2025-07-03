package Entity;

import java.util.Date;

public class Stock {
    private int stockId;
    private int prdId;
    private Date inDate;
    private int quantity;
    private String updateEmp;   // 업데이트 실행한 사원 확인

    public Stock() {}

    public Stock(int stockId, int prdId, Date inDate, int quantity, String updateEmp) {
        this.stockId = stockId;
        this.prdId = prdId;
        this.inDate = inDate;
        this.quantity = quantity;
        this.updateEmp = updateEmp;
    }

    // getter
    public int getStockId() { return stockId; }
    public int getPrdId() { return prdId; }
    public Date getInDate() { return inDate; }
    public int getQuantity() { return quantity; }
    public String getUpdateEmp() { return updateEmp; }

    // setter
    public void setStockId(int stockId) { this.stockId = stockId; }
    public void setPrdId(int prdId) { this.prdId = prdId; }
    public void setInDate(Date inDate) { this.inDate = inDate; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUpdateEmp(String updateEmp) { this.updateEmp = updateEmp; }
}