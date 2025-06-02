package service;

import dao.ClassRoomDAO;
import model.ClassRoom;

import java.sql.SQLException;
import java.util.List;

public class ClassRoomService {
    private final ClassRoomDAO classRoomDAO;

    public ClassRoomService() {
        this.classRoomDAO = new ClassRoomDAO();
    }

    // Thêm lớp học
    public void addClassRoom(ClassRoom classRoom) throws SQLException {
        // Kiểm tra mã lớp không trùng
        if (classRoomDAO.getClassRoomById(classRoom.getClassId()) != null) {
            throw new SQLException("Mã lớp đã tồn tại!");
        }
        classRoomDAO.addClassRoom(classRoom);
    }

    // Cập nhật lớp học
    public void updateClassRoom(ClassRoom classRoom) throws SQLException {
        if (classRoomDAO.getClassRoomById(classRoom.getClassId()) == null) {
            throw new SQLException("Lớp học không tồn tại!");
        }
        classRoomDAO.updateClassRoom(classRoom);
    }

    // Xóa lớp học
    public void deleteClassRoom(String classId) throws SQLException {
        if (classRoomDAO.getClassRoomById(classId) == null) {
            throw new SQLException("Lớp học không tồn tại!");
        }
        // Kiểm tra xem có sinh viên nào đang thuộc lớp này không
        // (Có thể thêm kiểm tra nếu cần, ví dụ: liên kết với bảng students)
        classRoomDAO.deleteClassRoom(classId);
    }

    // Lấy danh sách lớp học
    public List<ClassRoom> getAllClassRooms() throws SQLException {
        return classRoomDAO.getAllClassRooms();
    }

    // Tìm kiếm lớp học
    public List<ClassRoom> searchClassRooms(String keyword) throws SQLException {
        return classRoomDAO.searchClassRooms(keyword);
    }

    // Lấy lớp học theo ID
    public ClassRoom getClassRoomById(String classId) throws SQLException {
        return classRoomDAO.getClassRoomById(classId);
    }
}