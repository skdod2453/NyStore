import DAO.*;
import Entity.Employee;
import Entity.Product;
import Entity.Sales;
import Entity.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NystoreMain {
    public static void main(String[] args) {
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                System.out.println("\u001B[31mDB 연결 실패!\u001B[0m");
                return;
            }

            int storeBalance = 1234000;

            Scanner scanner = new Scanner(System.in);
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(conn);
            ProductDAO productDAO = new ProductDAOImpl(conn);
            StockDAO stockDAO = new StockDAOImpl(conn);
            SalesDAO salesDAO = new SalesDAOImpl(conn);
            WorksDAO worksDAO = new WorksDAOImpl(conn);

            Employee currentEmployee = null;    // 현재 로그인한 사원 정보
            boolean run = true;

            while (run) {
                if (currentEmployee == null) {  // 현재 로그인한 사원이 없을 경우. 즉, 비로그인 상태
                    showLoginMenu();            // 로그인 메뉴 보여주기
                    String input = scanner.nextLine();

                    switch (input) {
                        case "1":
                            System.out.print("아이디 : ");
                            String empID = scanner.nextLine().trim();
                            System.out.print("비밀번호: ");
                            String empPw = scanner.nextLine().trim();

                            Employee loginEmployee = employeeDAO.findByIdAndPassword(empID, empPw);
                            if (loginEmployee == null) {
                                System.out.println("\u001B[31m아이디 또는 비밀번호가 올바르지 않습니다.\u001B[0m");
                            } else {
                                currentEmployee = loginEmployee;
                                employeeDAO.updateLoginTime(empID);
                                worksDAO.insertLoginRecord(empID);

                                currentEmployee = employeeDAO.findByIdAndPassword(empID, empPw);
                                System.out.println("\u001B[32m" + currentEmployee.getEmpName() + "님 안녕하세요!\u001B[0m");
                                System.out.println("💰 가게 시작 잔고: " + storeBalance + "원");
                                System.out.println("💭 로그인 시간 : " + currentEmployee.getLoginDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                System.out.println();
                            }
                            break;

                        case "0":
                            System.out.println("프로그램 종료");
                            return;

                        default:
                            System.out.println("\u001B[31m 잘못된 입력입니다. 다시 시도하세요.\n\u001B[0m");
                    }
                } else {       // 현재 로그인한 사원이 있을 경우. 즉, 로그인 상태
                    showMainMenu();
                    String menuInput = scanner.nextLine().trim();
                    switch (menuInput) {
                        case "1": // 제품 입력
                            Product newProduct = new Product();

                            System.out.print("제품명: ");
                            String name = scanner.nextLine().trim();
                            while (name.isBlank()) {
                                System.out.print("제품명은 필수입니다. 다시 입력: ");
                                name = scanner.nextLine().trim();
                            }

                            System.out.print("제조회사: ");
                            String company = scanner.nextLine().trim();
                            while (company.isBlank()) {
                                System.out.print("제조회사는 필수입니다. 다시 입력: ");
                                company = scanner.nextLine().trim();
                            }

                            System.out.print("가격: ");
                            int price = 0;
                            while (true) {
                                try {
                                    price = Integer.parseInt(scanner.nextLine().trim());
                                    if (price <= 0) throw new NumberFormatException();
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.print("가격은 0보다 큰 숫자여야 합니다. 다시 입력: ");
                                }
                            }

                            System.out.print("19금 물품 여부 (Y/N): ");
                            char adult;
                            while (true) {
                                String adultInput = scanner.nextLine().trim().toUpperCase();
                                if (adultInput.equals("Y") || adultInput.equals("N")) {
                                    adult = adultInput.charAt(0);
                                    break;
                                } else {
                                    System.out.print("Y 또는 N 중 하나를 입력하세요: ");
                                }
                            }

                            Date expDate = null;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setLenient(false);
                            while (true) {
                                System.out.print("유통기한 (yyyy-MM-dd, 없으면 enter): ");
                                String expStr = scanner.nextLine().trim();
                                if (expStr.isBlank()) {
                                    break;
                                }
                                try {
                                    expDate = sdf.parse(expStr);
                                    break;
                                } catch (Exception e) {
                                    System.out.println("날짜 형식이 잘못되었습니다. 예: 2025-12-31");
                                }
                            }

                            newProduct.setPrdName(name);
                            newProduct.setPrdCompany(company);
                            newProduct.setPrdPrice(price);
                            newProduct.setPrdAdult(adult);
                            newProduct.setPrdExp(expDate);

                            newProduct.setPrdStock(10);

                            productDAO.insertProduct(newProduct);
                            System.out.println("\u001B[34m✅ 제품 등록 완료!\u001B[0m");
                            break;

                        case "2":
                            List<Product> products = productDAO.getAllProducts();
                            if (products.isEmpty()) {
                                System.out.println("등록된 제품이 없습니다.");
                            } else {
                                System.out.println("\n=== 제품 리스트 및 재고 ===");
                                for (Product p : products) {
                                    String stars = "*".repeat(Math.min(p.getPrdStock(), 20));
                                    System.out.printf("%s : %s %d개\n", p.getPrdName(), stars, p.getPrdStock());
                                }
                            }
                            break;

                        case "3":
                            List<Product> productsForStock = productDAO.getAllProducts();
                            if (productsForStock.isEmpty()) {
                                System.out.println("\u001B[31m입고할 제품이 없습니다. 먼저 제품을 등록하세요.\u001B[0m");
                                break;
                            }

                            Random rand = new Random();
                            Product selectedProduct = productsForStock.get(rand.nextInt(productsForStock.size()));
                            int randomQuantity = rand.nextInt(10) + 1; // 1~10개 랜덤 수량
                            long currentTimeMillis = System.currentTimeMillis();
                            long randomOffset = (long) (rand.nextDouble() * 3600 * 1000); // 0~1시간
                            Date randomTime = new Date(currentTimeMillis - randomOffset); // 현재보다 과거

                            System.out.println("\n🚚 입고 제안 - 아래 정보로 입고하세요");
                            System.out.println("제품명: " + selectedProduct.getPrdName());
                            System.out.println("수량: " + randomQuantity + "개");
                            System.out.println("입고 시각: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(randomTime));

                            System.out.print("위 정보로 입고하시겠습니까? (Y/N): ");
                            String confirm = scanner.nextLine().trim().toUpperCase();
                            if (!confirm.equals("Y")) {
                                System.out.println("입고 취소됨.");
                                break;
                            }

                            Stock stock = new Stock();
                            stock.setPrdId(selectedProduct.getPrdId());
                            stock.setQuantity(randomQuantity);
                            stock.setUpdateEmp(currentEmployee.getEmpId());
                            stock.setInDate(randomTime); // 입고시간 설정

                            stockDAO.insertStock(stock);
                            int newStock = selectedProduct.getPrdStock() + randomQuantity;
                            productDAO.updateProduct(selectedProduct.getPrdId(), newStock);

                            System.out.println("입고 완료! 현재 재고: " + newStock);
                            break;

                        case "4": // 계산
                            while (true) {
                                System.out.println("\n=== [계산 메뉴] ===");
                                System.out.println("1. 제품 이름으로 검색");
                                System.out.println("2. 계산 진행");
                                System.out.println("0. 이전 메뉴로");
                                System.out.print("선택: ");
                                String input = scanner.nextLine().trim();

                                if (input.equals("1")) {
                                    System.out.print("검색할 제품 이름 입력: ");
                                    String productname = scanner.nextLine().trim();
                                    List<Product> searchResults = productDAO.searchProductsByName(productname);
                                    if (searchResults.isEmpty()) {
                                        System.out.println("\u001B[31m검색 결과가 없습니다.\u001B[0m");
                                    } else {
                                        System.out.println("\n🔍 검색 결과:");
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                        for (Product p : searchResults) {
                                            if (p.getPrdStock() > 0) {
                                                String expStr = (p.getPrdExp() == null) ? "없음" : sdf2.format(p.getPrdExp());
                                                System.out.printf("ID: %d | %s | 가격: %d원 | 재고: %d개 | 유통기한: %s | 19금: %s\n",
                                                        p.getPrdId(), p.getPrdName(), p.getPrdPrice(), p.getPrdStock(), expStr, p.getPrdAdult());
                                            }
                                        }
                                    }

                                } else if (input.equals("2")) {
                                    List<Product> purchaseProducts = productDAO.getAllProducts();
                                    if (purchaseProducts.isEmpty()) {
                                        System.out.println("\u001B[31m등록된 제품이 없습니다.\u001B[0m");
                                        continue;
                                    }

                                    List<Sales> salesToInsert = new ArrayList<>();
                                    int totalAmount = 0;

                                    while (true) {
                                        System.out.println("\n=== 구매 가능한 제품 목록 ===");
                                        Date today = new Date();
                                        boolean hasAvailable = false;
                                        for (Product p : purchaseProducts) {
                                            if (p.getPrdStock() > 0) {
                                                System.out.printf("ID: %d | %s | 가격: %d원 | 재고: %d개 | 유통기한: %s | 19금: %s\n",
                                                        p.getPrdId(), p.getPrdName(), p.getPrdPrice(), p.getPrdStock(),
                                                        (p.getPrdExp() != null
                                                                ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(p.getPrdExp())
                                                                : "없음"),
                                                        p.getPrdAdult());
                                                hasAvailable = true;
                                            }
                                        }
                                        if (!hasAvailable) {
                                            System.out.println("\u001B[31m구매 가능한 제품이 없습니다.\u001B[0m");
                                            break;
                                        }

                                        System.out.print("구매할 제품 ID 입력 (0 입력 시 결제): ");
                                        int selectedId = Integer.parseInt(scanner.nextLine().trim());
                                        if (selectedId == 0) break;

                                        Product selected = null;
                                        for (Product p : purchaseProducts) {
                                            if (p.getPrdId() == selectedId) {
                                                selected = p;
                                                break;
                                            }
                                        }

                                        if (selected == null || selected.getPrdStock() <= 0) {
                                            System.out.println("\u001B[31m해당 ID의 제품이 없거나 품절입니다.\u001B[0m");
                                            continue;
                                        }


                                        if (selected.getPrdExp() != null && selected.getPrdExp().before(today)) {
                                            System.out.println("\u001B[31m⚠ 유통기한이 지난 상품은 구매할 수 없습니다.\u001B[0m");
                                            continue;
                                        }

                                        System.out.print("수량 입력: ");
                                        int qty = Integer.parseInt(scanner.nextLine().trim());
                                        if (qty <= 0 || qty > selected.getPrdStock()) {
                                            System.out.println("\u001B[31m재고 부족 또는 잘못된 수량.\u001B[0m");
                                            continue;
                                        }

                                        // 19금 확인
                                        if (selected.getPrdAdult() == 'Y') {
                                            System.out.print("주민등록번호 앞 6자리와 뒷자리 첫 숫자 입력 (예: 091025-3): ");
                                            String rrn = scanner.nextLine().trim();

                                            if (!rrn.matches("\\d{6}-\\d")) {
                                                System.out.println("\u001B[31m형식 오류.\u001B[0m");
                                                continue;
                                            }

                                            int birthYear = Integer.parseInt(rrn.substring(0, 2));
                                            int centuryCode = Integer.parseInt(rrn.substring(7, 8));
                                            if (centuryCode == 1 || centuryCode == 2 || centuryCode == 5 || centuryCode == 6) {
                                                birthYear += 1900;
                                            } else if (centuryCode == 3 || centuryCode == 4 || centuryCode == 7 || centuryCode == 8) {
                                                birthYear += 2000;
                                            } else {
                                                System.out.println("\u001B[31m잘못된 성별코드.\u001B[0m");
                                                continue;
                                            }

                                            int age = 2025 - birthYear;
                                            if (age < 19) {
                                                System.out.println("\u001B[31m🔞미성년자는 구매 불가.\u001B[0m");
                                                continue;
                                            }
                                        }

                                        int itemTotal = selected.getPrdPrice() * qty;
                                        totalAmount += itemTotal;

                                        Sales sale = new Sales();
                                        sale.setPrdId(selected.getPrdId());
                                        sale.setQuantity(qty);
                                        sale.setTotalPrice(itemTotal);
                                        sale.setSoldEmp(currentEmployee.getEmpId());
                                        salesToInsert.add(sale);

                                        selected.setPrdStock(selected.getPrdStock() - qty);
                                        System.out.println("장바구니에 추가됨.");
                                    }

                                    if (salesToInsert.isEmpty()) {
                                        System.out.println("\u001B[31m결제할 항목이 없습니다.\u001B[0m");
                                        continue;
                                    }

                                    System.out.printf("총 결제 금액: %d원\n", totalAmount);
                                    System.out.print("결제 수단 (CARD / CASH): ");
                                    String payment = scanner.nextLine().trim().toUpperCase();

                                    if (!payment.equals("CARD") && !payment.equals("CASH")) {
                                        System.out.println("\u001B[31m결제 수단 오류.\u001B[0m");
                                        continue;
                                    }

                                    String cardNum = null;
                                    int cash = 0, change = 0;
                                    if (payment.equals("CARD")) {
                                        System.out.print("카드 번호 입력: ");
                                        cardNum = scanner.nextLine().trim();
                                        storeBalance += totalAmount;
                                    } else {
                                        System.out.print("현금 입력: ");
                                        cash = Integer.parseInt(scanner.nextLine().trim());
                                        if (cash < totalAmount) {
                                            System.out.println("현금 부족.");
                                            continue;
                                        }
                                        change = cash - totalAmount;
                                        storeBalance += totalAmount;
                                        System.out.println("거스름돈: " + change + "원");
                                    }

                                    for (Sales s : salesToInsert) {
                                        s.setPayment(payment);
                                        s.setCardNum(cardNum);
                                        s.setCash(payment.equals("CASH") ? cash : 0);
                                        s.setCashChange(payment.equals("CASH") ? change : 0);
                                        salesDAO.insertSale(s);

                                        for (Product p : purchaseProducts) {
                                            if (p.getPrdId() == s.getPrdId()) {
                                                productDAO.updateProduct(p.getPrdId(), p.getPrdStock());
                                                break;
                                            }
                                        }
                                    }

                                    System.out.println("\u001B[34m✅ 결제 완료!\u001B[0m");
                                    System.out.println("💰 현재 가게 잔고: " + storeBalance + "원");

                                } else if (input.equals("0")) {
                                    break;
                                } else {
                                    System.out.println("❌ 잘못된 입력입니다.");
                                }
                            }
                            break;


                        case "5":  // 매출 확인
                            System.out.print("조회할 날짜 입력 (yyyy-MM-dd): ");
                            String date = scanner.nextLine().trim();

                            List<Sales> salesList = salesDAO.getSalesByDate(date);
                            if (salesList.isEmpty()) {
                                System.out.println("해당 날짜에 매출이 없습니다.");
                            } else {
                                System.out.println("\n📋 매출 내역:");
                                int dailyTotal = 0;

                                for (Sales s : salesList) {
                                    System.out.printf("판매ID: %d | 제품ID: %d | 수량: %d | 총가격: %d원 | 결제수단: %s | 판매직원: %s | 판매시간: %s\n",
                                            s.getSalesId(), s.getPrdId(), s.getQuantity(), s.getTotalPrice(), s.getPayment(), s.getSoldEmp(),
                                            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getSaleDate()));

                                    dailyTotal += s.getTotalPrice();
                                }

                                System.out.println("\n💵 총 매출: " + dailyTotal + "원");
                            }
                            break;

                        case "6":
                            System.out.println("로그아웃합니다...");
                            worksDAO.updateLogoutRecord(currentEmployee.getEmpId());
                            currentEmployee = null;
                            break;

                        case "0":
                            if (currentEmployee != null) {
                                worksDAO.updateLogoutRecord(currentEmployee.getEmpId());
                            }
                            System.out.println("프로그램 종료");
                            run = false;
                            break;

                        default:
                            System.out.println("메뉴를 다시 선택하세요.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("DB 연결 중 오류 발생");
        }
    }

    public static void showLoginMenu() {        // 로그인 메뉴
        System.out.println("====== NyStore\uD83C\uDFEA =====");
        System.out.println("1. 로그인");
        System.out.println("0. 종료");
        System.out.print("번호 입력 -> ");
    }

    public static void showMainMenu() {
        System.out.println("\n===== 메인 메뉴 =====");
        System.out.println("1. 제품 입력");
        System.out.println("2. 제품(재고) 확인");
        System.out.println("3. 물품 입고");
        System.out.println("4. 계산");
        System.out.println("5. 매출 확인");
        System.out.println("6. 로그아웃");
        System.out.print("번호 입력 -> ");
    }
}
