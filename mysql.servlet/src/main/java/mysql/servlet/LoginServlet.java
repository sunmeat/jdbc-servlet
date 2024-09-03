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
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println(generateHtmlResponse("Вход успешен!", "success"));
            } else {
                out.println(generateHtmlResponse("Неверное имя пользователя либо пароль.", "error"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println(generateHtmlResponse("Ошибка: " + e.getMessage(), "error"));
        }
    }

    private String generateHtmlResponse(String message, String messageType) {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>Response</title>" +
               "    <link rel=\"stylesheet\" href=\"css/login.css\">" +
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