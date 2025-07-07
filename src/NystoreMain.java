import DAO.*;
import Entity.Employee;
import Entity.Product;
import Entity.Sales;
import Entity.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NystoreMain {
    public static void main(String[] args) {
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                System.out.println("DB 연결 실패!");
                return;
            }

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
                                System.out.println("로그인 시간 : " + currentEmployee.getLoginDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
                                System.out.println("=== 제품 리스트 및 재고 ===");
                                for (Product p : products) {
                                    String stars = "*".repeat(Math.min(p.getPrdStock(), 20));
                                    System.out.printf("%s : %s %d개\n", p.getPrdName(), stars, p.getPrdStock());
                                }
                            }
                            break;

                        case "3":
                            List<Product> stockProducts = productDAO.getAllProducts();
                            if (stockProducts.isEmpty()) {
                                System.out.println("입고할 제품이 없습니다. 제품을 먼저 등록하세요.");
                                break;
                            }

                            Random random = new Random();
                            Product randomProduct = stockProducts.get(random.nextInt(stockProducts.size()));
                            System.out.println("입고 제품: " + randomProduct.getPrdName());

                            int quantity = 0;
                            while (true) {
                                System.out.print("입고 수량을 입력하세요: ");
                                try {
                                    quantity = Integer.parseInt(scanner.nextLine().trim());
                                    if (quantity <= 0) {
                                        System.out.println("수량은 0보다 커야 합니다.");
                                        continue;
                                    }
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("숫자만 입력하세요.");
                                }
                            }

                            Stock stock = new Stock();
                            stock.setPrdId(randomProduct.getPrdId());
                            stock.setQuantity(quantity);
                            stock.setUpdateEmp(currentEmployee.getEmpId());

                            stockDAO.insertStock(stock);

                            int updatedStock = randomProduct.getPrdStock() + quantity;
                            productDAO.updateProduct(randomProduct.getPrdId(), updatedStock);

                            System.out.println("입고 완료! 현재 재고: " + updatedStock);
                            break;

                        case "4": // 계산
                            List<Product> purchaseProducts = productDAO.getAllProducts();

                            if (purchaseProducts.isEmpty()) {
                                System.out.println("등록된 제품이 없습니다.");
                                break;
                            }

                            System.out.println("구매 가능한 제품 목록:");
                            for (Product p : purchaseProducts) {
                                System.out.printf("ID: %d | %s | 가격: %d원 | 재고: %d개 | 19금: %s\n",
                                        p.getPrdId(), p.getPrdName(), p.getPrdPrice(), p.getPrdStock(), p.getPrdAdult());
                            }

                            System.out.print("구매할 제품 ID를 입력하세요: ");
                            int selectedId = Integer.parseInt(scanner.nextLine().trim());
                            Product selectedProduct = productDAO.getProductById(selectedId);

                            if (selectedProduct == null) {
                                System.out.println("해당 ID의 제품이 없습니다.");
                                break;
                            }

                            System.out.print("수량을 입력하세요: ");
                            int qty = Integer.parseInt(scanner.nextLine().trim());

                            if (selectedProduct.getPrdStock() < qty) {
                                System.out.println("재고가 부족합니다.");
                                break;
                            }

                            // 19금 확인
                            if (selectedProduct.getPrdAdult() == 'Y') {
                                System.out.print("이 상품은 19금입니다. 주민번호 앞자리를 입력하세요 (6자리): ");
                                String rrn = scanner.nextLine().trim();
                                if (rrn.length() != 6) {
                                    System.out.println("입력이 올바르지 않습니다.");
                                    break;
                                }
                                int birthYear = Integer.parseInt(rrn.substring(0, 2));
                                int age = 2025 - (birthYear + 1900); // 대략 계산
                                if (age < 19) {
                                    System.out.println("19세 미만은 구매할 수 없습니다.");
                                    break;
                                }
                            }

                            int total = selectedProduct.getPrdPrice() * qty;
                            System.out.printf("총 금액은 %d원입니다.\n", total);
                            System.out.print("결제 수단을 선택하세요 (CARD/CASH): ");
                            String method = scanner.nextLine().trim().toUpperCase();

                            Sales sale = new Sales();
                            sale.setPrdId(selectedId);
                            sale.setQuantity(qty);
                            sale.setTotalPrice(total);
                            sale.setPayment(method);
                            sale.setSoldEmp(currentEmployee.getEmpId());

                            if (method.equals("CARD")) {
                                System.out.print("카드 번호 입력: ");
                                String card = scanner.nextLine().trim();
                                sale.setCardNum(card);
                            } else if (method.equals("CASH")) {
                                System.out.print("현금 입력: ");
                                int cash = Integer.parseInt(scanner.nextLine().trim());
                                if (cash < total) {
                                    System.out.println("현금이 부족합니다.");
                                    break;
                                }
                                sale.setCash(cash);
                                sale.setCashChange(cash - total);
                            } else {
                                System.out.println("잘못된 결제 수단입니다.");
                                break;
                            }

                            // 매출 등록
                            salesDAO.insertSale(sale);

                            // 재고 차감
                            productDAO.updateProduct(selectedId, selectedProduct.getPrdStock() - qty);

                            System.out.println("\u001B[34m✅ 결제가 완료되었습니다. 감사합니다!\u001B[0m");
                            break;

                        case "5":  // 매출 확인
                            System.out.print("조회할 날짜 입력 (yyyy-MM-dd): ");
                            String date = scanner.nextLine().trim();

                            List<Sales> salesList = salesDAO.getSalesByDate(date);
                            if (salesList.isEmpty()) {
                                System.out.println("해당 날짜에 매출이 없습니다.");
                            } else {
                                System.out.println("매출 내역:");
                                for (Sales s : salesList) {
                                    System.out.printf("판매ID: %d, 제품ID: %d, 수량: %d, 총가격: %d, 결제수단: %s, 판매직원: %s, 판매시간: %s\n",
                                            s.getSalesId(), s.getPrdId(), s.getQuantity(), s.getTotalPrice(), s.getPayment(), s.getSoldEmp(),
                                            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getSaleDate()));
                                }
                            }
                            break;

                        case "6":
                            System.out.println("로그아웃합니다...");
                            worksDAO.updateLogoutRecord(currentEmployee.getEmpId());
                            currentEmployee = null;
                            break;

                        case "0":
                            System.out.println("프로그램 종료");
                            worksDAO.updateLogoutRecord(currentEmployee.getEmpId());
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
        System.out.println("===== 메인 메뉴 =====");
        System.out.println("1. 제품 입력");
        System.out.println("2. 제품(재고) 확인");
        System.out.println("3. 물품 입고");
        System.out.println("4. 계산");
        System.out.println("5. 매출 확인");
        System.out.println("6. 로그아웃");
        System.out.print("번호 입력 -> ");
    }
}
