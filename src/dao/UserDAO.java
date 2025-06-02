package dao;

import model.User; // Đảm bảo User model là phiên bản dùng String userId và String studentId
import util.DatabaseConnection;

import java.sql.*;

public class UserDAO {

    // Thêm người dùng mới
    public void addUser(User user) throws SQLException {
        // SQL với các cột kiểu String
        String sql = "INSERT INTO users (userid, username, password, role, studentId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword()); // Nhớ mã hóa!
            stmt.setString(4, user.getRole());

            // Xử lý studentId kiểu String (có thể null)
            if (user.getStudentId() != null && !user.getStudentId().trim().isEmpty()) {
                // SỬA LỖI Ở ĐÂY: Dùng setString vì cột studentId trong DB là VARCHAR
                stmt.setString(5, user.getStudentId());
            } else {
                // Đặt là NULL nếu studentId là null hoặc rỗng
                stmt.setNull(5, Types.VARCHAR); // Dùng Types.VARCHAR vì cột là VARCHAR
            }

            stmt.executeUpdate();
        }
    }

    // Kiểm tra đăng nhập (lấy đầy đủ thông tin User)
    public User authenticate(String username, String password) throws SQLException {
        // Lấy các cột cần thiết
        String sql = "SELECT userid, username, password, role, studentId FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // So sánh mật khẩu dạng text thuần (Không an toàn!)
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Tạo đối tượng User bằng constructor đúng kiểu dữ liệu (String IDs)
                return new User(
                        rs.getString("userid"),      // String
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("studentId") // String (có thể là null)
                );
            }
        }
        return null; // Trả về null nếu không tìm thấy hoặc mật khẩu sai
    }

    // Lấy người dùng theo username (lấy đầy đủ thông tin User)
    public User getUserByUsername(String username) throws SQLException {
        // Lấy các cột cần thiết
        String sql = "SELECT userid, username, password, role, studentId FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Tạo đối tượng User bằng constructor đúng kiểu dữ liệu (String IDs)
                return new User(
                        rs.getString("userid"),      // String
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("studentId") // String (có thể là null)
                );
            }
        }
        return null; // Trả về null nếu không tìm thấy user
    }

    // Thêm các phương thức khác nếu cần (ví dụ: updateUser, deleteUser, getAllUsers, ...)
    // public void updateUser(User user) throws SQLException { ... }
    // public void deleteUser(String userId) throws SQLException { ... } // Tham số là String
    // public List<User> getAllUsers() throws SQLException { ... }
}
