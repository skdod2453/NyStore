package Entity;

import java.util.Date;

public class Product {
    private int prdId;
    private String prdName;
    private String prdCompany;
    private int prdPrice;
    private char prdAdult;  // 19금 물품인지 확인
    private int prdStock;   // 재고
    private Date prdExp;    // 제품 유통기한

    public Product() {}

    public Product(int prdId, String prdName, String prdCompany, int prdPrice, char prdAdult, int prdStock, Date prdExp) {
        this.prdId = prdId;
        this.prdName = prdName;
        this.prdCompany = prdCompany;
        this.prdPrice = prdPrice;
        this.prdAdult = prdAdult;
        this.prdStock = prdStock;
        this.prdExp = prdExp;
    }

    // getter
    public int getPrdId() { return prdId; }
    public String getPrdName() { return prdName; }
    public String getPrdCompany() { return prdCompany; }
    public int getPrdPrice() { return prdPrice; }
    public char getPrdAdult() { return prdAdult; }
    public int getPrdStock() { return prdStock; }
    public Date getPrdExp() { return prdExp; }

    // setter

    public void setPrdId(int prdId) { this.prdId = prdId; }
    public void setPrdName(String prdName) { this.prdName = prdName; }
    public void setPrdCompany(String prdCompany) { this.prdCompany = prdCompany; }
    public void setPrdPrice(int prdPrice) { this.prdPrice = prdPrice; }
    public void setPrdAdult(char prdAdult) { this.prdAdult = prdAdult; }
    public void setPrdStock(int prdStock) { this.prdStock = prdStock; }
    public void setPrdExp(Date prdExp) { this.prdExp = prdExp; }
}
