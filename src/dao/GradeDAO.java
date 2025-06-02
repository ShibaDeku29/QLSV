package dao;

import model.Grade;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    // Thêm điểm số
    public void addGrade(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades (student_id, course_id, score) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grade.getStudentId());
            stmt.setString(2, grade.getCourseId());
            stmt.setFloat(3, grade.getScore());
            stmt.executeUpdate();
        }
    }

    // Cập nhật điểm số
    public void updateGrade(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET score = ? WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, grade.getScore());
            stmt.setString(2, grade.getStudentId());
            stmt.setString(3, grade.getCourseId());
            stmt.executeUpdate();
        }
    }

    // Xóa điểm số
    public void deleteGrade(String studentId, String courseId) throws SQLException {
        String sql = "DELETE FROM grades WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách điểm số của một sinh viên
    public List<Grade> getGradesByStudent(String studentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getFloat("score")
                );
                grades.add(grade);
            }
        }
        return grades;
    }

    // Lấy điểm số theo sinh viên và môn học
    public Grade getGrade(String studentId, String courseId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Grade(
                        rs.getString("student_id"),
                        rs.getString("course_id"),
                        rs.getFloat("score")
                );
            }
        }
        return null;
    }
}