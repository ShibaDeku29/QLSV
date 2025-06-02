package dao;

import model.Course; // Import các model cần thiết
import model.Grade;
import model.Student;
import util.DatabaseConnection; // Import lớp kết nối CSDL

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Import Statement
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp DAO (Data Access Object) cho các chức năng báo cáo và thống kê.
 * Chứa các phương thức truy vấn dữ liệu tổng hợp và danh sách từ cơ sở dữ liệu.
 */
public class ReportDAO {

    /**
     * Lấy danh sách tất cả sinh viên (bao gồm cả không hoạt động) cho báo cáo.
     *
     * @return Danh sách các đối tượng Student.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Student> getAllStudentsForReport() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id"; // Lấy tất cả sinh viên, sắp xếp theo ID

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement(); // Dùng Statement vì không có tham số
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Tạo đối tượng Student từ ResultSet
                // Đảm bảo constructor của Student khớp
                Student student = new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("class_id"),
                        rs.getString("status")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sinh viên cho báo cáo: " + e.getMessage());
            throw e;
        }
        return students;
    }

    /**
     * Lấy danh sách tất cả môn học cho báo cáo.
     *
     * @return Danh sách các đối tượng Course.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Course> getAllCoursesForReport() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id"; // Lấy tất cả môn học

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Tạo đối tượng Course từ ResultSet
                // Đảm bảo constructor của Course khớp (String, String, int)
                Course course = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách môn học cho báo cáo: " + e.getMessage());
            throw e;
        }
        return courses;
    }

    /**
     * Lấy danh sách tất cả điểm số cho báo cáo tổng hợp.
     *
     * @return Danh sách các đối tượng Grade.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Grade> getAllGradesForReport() throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades ORDER BY student_id, course_id"; // Lấy tất cả điểm

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Tạo đối tượng Grade từ ResultSet
                // Đảm bảo constructor của Grade khớp (String, String, float/double)
                Grade grade = new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getFloat("score") // Hoặc getDouble nếu kiểu là double
                );
                grades.add(grade);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách điểm cho báo cáo: " + e.getMessage());
            throw e;
        }
        return grades;
    }


    /**
     * Lấy thống kê số lượng sinh viên (đang hoạt động) theo từng lớp học.
     * @return Danh sách các Map, mỗi Map chứa: "classId", "className", "studentCount".
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Map<String, Object>> getStudentCountByClass() throws SQLException {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT c.class_id, c.class_name, COUNT(s.id) as student_count " +
                "FROM classrooms c " +
                "LEFT JOIN students s ON c.class_id = s.class_id AND s.status = 'Active' " +
                "GROUP BY c.class_id, c.class_name " +
                "ORDER BY c.class_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("classId", rs.getString("class_id"));
                row.put("className", rs.getString("class_name"));
                row.put("studentCount", rs.getLong("student_count"));
                stats.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thống kê sinh viên theo lớp: " + e.getMessage());
            throw e;
        }
        return stats;
    }

    /**
     * Lấy thống kê điểm trung bình theo từng môn học.
     * @return Danh sách các Map, mỗi Map chứa: "courseId", "courseName", "averageScore".
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Map<String, Object>> getAverageScoreByCourse() throws SQLException {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_name, AVG(g.score) as average_score " +
                "FROM courses c " +
                "LEFT JOIN grades g ON c.course_id = g.course_id " +
                "GROUP BY c.course_id, c.course_name " +
                "ORDER BY c.course_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("courseId", rs.getString("course_id"));
                row.put("courseName", rs.getString("course_name"));
                row.put("averageScore", rs.getObject("average_score")); // Dùng getObject để xử lý null
                stats.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thống kê điểm trung bình theo môn: " + e.getMessage());
            throw e;
        }
        return stats;
    }

    // --- Thêm các phương thức DAO cho các báo cáo/thống kê khác nếu cần ---

}
