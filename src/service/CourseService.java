package service;

import dao.CourseDAO;
import dao.ReportDAO; // Import ReportDAO
import model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Map; // Import Map nếu cần cho các phương thức khác

public class CourseService {
    private final CourseDAO courseDAO;
    private final ReportDAO reportDAO; // Thêm ReportDAO

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.reportDAO = new ReportDAO(); // Khởi tạo ReportDAO
    }

    // Thêm môn học mới
    public void addCourse(Course course) throws SQLException, IllegalArgumentException {
        if (course == null || course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin môn học hoặc Mã môn không được để trống!");
        }
        // Kiểm tra ID trùng lặp
        if (courseDAO.getCourseById(course.getCourseId()) != null) {
            throw new SQLException("Mã môn học '" + course.getCourseId() + "' đã tồn tại!");
        }
        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên dương.");
        }
        courseDAO.addCourse(course);
    }

    // Cập nhật thông tin môn học
    public void updateCourse(Course course) throws SQLException, IllegalArgumentException {
        if (course == null || course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin môn học hoặc Mã môn không được để trống!");
        }
        // Kiểm tra môn học tồn tại
        if (courseDAO.getCourseById(course.getCourseId()) == null) {
            throw new SQLException("Không tìm thấy môn học với mã '" + course.getCourseId() + "' để cập nhật!");
        }
        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên dương.");
        }
        courseDAO.updateCourse(course);
    }

    // Xóa môn học theo ID
    public void deleteCourse(String courseId) throws SQLException, IllegalArgumentException {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã môn học không được để trống!");
        }
        // Kiểm tra môn học tồn tại
        if (courseDAO.getCourseById(courseId) == null) {
            throw new SQLException("Không tìm thấy môn học với mã '" + courseId + "' để xóa!");
        }
        // DAO sẽ xử lý việc xóa (có thể cần kiểm tra ràng buộc khóa ngoại)
        courseDAO.deleteCourse(courseId);
    }

    // Lấy danh sách tất cả môn học (thường dùng cho ComboBox,...)
    public List<Course> getAllCourses() throws SQLException {
        // Giả sử CourseDAO có phương thức này và nó hoạt động đúng
        return courseDAO.getAllCourses();
    }

    // Tìm kiếm môn học theo keyword
    public List<Course> searchCourses(String keyword) throws SQLException {
        if (keyword == null) {
            keyword = ""; // Trả về tất cả nếu keyword null
        }
        // Giả sử CourseDAO có phương thức này
        return courseDAO.searchCourses(keyword);
    }

    // Lấy môn học theo ID
    public Course getCourseById(String courseId) throws SQLException, IllegalArgumentException {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã môn học không được để trống!");
        }
        // Giả sử CourseDAO có phương thức này
        return courseDAO.getCourseById(courseId);
    }

    // --- PHƯƠNG THỨC CHO BÁO CÁO ---
    /**
     * Lấy danh sách tất cả môn học cho mục đích báo cáo.
     * @return Danh sách tất cả đối tượng Course.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Course> getAllCoursesForReport() throws SQLException {
        // **SỬA LẠI Ở ĐÂY:** Gọi phương thức tương ứng từ ReportDAO
        return reportDAO.getAllCoursesForReport(); // Gọi DAO báo cáo
    }

}
