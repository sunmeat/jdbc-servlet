import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {
                // завантаження драйвера MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // спроба підключення до бази даних
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
                    // перевірка і створення таблиці registration, якщо вона не існує
                    try (Statement stmt = conn.createStatement()) {
                        String createTableSql = "CREATE TABLE IF NOT EXISTS registration (" +
                                               "id INT AUTO_INCREMENT PRIMARY KEY," +
                                               "username VARCHAR(50) NOT NULL," +
                                               "password VARCHAR(50) NOT NULL)";
                        stmt.executeUpdate(createTableSql);
                    }

                    // перевірка, чи існує користувач з таким ім'ям
                    String checkSql = "SELECT * FROM registration WHERE username = ?";
                    try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                        checkPstmt.setString(1, username);
                        try (ResultSet rs = checkPstmt.executeQuery()) {
                            if (rs.next()) {
                                out.println(generateHtmlResponse("Такий користувач уже існує. Виберіть інший логін", "error"));
                            } else {
                                // реєстрація нового користувача
                                String sql = "INSERT INTO registration (username, password) VALUES (?, ?)";
                                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                    pstmt.setString(1, username);
                                    pstmt.setString(2, password);
                                    pstmt.executeUpdate();
                                    out.println(generateHtmlResponse("Реєстрація пройшла успішно! Тепер ви можете увійти.", "success"));
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    out.println(generateHtmlResponse("Помилка бази даних: сервер бази даних недоступний або неправильні налаштування. Будь ласка, зверніться до адміністратора.", "error"));
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                out.println(generateHtmlResponse("Помилка: Драйвер MySQL не знайдено. Будь ласка, перевірте конфігурацію сервера.", "error"));
                e.printStackTrace();
            }
        }
    }

    private String generateHtmlResponse(String message, String messageType) {
        return "<!DOCTYPE html>" +
               "<html lang=\"uk\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>Відповідь</title>" +
               "    <link rel=\"stylesheet\" href=\"css/register.css\">" +
               "</head>" +
               "<body>" +
               "    <div class=\"message-container\">" +
               "        <div class=\"message-box " + messageType + "\">" +
               "            <p>" + message + "</p>" +
               "            <a href=\"login.html\">Повернутися до входу</a>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }
}
