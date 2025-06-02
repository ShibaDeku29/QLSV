package util;

import model.Course;
import model.Grade; // Import Grade model
import model.Student;

import java.io.BufferedWriter; // Sử dụng BufferedWriter để hiệu quả hơn
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat; // Để định dạng ngày tháng
import java.util.List;
import java.util.Map; // Import Map để nhận dữ liệu thống kê

public class ReportGenerator {

    // Constructor rỗng
    public ReportGenerator() {
    }

    /**
     * Tạo báo cáo danh sách sinh viên dưới dạng file CSV.
     * @param students Danh sách sinh viên cần báo cáo.
     * @param filePath Đường dẫn đầy đủ đến file CSV sẽ được tạo.
     * @throws IOException Nếu có lỗi khi ghi file.
     */
    public void generateStudentReport(List<Student> students, String filePath) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("MaSV,HoTen,NgaySinh,GioiTinh,DiaChi,Email,SoDT,MaLop,TrangThai");
            writer.newLine();

            if (students != null) {
                for (Student s : students) {
                    writer.write(escapeCsv(s.getId()) + ",");
                    writer.write(escapeCsv(s.getName()) + ",");
                    writer.write(((s.getDob() != null) ? sdf.format(s.getDob()) : "") + ",");
                    writer.write(escapeCsv(s.getGender()) + ",");
                    writer.write(escapeCsv(s.getAddress()) + ",");
                    writer.write(escapeCsv(s.getEmail()) + ",");
                    writer.write(escapeCsv(s.getPhone()) + ",");
                    writer.write(escapeCsv(s.getClassId()) + ",");
                    writer.write(escapeCsv(s.getStatus()));
                    writer.newLine();
                }
            }
            System.out.println("Báo cáo Sinh viên đã được tạo thành công tại: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo báo cáo Sinh viên: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tạo báo cáo danh sách môn học dưới dạng file CSV.
     * @param courses Danh sách môn học cần báo cáo.
     * @param filePath Đường dẫn đầy đủ đến file CSV sẽ được tạo.
     * @throws IOException Nếu có lỗi khi ghi file.
     */
    public void generateCourseReport(List<Course> courses, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("MaMonHoc,TenMonHoc,SoTinChi");
            writer.newLine();

            if (courses != null) {
                for (Course c : courses) {
                    writer.write(escapeCsv(c.getCourseId()) + ",");
                    writer.write(escapeCsv(c.getCourseName()) + ",");
                    writer.write(String.valueOf(c.getCredits()));
                    writer.newLine();
                }
            }
            System.out.println("Báo cáo Môn học đã được tạo thành công tại: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo báo cáo Môn học: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tạo báo cáo bảng điểm dưới dạng file CSV.
     * @param grades Danh sách điểm cần báo cáo.
     * @param filePath Đường dẫn đầy đủ đến file CSV sẽ được tạo.
     * @throws IOException Nếu có lỗi khi ghi file.
     */
    public void generateGradeReport(List<Grade> grades, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("MaSinhVien,MaMonHoc,DiemSo");
            writer.newLine();

            if (grades != null) {
                for (Grade g : grades) {
                    writer.write(escapeCsv(g.getStudentId()) + ",");
                    writer.write(escapeCsv(g.getCourseId()) + ",");
                    writer.write(String.valueOf(g.getScore()));
                    writer.newLine();
                }
            }
            System.out.println("Báo cáo Điểm số đã được tạo thành công tại: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo báo cáo Điểm số: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tạo báo cáo thống kê số lượng sinh viên theo lớp dưới dạng file CSV.
     * @param classStats Danh sách chứa Map, mỗi Map đại diện cho một lớp với các key: "classId", "className", "studentCount".
     * @param filePath Đường dẫn đầy đủ đến file CSV sẽ được tạo.
     * @throws IOException Nếu có lỗi khi ghi file.
     */
    public void generateStudentCountByClassReport(List<Map<String, Object>> classStats, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("MaLop,TenLop,SoLuongSinhVien");
            writer.newLine();

            if (classStats != null) {
                for (Map<String, Object> stat : classStats) {
                    // Lấy dữ liệu từ Map, xử lý null nếu cần
                    String classId = stat.get("classId") != null ? stat.get("classId").toString() : "";
                    String className = stat.get("className") != null ? stat.get("className").toString() : "";
                    // COUNT thường trả về Long, cần chuyển sang String
                    String studentCount = stat.get("studentCount") != null ? stat.get("studentCount").toString() : "0";

                    writer.write(escapeCsv(classId) + ",");
                    writer.write(escapeCsv(className) + ",");
                    writer.write(studentCount); // Số lượng không cần escape
                    writer.newLine();
                }
            }
            System.out.println("Báo cáo Thống kê Sinh viên theo Lớp đã được tạo thành công tại: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo báo cáo Thống kê Sinh viên theo Lớp: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tạo báo cáo thống kê điểm trung bình theo môn học dưới dạng file CSV.
     * @param courseStats Danh sách chứa Map, mỗi Map đại diện cho một môn học với các key: "courseId", "courseName", "averageScore".
     * @param filePath Đường dẫn đầy đủ đến file CSV sẽ được tạo.
     * @throws IOException Nếu có lỗi khi ghi file.
     */
    public void generateAverageScoreByCourseReport(List<Map<String, Object>> courseStats, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("MaMonHoc,TenMonHoc,DiemTrungBinh");
            writer.newLine();

            if (courseStats != null) {
                for (Map<String, Object> stat : courseStats) {
                    String courseId = stat.get("courseId") != null ? stat.get("courseId").toString() : "";
                    String courseName = stat.get("courseName") != null ? stat.get("courseName").toString() : "";
                    // AVG thường trả về Double, định dạng lại nếu cần
                    String averageScore = "0.0";
                    if (stat.get("averageScore") != null) {
                        try {
                            double avg = Double.parseDouble(stat.get("averageScore").toString());
                            averageScore = String.format("%.2f", avg); // Định dạng 2 chữ số thập phân
                        } catch (NumberFormatException nfe) {
                            System.err.println("Lỗi định dạng điểm trung bình cho môn " + courseId);
                        }
                    }

                    writer.write(escapeCsv(courseId) + ",");
                    writer.write(escapeCsv(courseName) + ",");
                    writer.write(averageScore); // Điểm TB đã định dạng
                    writer.newLine();
                }
            }
            System.out.println("Báo cáo Thống kê Điểm trung bình theo Môn đã được tạo thành công tại: " + filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi tạo báo cáo Thống kê Điểm trung bình theo Môn: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Phương thức tiện ích để xử lý các ký tự đặc biệt trong CSV.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            String escapedValue = value.replace("\"", "\"\"");
            return "\"" + escapedValue + "\"";
        }
        return value;
    }
}
