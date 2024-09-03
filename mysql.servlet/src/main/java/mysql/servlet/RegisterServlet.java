package mysql.servlet;

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
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

                String checkSql = "SELECT * FROM users WHERE username = ?";
                PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
                checkPstmt.setString(1, username);
                if (checkPstmt.executeQuery().next()) {
                    out.println(generateHtmlResponse("Такой пользователь уже существует. Выберите другой логин", "error"));
                } else {
                    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.executeUpdate();
                    out.println(generateHtmlResponse("Регистрация прошла успешно! Теперь вы можете залогиниться.", "success"));
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println(generateHtmlResponse("Ошибка: " + e.getMessage(), "error"));
            }
        }
    }

    private String generateHtmlResponse(String message, String messageType) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>Response</title>" +
               "    <link rel=\"stylesheet\" href=\"css/register.css\">" +
               "</head>" +
               "<body>" +
               "    <div class=\"message-container\">" +
               "        <div class=\"message-box " + messageType + "\">" +
               "            <p>" + message + "</p>" +
               "            <a href=\"login.html\">Вернуться ко входу</a>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }
}