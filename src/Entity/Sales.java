package Entity;

import java.util.Date;

public class Sales {
    private int salesId;
    private Date saleDate;
    private int prdId;
    private int quantity;
    private int totalPrice;
    private String payment;
    private String cardNum;
    private int cash;
    private int cashChange;
    private String soldEmp;

    public Sales() {}

    public Sales(int salesId, Date saleDate, int prdId, int quantity, int totalPrice, String payment, String cardNum, int cash, int cashChange, String soldEmp) {
        this.salesId = salesId;
        this.saleDate = saleDate;
        this.prdId = prdId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.payment = payment;
        this.cardNum = cardNum;
        this.cash = cash;
        this.cashChange = cashChange;
        this.soldEmp = soldEmp;
    }

    // getter
    public int getSalesId() { return salesId; }
    public Date getSaleDate() { return saleDate; }
    public int getPrdId() { return prdId; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }
    public String getPayment() { return payment; }
    public String getCardNum() { return cardNum; }
    public int getCash() { return cash; }
    public int getCashChange() { return cashChange; }
    public String getSoldEmp() { return soldEmp; }

    // setter
    public void setSalesId(int salesId) { this.salesId = salesId; }
    public void setSaleDate(Date saleDate) { this.saleDate = saleDate; }
    public void setPrdId(int prdId) { this.prdId = prdId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
    public void setPayment(String payment) { this.payment = payment; }
    public void setCardNum(String cardNum) { this.cardNum = cardNum; }
    public void setCash(int cash) { this.cash = cash; }
    public void setCashChange(int cashChange) { this.cashChange = cashChange; }
    public void setSoldEmp(String soldEmp) { this.soldEmp = soldEmp; }
}
