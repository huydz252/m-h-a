import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
       
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            registerUser(conn, "username", "password");

            
            boolean loginResult = loginUser(conn, "username", "password");
            if (loginResult) {
                System.out.println("Đăng nhập thành công! Chào mừng bạn.");
               
            } else {
                System.out.println("Đăng nhập thất bại! Vui lòng kiểm tra lại tên người dùng và mật khẩu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(Connection conn, String username, String password) throws SQLException {

        String encryptedPassword = encryptPassword(password);
        

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword);
            stmt.executeUpdate();
        }
    }

    private static boolean loginUser(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    String enteredPassword = encryptPassword(password);
                    return storedPassword.equals(enteredPassword);
                } else {
                    return false; // Tên người dùng không tồn tại
                }
            }
        }
    }

    private static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
