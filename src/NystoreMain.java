import DAO.EmployeeDAO;
import DAO.EmployeeDAOImpl;
import Entity.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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
                    String menuInput = scanner.nextLine();
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
        System.out.println("2. 제품 확인");
        System.out.println("3. 물품 입고");
        System.out.println("4. 계산");
        System.out.println("5. 매출 확인");
        System.out.println("6. 로그아웃");
        System.out.print("번호 입력 -> ");
    }
}
