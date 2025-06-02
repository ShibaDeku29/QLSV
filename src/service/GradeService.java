package service;

import dao.GradeDAO;
import dao.ReportDAO; // Import ReportDAO
import model.Grade;
import model.Course;
import service.CourseService;
import java.sql.SQLException;
import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Map; // Import Map

public class GradeService {
    private final GradeDAO gradeDAO;
    private final CourseService courseService;
    private final ReportDAO reportDAO; // Thêm ReportDAO

    public GradeService() {
        this.gradeDAO = new GradeDAO();
        this.courseService = new CourseService();
        this.reportDAO = new ReportDAO(); // Khởi tạo ReportDAO
    }

    // Thêm điểm mới
    public void addGrade(Grade grade) throws SQLException, IllegalArgumentException {
        if (grade == null || grade.getStudentId() == null || grade.getCourseId() == null) {
            throw new IllegalArgumentException("Thông tin điểm không hợp lệ.");
        }
        // Giả sử getGrade trong DAO nhận String IDs
        if (gradeDAO.getGrade(grade.getStudentId(), grade.getCourseId()) != null) {
            throw new SQLException("Điểm số cho sinh viên '" + grade.getStudentId() + "' và môn học '" + grade.getCourseId() + "' đã tồn tại!");
        }
        if (grade.getScore() < 0 || grade.getScore() > 10) { // Giả sử thang điểm 10
            throw new IllegalArgumentException("Điểm số phải nằm trong khoảng từ 0 đến 10!");
        }
        gradeDAO.addGrade(grade);
    }

    // Cập nhật điểm
    public void updateGrade(Grade grade) throws SQLException, IllegalArgumentException {
        if (grade == null || grade.getStudentId() == null || grade.getCourseId() == null) {
            throw new IllegalArgumentException("Thông tin điểm không hợp lệ.");
        }
        // Giả sử getGrade trong DAO nhận String IDs
        if (gradeDAO.getGrade(grade.getStudentId(), grade.getCourseId()) == null) {
            throw new SQLException("Điểm số cho sinh viên '" + grade.getStudentId() + "' và môn học '" + grade.getCourseId() + "' không tồn tại!");
        }
        if (grade.getScore() < 0 || grade.getScore() > 10) {
            throw new IllegalArgumentException("Điểm số phải nằm trong khoảng từ 0 đến 10!");
        }
        gradeDAO.updateGrade(grade);
    }

    // Xóa điểm
    public void deleteGrade(String studentId, String courseId) throws SQLException, IllegalArgumentException {
        if (studentId == null || studentId.trim().isEmpty() || courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên và Mã môn học không được để trống!");
        }
        // Giả sử getGrade trong DAO nhận String IDs
        if (gradeDAO.getGrade(studentId, courseId) == null) {
            throw new SQLException("Điểm số cho sinh viên '" + studentId + "' và môn học '" + courseId + "' không tồn tại!");
        }
        // Giả sử deleteGrade trong DAO nhận String IDs
        gradeDAO.deleteGrade(studentId, courseId);
    }

    // Lấy danh sách điểm của một sinh viên
    public List<Grade> getGradesByStudent(String studentId) throws SQLException, IllegalArgumentException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        // Giả sử getGradesByStudent trong DAO nhận String ID
        return gradeDAO.getGradesByStudent(studentId);
    }

    // Tính GPA của một sinh viên
    public double calculateGPA(String studentId) throws SQLException, IllegalArgumentException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        // Giả sử getGradesByStudent trong DAO nhận String ID
        List<Grade> grades = gradeDAO.getGradesByStudent(studentId);
        if (grades == null || grades.isEmpty()) {
            return 0.0; // Trả về 0 nếu không có điểm nào
        }

        double totalWeightedScore = 0.0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            // Lấy thông tin môn học để biết số tín chỉ
            // Giả sử courseService.getCourseById nhận String ID
            Course course = courseService.getCourseById(grade.getCourseId());

            if (course != null) {
                int credits = course.getCredits();
                totalWeightedScore += grade.getScore() * credits;
                totalCredits += credits;
            } else {
                System.err.println("Cảnh báo: Không tìm thấy thông tin môn học với ID: " + grade.getCourseId() + " khi tính GPA cho sinh viên " + studentId);
            }
        }
        return totalCredits > 0 ? (totalWeightedScore / totalCredits) : 0.0;
    }

    // Xếp loại học lực dựa trên GPA
    public String rankStudent(String studentId) throws SQLException, IllegalArgumentException {
        double gpa = calculateGPA(studentId);
        if (gpa >= 8.5) return "Giỏi";
        else if (gpa >= 7.0) return "Khá";
        else if (gpa >= 5.0) return "Trung bình";
        else if (gpa >= 0) return "Yếu";
        else return "Không xác định";
    }

    // --- PHƯƠNG THỨC CHO BÁO CÁO/THỐNG KÊ ---

    /**
     * Lấy danh sách tất cả điểm số cho mục đích báo cáo.
     * @return Danh sách tất cả đối tượng Grade.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Grade> getAllGradesForReport() throws SQLException {
        // **HOÀN THIỆN Ở ĐÂY:** Gọi phương thức tương ứng từ ReportDAO
        return reportDAO.getAllGradesForReport(); // Gọi DAO báo cáo
    }

    /**
     * Lấy dữ liệu thống kê điểm trung bình theo môn học.
     * @return Danh sách Map chứa thông tin thống kê ("courseId", "courseName", "averageScore").
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Map<String, Object>> getAverageScoreByCourse() throws SQLException {
        // Gọi phương thức tương ứng từ ReportDAO
        return reportDAO.getAverageScoreByCourse();
    }

    // Phương thức getAllGrades() cũ có thể không cần thiết nếu đã có getAllGradesForReport()
    // Hoặc có thể giữ lại và gọi DAO khác nếu logic khác nhau
    public List<Grade> getAllGrades() throws SQLException {
        // Hiện tại, có thể gọi luôn phương thức báo cáo nếu logic giống nhau
        // Hoặc gọi GradeDAO nếu có phương thức riêng
        // return gradeDAO.getAllGrades(); // Giả sử GradeDAO có phương thức này
        System.err.println("Phương thức getAllGrades() đang gọi phương thức báo cáo. Cân nhắc logic riêng nếu cần.");
        return getAllGradesForReport(); // Tạm thời gọi phương thức báo cáo
    }

}
