package dao;

import model.Course;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    // Thêm môn học mới
    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_id, course_name, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.executeUpdate();
        }
    }

    // Cập nhật môn học
    public void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_name = ?, credits = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getCredits());
            stmt.setString(3, course.getCourseId());
            stmt.executeUpdate();
        }
    }

    // Xóa môn học
    public void deleteCourse(String courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách tất cả môn học
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    // Tìm kiếm môn học theo mã môn hoặc tên môn
    public List<Course> searchCourses(String keyword) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE course_id LIKE ? OR course_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    // Lấy môn học theo mã môn
    public Course getCourseById(String courseId) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Course(
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credits")
                );
            }
        }
        return null;
    }
}