import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDB {
    public static Connection getConnection() {
        Connection connection = null;
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            Class.forName("oracle.jdbc.OracleDriver");

            connection = DriverManager.getConnection(url, user, password);
            System.out.printf("%s\r\n", connection);
            System.out.printf("%s\r\n", "연결 성공");

        } catch (ClassNotFoundException e) {
            System.out.printf("%s\r\n", "jdbc11.jar 없습니다.");
        } catch (SQLException e) {
            System.out.printf("%s\r\n", "로그인 오류");
        } catch (IOException e) {
            System.out.printf("%s\r\n", "DB 설정 파일 읽어오기 실패");
        }
        return connection;
        }
}
