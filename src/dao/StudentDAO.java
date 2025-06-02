package dao;

import model.Student;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // Thêm sinh viên mới
    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (id, name, dob, gender, address, email, phone, class_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            // Đảm bảo student.getDob() trả về java.util.Date và xử lý null
            if (student.getDob() != null) {
                stmt.setDate(3, new java.sql.Date(student.getDob().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setString(4, student.getGender());
            stmt.setString(5, student.getAddress());
            stmt.setString(6, student.getEmail());
            stmt.setString(7, student.getPhone());
            stmt.setString(8, student.getClassId());
            stmt.setString(9, student.getStatus());
            stmt.executeUpdate();
        }
    }

    // Cập nhật thông tin sinh viên
    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, dob = ?, gender = ?, address = ?, email = ?, phone = ?, class_id = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            // Đảm bảo student.getDob() trả về java.util.Date và xử lý null
            if (student.getDob() != null) {
                stmt.setDate(2, new java.sql.Date(student.getDob().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setString(3, student.getGender());
            stmt.setString(4, student.getAddress());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getClassId());
            stmt.setString(8, student.getStatus());
            stmt.setString(9, student.getId()); // WHERE clause
            stmt.executeUpdate();
        }
    }

    // Xóa sinh viên (xóa mềm - cập nhật trạng thái)
    public void deleteStudent(String id) throws SQLException {
        String sql = "UPDATE students SET status = 'Inactive' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách tất cả sinh viên đang hoạt động
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE status = 'Active' ORDER BY id"; // Thêm ORDER BY
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs); // Gọi hàm riêng để tạo Student
                students.add(student);
            }
        }
        return students;
    }

    // Tìm kiếm sinh viên theo mã SV hoặc tên (chỉ sinh viên hoạt động)
    public List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE (LOWER(id) LIKE LOWER(?) OR LOWER(name) LIKE LOWER(?)) AND status = 'Active' ORDER BY id"; // Thêm ORDER BY
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = mapResultSetToStudent(rs); // Gọi hàm riêng
                students.add(student);
            }
        }
        return students;
    }

    // Lấy sinh viên theo ID (chỉ sinh viên hoạt động)
    public Student getStudentById(String id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ? AND status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudent(rs); // Gọi hàm riêng
            }
        }
        return null; // Trả về null nếu không tìm thấy hoặc không hoạt động
    }

    /**
     * Lấy thông tin sinh viên theo ID, bất kể trạng thái hoạt động.
     * Dùng để kiểm tra sự tồn tại trước khi cập nhật/xóa.
     *
     * @param id Mã sinh viên cần lấy thông tin.
     * @return Đối tượng Student nếu tìm thấy, ngược lại trả về null.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public Student getStudentByIdRegardlessOfStatus(String id) throws SQLException { // Sửa kiểu trả về thành Student
        String sql = "SELECT * FROM students WHERE id = ?"; // Bỏ điều kiện status
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToStudent(rs); // Trả về đối tượng Student nếu tìm thấy
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy sinh viên (bất kể trạng thái) với ID " + id + ": " + e.getMessage());
            throw e; // Ném lại lỗi để tầng trên xử lý
        }
        return null; // Trả về null nếu không tìm thấy
    }

    /**
     * Phương thức trợ giúp để tạo đối tượng Student từ ResultSet.
     * Giúp tránh lặp lại code.
     * @param rs ResultSet đang trỏ đến một hàng dữ liệu sinh viên.
     * @return Đối tượng Student được tạo từ dữ liệu.
     * @throws SQLException Nếu có lỗi đọc dữ liệu từ ResultSet.
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        // Đảm bảo constructor của Student khớp (String, String, Date, ...)
        return new Student(
                rs.getString("id"),
                rs.getString("name"),
                rs.getDate("dob"), // java.sql.Date tương thích với java.util.Date
                rs.getString("gender"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("class_id"),
                rs.getString("status")
        );
    }
}