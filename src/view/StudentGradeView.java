package view;

import model.Grade;
import model.User;
import model.Student;
import model.Course;
import service.GradeService;
import service.StudentService;
import service.CourseService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentGradeView extends JFrame {

    // --- Styling Constants (Giữ nguyên từ CourseView) ---
    private static final Color PRIMARY_COLOR_LIGHT = new Color(238, 242, 247);
    private static final Color PRIMARY_COLOR_DARK = new Color(50, 50, 90);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final Color TABLE_HEADER_BG = new Color(210, 218, 226);
    private static final Color TABLE_GRID_COLOR = new Color(220, 220, 220);
    private static final Color TABLE_ALT_ROW_COLOR = new Color(245, 248, 251);

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font TITLE_BORDER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Border TEXT_FIELD_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 8, 5, 8)
    );

    // --- UI Components ---
    private JLabel studentInfoLabel;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JLabel gpaLabel, rankLabel;
    private GradeService gradeService;
    private StudentService studentService;
    private CourseService courseService;
    private User currentUser;

    // Constructor nhận User object
    public StudentGradeView(User user) {
        // Kiểm tra user hợp lệ
        if (user == null || !"student".equalsIgnoreCase(user.getRole()) || user.getStudentId() == null || user.getStudentId().trim().isEmpty()) {
            showErrorMessage("Lỗi: Người dùng không hợp lệ, không phải sinh viên, hoặc thiếu mã sinh viên liên kết.");
            setTitle("Lỗi Truy Cập");
            setSize(300, 100);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(new JLabel("Truy cập bị từ chối.", SwingConstants.CENTER));
            return;
        }

        this.currentUser = user;
        this.gradeService = new GradeService();
        this.studentService = new StudentService();
        this.courseService = new CourseService();

        // --- Cấu hình Look and Feel ---
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set LookAndFeel: " + ex.getMessage());
            }
        }

        // --- Cấu hình JFrame ---
        setTitle("Bảng điểm cá nhân");
        setSize(600, 400);
        setMinimumSize(new Dimension(500, 300));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- Panel Thông tin Sinh viên (NORTH) ---
        JPanel infoPanelTop = createInfoPanelTop();
        add(infoPanelTop, BorderLayout.NORTH);

        // --- Bảng Điểm (CENTER) ---
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // --- Panel Thông tin GPA và Xếp loại (SOUTH) ---
        JPanel infoPanelBottom = createInfoPanelBottom();
        add(infoPanelBottom, BorderLayout.SOUTH);

        // --- Tải dữ liệu ---
        loadStudentData();
    }

    // --- Panel Creation Methods ---

    private JPanel createInfoPanelTop() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Thông tin Sinh viên ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        infoPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        studentInfoLabel = createStyledLabel("Đang tải thông tin sinh viên...");
        infoPanel.add(studentInfoLabel);
        return infoPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setOpaque(false);

        // Bảng điểm
        String[] columns = {"Tên Môn học", "Điểm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradeTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW_COLOR);
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        };

        // Cấu hình giao diện bảng
        gradeTable.setFont(TABLE_FONT);
        gradeTable.setRowHeight(28);
        gradeTable.setGridColor(TABLE_GRID_COLOR);
        gradeTable.setShowGrid(true);
        gradeTable.setIntercellSpacing(new Dimension(0, 0));
        gradeTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        gradeTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        gradeTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        gradeTable.getTableHeader().setOpaque(false);
        gradeTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(gradeTable);
        TitledBorder tableBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Bảng Điểm ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        tableScrollPane.setBorder(new CompoundBorder(tableBorder, new EmptyBorder(5, 5, 5, 5)));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createInfoPanelBottom() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Kết quả Học tập ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        infoPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // GPA
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        infoPanel.add(createStyledLabel("Điểm trung bình (GPA):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gpaLabel = createStyledLabel("N/A");
        infoPanel.add(gpaLabel, gbc);

        // Rank
        gbc.gridx = 2;
        gbc.weightx = 0;
        infoPanel.add(createStyledLabel("Xếp loại:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        rankLabel = createStyledLabel("N/A");
        infoPanel.add(rankLabel, gbc);

        return infoPanel;
    }

    // --- Logic Methods ---

    private void loadStudentData() {
        String studentId = currentUser.getStudentId();
        try {
            // Lấy thông tin sinh viên
            Student student = studentService.getStudent(studentId);
            if (student != null) {
                studentInfoLabel.setText("Sinh viên: " + student.getName() + " (ID: " + student.getId() + ")");
            } else {
                studentInfoLabel.setText("Sinh viên ID: " + studentId + " (Không tìm thấy thông tin)");
            }

            // Lấy danh sách điểm
            List<Grade> grades = gradeService.getGradesByStudent(studentId);

            // Lấy danh sách môn học để tra cứu tên
            List<Course> allCourses = courseService.getAllCourses();
            Map<String, String> courseIdToNameMap = allCourses.stream()
                    .collect(Collectors.toMap(Course::getCourseId, Course::getCourseName));

            // Đổ dữ liệu vào bảng
            tableModel.setRowCount(0);
            if (grades != null) {
                for (Grade g : grades) {
                    String courseIdStr = g.getCourseId();
                    String courseName = courseIdToNameMap.getOrDefault(courseIdStr, "Mã môn học: " + courseIdStr);
                    tableModel.addRow(new Object[]{
                            courseName,
                            String.format("%.2f", g.getScore())
                    });
                }
            }

            // Tính GPA và xếp loại
            double gpa = gradeService.calculateGPA(studentId);
            String rank = gradeService.rankStudent(studentId);
            gpaLabel.setText(String.format("%.2f", gpa));
            rankLabel.setText(rank);

        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải dữ liệu điểm: " + e.getMessage());
            studentInfoLabel.setText("Lỗi tải dữ liệu.");
            gpaLabel.setText("Lỗi");
            rankLabel.setText("Lỗi");
            e.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Đã xảy ra lỗi không mong muốn: " + ex.getMessage());
            studentInfoLabel.setText("Lỗi tải dữ liệu.");
            gpaLabel.setText("Lỗi");
            rankLabel.setText("Lỗi");
            ex.printStackTrace();
        }
    }

    // --- UI Styling Helper Methods ---

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR_DARK);
        return label;
    }

    // --- Message Dialog Methods ---

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    // --- Main method (Chỉ dùng để test độc lập) ---
    public static void main(String[] args) {
        // Tạo User sinh viên giả để test
        User testStudentUser = new User("U004", "sv001", "sv001123", "student", "SV001");

        SwingUtilities.invokeLater(() -> {
            StudentGradeView studentView = new StudentGradeView(testStudentUser);
            studentView.setVisible(true);
        });

        // Test trường hợp không phải sinh viên
        User testAdminUser = new User("U001", "admin", "admin123", "admin", null);
        SwingUtilities.invokeLater(() -> new StudentGradeView(testAdminUser));

        // Test trường hợp sinh viên nhưng studentId là null
        User testStudentNullId = new User("U003", "student1", "student123", "student", null);
        SwingUtilities.invokeLater(() -> new StudentGradeView(testStudentNullId));

        // Test trường hợp sinh viên nhưng studentId là chuỗi rỗng
        User testStudentEmptyId = new User("U008", "student2", "student123", "student", " ");
        SwingUtilities.invokeLater(() -> new StudentGradeView(testStudentEmptyId));
    }
}