package service;

import dao.StudentDAO;
import dao.ReportDAO; // Import ReportDAO
import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map; // Import Map
import java.util.regex.Pattern;

public class StudentService {
    // Sử dụng final để đảm bảo DAO không bị thay đổi sau khi khởi tạo
    private final StudentDAO studentDAO;
    private final ReportDAO reportDAO; // Thêm lại ReportDAO

    // Email validation pattern (một ví dụ, có thể cần phức tạp hơn)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public StudentService() {
        // Khởi tạo DAO, giả sử StudentDAO và ReportDAO có constructor mặc định
        this.studentDAO = new StudentDAO();
        this.reportDAO = new ReportDAO(); // Khởi tạo ReportDAO
    }

    public static String getStudentClassById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "";
        }
        String[] parts = studentId.split("-");
        if (parts.length != 2) {
            return "";
        }
        String classId = parts[1];
        if (classId == null || classId.trim().isEmpty()) {
        }

        return classId;
    }

    // Thêm sinh viên mới
    public void addStudent(Student student) throws SQLException, IllegalArgumentException {
        // Kiểm tra null cho các đối tượng quan trọng
        if (student == null || student.getId() == null || student.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin sinh viên hoặc Mã SV không được để trống!");
        }
        if (student.getEmail() == null) {
            throw new IllegalArgumentException("Email không được để trống!");
        }

        // Kiểm tra mã SV không trùng
        // Giả sử studentDAO.getStudentById trả về null nếu không tìm thấy
        if (studentDAO.getStudentById(student.getId()) != null) {
            throw new SQLException("Mã sinh viên '" + student.getId() + "' đã tồn tại!");
        }

        // Kiểm tra email hợp lệ (nếu có email)
        if (!student.getEmail().trim().isEmpty() && !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            // Ném IllegalArgumentException thay vì SQLException cho lỗi validation
            throw new IllegalArgumentException("Định dạng email không hợp lệ!");
        }

        // Gọi DAO để thêm sinh viên
        // Giả sử addStudent trong DAO xử lý việc thêm vào DB
        studentDAO.addStudent(student);
    }

    // Cập nhật thông tin sinh viên
    public void updateStudent(Student student) throws SQLException, IllegalArgumentException {
        if (student == null || student.getId() == null || student.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin sinh viên hoặc Mã SV không được để trống!");
        }
        if (student.getEmail() == null) {
            throw new IllegalArgumentException("Email không được để trống!");
        }

        // Kiểm tra sinh viên tồn tại trước khi cập nhật
        // Nên dùng phương thức lấy SV bất kể trạng thái nếu có
        if (studentDAO.getStudentByIdRegardlessOfStatus(student.getId()) == null) { // Giả sử có phương thức này
            throw new SQLException("Không tìm thấy sinh viên với mã '" + student.getId() + "' để cập nhật!");
        }

        // Kiểm tra email hợp lệ (nếu có email)
        if (!student.getEmail().trim().isEmpty() && !EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            throw new IllegalArgumentException("Định dạng email không hợp lệ!");
        }

        // Gọi DAO để cập nhật
        studentDAO.updateStudent(student);
    }

    // Xóa sinh viên theo ID (String) - Giả sử là xóa mềm
    public void deleteStudent(String id) throws SQLException, IllegalArgumentException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SV không được để trống!");
        }
        // Kiểm tra sinh viên tồn tại trước khi xóa (bất kể trạng thái)
        if (studentDAO.getStudentByIdRegardlessOfStatus(id) == null) { // Giả sử có phương thức này
            throw new SQLException("Không tìm thấy sinh viên với mã '" + id + "' để xóa!");
        }
        // Gọi DAO để xóa (cập nhật status)
        studentDAO.deleteStudent(id);
    }

    // Lấy danh sách tất cả sinh viên đang hoạt động
    public List<Student> getAllStudents() throws SQLException {
        // Phương thức này trong DAO hiện chỉ lấy SV 'Active'
        return studentDAO.getAllStudents();
    }

    // Tìm kiếm sinh viên theo keyword (String) (chỉ sinh viên hoạt động)
    public List<Student> searchStudents(String keyword) throws SQLException {
        // Có thể thêm xử lý keyword null/empty ở đây nếu DAO không xử lý
        if (keyword == null) {
            keyword = ""; // Tìm tất cả nếu keyword là null
        }
        return studentDAO.searchStudents(keyword);
    }

    // Lấy sinh viên theo ID (String) (chỉ sinh viên hoạt động)
    public Student getStudent(String id) throws SQLException, IllegalArgumentException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã SV không được để trống!");
        }
        return studentDAO.getStudentById(id); // Gọi phương thức DAO tương ứng
    }

    // --- PHƯƠNG THỨC CHO BÁO CÁO/THỐNG KÊ ---

    /**
     * Lấy dữ liệu thống kê số lượng sinh viên (đang hoạt động) theo lớp.
     *
     * @return Danh sách Map chứa thông tin thống kê ("classId", "className", "studentCount").
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Map<String, Object>> getStudentCountByClass() throws SQLException {
        return reportDAO.getStudentCountByClass(); // Gọi phương thức DAO tương ứng
    }

    /**
     * Lấy danh sách tất cả sinh viên (bao gồm cả không hoạt động) cho mục đích báo cáo.
     *
     * @return Danh sách tất cả đối tượng Student.
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    public List<Student> getAllStudentsForReport() throws SQLException {
        // Giả sử ReportDAO có phương thức này
        return reportDAO.getAllStudentsForReport();
    }
}