package dao;

import model.ClassRoom;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassRoomDAO {
    // Thêm lớp học mới
    public void addClassRoom(ClassRoom classRoom) throws SQLException {
        String sql = "INSERT INTO classrooms (class_id, class_name, teacher) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classRoom.getClassId());
            stmt.setString(2, classRoom.getClassName());
            stmt.setString(3, classRoom.getTeacher());
            stmt.executeUpdate();
        }
    }

    // Cập nhật thông tin lớp học
    public void updateClassRoom(ClassRoom classRoom) throws SQLException {
        String sql = "UPDATE classrooms SET class_name = ?, teacher = ? WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classRoom.getClassName());
            stmt.setString(2, classRoom.getTeacher());
            stmt.setString(3, classRoom.getClassId());
            stmt.executeUpdate();
        }
    }

    // Xóa lớp học
    public void deleteClassRoom(String classId) throws SQLException {
        String sql = "DELETE FROM classrooms WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classId);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách tất cả lớp học
    public List<ClassRoom> getAllClassRooms() throws SQLException {
        List<ClassRoom> classRooms = new ArrayList<>();
        String sql = "SELECT * FROM classrooms";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ClassRoom classRoom = new ClassRoom(
                        rs.getString("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher")
                );
                classRooms.add(classRoom);
            }
        }
        return classRooms;
    }

    // Tìm kiếm lớp học theo mã lớp hoặc tên lớp
    public List<ClassRoom> searchClassRooms(String keyword) throws SQLException {
        List<ClassRoom> classRooms = new ArrayList<>();
        String sql = "SELECT * FROM classrooms WHERE class_id LIKE ? OR class_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ClassRoom classRoom = new ClassRoom(
                        rs.getString("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher")
                );
                classRooms.add(classRoom);
            }
        }
        return classRooms;
    }

    // Lấy lớp học theo mã lớp
    public ClassRoom getClassRoomById(String classId) throws SQLException {
        String sql = "SELECT * FROM classrooms WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClassRoom(
                        rs.getString("class_id"),
                        rs.getString("class_name"),
                        rs.getString("teacher")
                );
            }
        }
        return null;
    }
}