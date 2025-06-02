package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thông tin kết nối MySQL trong XAMPP
    // SỬA LẠI TÊN CSDL Ở ĐÂY: từ "student_management" thành "student_management1"
    private static final String URL = "jdbc:mysql://localhost:3306/student_management1?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Mặc định trong XAMPP
    private static final String PASSWORD = ""; // Mặc định trong XAMPP (để trống)

    // Phương thức lấy kết nối
    public static Connection getConnection() throws SQLException {
        try {
            // Đăng ký driver MySQL (Chỉ cần làm một lần, nhưng để đây cũng không sao)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Tạo và trả về kết nối
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // Lỗi này xảy ra nếu thiếu thư viện MySQL Connector/J
            System.err.println("Quan trọng: Không tìm thấy MySQL JDBC Driver. Hãy đảm bảo bạn đã thêm thư viện MySQL Connector/J vào dự án.");
            throw new SQLException("Không tìm thấy MySQL JDBC Driver: " + e.getMessage(), e);
        } catch (SQLException e) {
            // Lỗi này có thể do sai URL, username, password, hoặc MySQL server chưa chạy
            System.err.println("Lỗi kết nối CSDL. Kiểm tra URL, username, password và đảm bảo MySQL server đang chạy.");
            throw new SQLException("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }

    // Phương thức đóng kết nối (tùy chọn, nên dùng try-with-resources trong DAO)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ghi log lỗi thay vì chỉ in ra console trong ứng dụng thực tế
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }

    // Có thể thêm phương thức main để test kết nối nhanh
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
                // Thêm các kiểm tra khác nếu muốn
            } else {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            System.err.println("Kiểm tra kết nối thất bại: " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi để debug
        }
    }
}
