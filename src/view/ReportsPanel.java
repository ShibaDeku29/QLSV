package view;

import model.Student;
import model.Course;
import model.Grade;
import service.StudentService;
import service.CourseService;
import service.GradeService;
import util.ReportGenerator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReportsPanel extends JPanel {

    // --- Styling Constants (Giữ nguyên từ CourseView) ---
    private static final Color PRIMARY_COLOR_LIGHT = new Color(238, 242, 247);
    private static final Color PRIMARY_COLOR_DARK = new Color(50, 50, 90);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final Color BUTTON_NORMAL_BG = new Color(100, 149, 237);
    private static final Color BUTTON_HOVER_BG = new Color(135, 206, 250);
    private static final Color BUTTON_PRESSED_BG = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color TABLE_HEADER_BG = new Color(210, 218, 226);
    private static final Color TABLE_GRID_COLOR = new Color(220, 220, 220);
    private static final Color TABLE_ALT_ROW_COLOR = new Color(245, 248, 251);

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font TITLE_BORDER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Border TEXT_FIELD_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 8, 5, 8)
    );
    private static final Insets BUTTON_MARGIN = new Insets(6, 15, 6, 15);

    // --- UI Components ---
    private JComboBox<String> reportTypeComboBox;
    private JButton generateReportButton;
    private JButton exportButton;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // --- Services ---
    private StudentService studentService;
    private CourseService courseService;
    private GradeService gradeService;
    private ReportGenerator reportGenerator;

    // --- Data Storage ---
    private List<?> currentReportData;
    private String currentReportType = "";

    public ReportsPanel() {
        // Khởi tạo các service
        try {
            studentService = new StudentService();
            courseService = new CourseService();
            gradeService = new GradeService();
            reportGenerator = new ReportGenerator();
        } catch (Exception e) {
            showErrorMessage("Lỗi khởi tạo hệ thống báo cáo: " + e.getMessage());
            e.printStackTrace();
            setLayout(new BorderLayout());
            add(new JLabel("Không thể tải dữ liệu báo cáo do lỗi khởi tạo.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

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

        // --- Cấu hình Panel ---
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // --- Panel Lựa chọn Báo cáo (NORTH) ---
        JPanel optionsPanel = createOptionsPanel();
        add(optionsPanel, BorderLayout.NORTH);

        // --- Khu vực Hiển thị Báo cáo (CENTER) ---
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        generateReportButton.addActionListener(this::generateReport);
        exportButton.addActionListener(this::exportReport);
    }

    // --- Panel Creation Methods ---

    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        optionsPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Tùy chọn Báo cáo ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        optionsPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        optionsPanel.add(createStyledLabel("Chọn loại báo cáo:"));
        String[] reportTypes = {
                "-- Chọn báo cáo --",
                "Danh sách Sinh viên",
                "Danh sách Môn học",
                "Bảng điểm Tổng hợp",
                "Thống kê Sinh viên theo Lớp",
                "Thống kê Điểm trung bình theo Môn"
        };
        reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(INPUT_FONT);
        reportTypeComboBox.setBorder(TEXT_FIELD_BORDER);
        optionsPanel.add(reportTypeComboBox);

        generateReportButton = createStyledButton("Xem Báo cáo");
        optionsPanel.add(generateReportButton);

        exportButton = createStyledButton("Xuất ra File CSV");
        exportButton.setEnabled(false);
        optionsPanel.add(exportButton);

        return optionsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setOpaque(false);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel) {
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
        reportTable.setFont(TABLE_FONT);
        reportTable.setRowHeight(28);
        reportTable.setGridColor(TABLE_GRID_COLOR);
        reportTable.setShowGrid(true);
        reportTable.setIntercellSpacing(new Dimension(0, 0));
        reportTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        reportTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        reportTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        reportTable.getTableHeader().setOpaque(false);
        reportTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));
        reportTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        reportTable.setFillsViewportHeight(true);
        reportTable.setAutoCreateRowSorter(true);

        scrollPane = new JScrollPane(reportTable);
        TitledBorder tableBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Kết quả Báo cáo / Thống kê ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        scrollPane.setBorder(new CompoundBorder(tableBorder, new EmptyBorder(5, 5, 5, 5)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // --- Logic Methods ---

    private void generateReport(ActionEvent e) {
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();
        currentReportData = null;
        currentReportType = "";
        exportButton.setEnabled(false);

        if (selectedReport == null || selectedReport.equals("-- Chọn báo cáo --")) {
            showWarningMessage("Vui lòng chọn một loại báo cáo.");
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<?>, Void> worker = new SwingWorker<List<?>, Void>() {
            @Override
            protected List<?> doInBackground() throws Exception {
                switch (selectedReport) {
                    case "Danh sách Sinh viên":
                        return studentService.getAllStudentsForReport();
                    case "Danh sách Môn học":
                        return courseService.getAllCoursesForReport();
                    case "Bảng điểm Tổng hợp":
                        return gradeService.getAllGradesForReport();
                    case "Thống kê Sinh viên theo Lớp":
                        return studentService.getStudentCountByClass();
                    case "Thống kê Điểm trung bình theo Môn":
                        return gradeService.getAverageScoreByCourse();
                    default:
                        throw new UnsupportedOperationException("Loại báo cáo không được hỗ trợ: " + selectedReport);
                }
            }

            @Override
            protected void done() {
                try {
                    List<?> resultData = get();
                    currentReportData = resultData;
                    currentReportType = selectedReport;
                    displayReportData(selectedReport, resultData);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showErrorMessage("Quá trình tải báo cáo bị gián đoạn.");
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof SQLException) {
                        showErrorMessage("Lỗi truy vấn cơ sở dữ liệu: " + cause.getMessage());
                    } else if (cause instanceof UnsupportedOperationException) {
                        showErrorMessage(cause.getMessage());
                    } else if (cause instanceof NullPointerException) {
                        showErrorMessage("Lỗi dữ liệu không hợp lệ (NullPointerException) khi xử lý báo cáo.");
                        cause.printStackTrace();
                    } else {
                        showErrorMessage("Đã xảy ra lỗi khi tải báo cáo: " + cause.getMessage());
                    }
                    cause.printStackTrace();
                } catch (Exception ex) {
                    showErrorMessage("Đã xảy ra lỗi không mong muốn: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void displayReportData(String reportType, List<?> data) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (data == null || data.isEmpty()) {
            showInfoMessage("Không có dữ liệu cho báo cáo '" + reportType + "'.");
            exportButton.setEnabled(false);
            return;
        }

        try {
            switch (reportType) {
                case "Danh sách Sinh viên":
                    displayStudentList((List<Student>) data);
                    break;
                case "Danh sách Môn học":
                    displayCourseList((List<Course>) data);
                    break;
                case "Bảng điểm Tổng hợp":
                    displayGradeList((List<Grade>) data);
                    break;
                case "Thống kê Sinh viên theo Lớp":
                    displayStudentCountByClassStats((List<Map<String, Object>>) data);
                    break;
                case "Thống kê Điểm trung bình theo Môn":
                    displayAverageScoreByCourseStats((List<Map<String, Object>>) data);
                    break;
                default:
                    showWarningMessage("Loại báo cáo không xác định để hiển thị: " + reportType);
                    return;
            }
            exportButton.setEnabled(true);
        } catch (ClassCastException cce) {
            showErrorMessage("Lỗi kiểu dữ liệu không mong đợi trả về từ Service cho báo cáo: " + reportType);
            cce.printStackTrace();
            exportButton.setEnabled(false);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi hiển thị dữ liệu báo cáo: " + e.getMessage());
            e.printStackTrace();
            exportButton.setEnabled(false);
        }
    }

    private void displayStudentList(List<Student> students) {
        String[] studentColumns = {"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Email", "Số ĐT", "Mã lớp", "Trạng thái"};
        tableModel.setColumnIdentifiers(studentColumns);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getName(), (s.getDob() != null) ? sdf.format(s.getDob()) : "",
                    s.getGender(), s.getAddress(), s.getEmail(), s.getPhone(), s.getClassId(), s.getStatus()
            });
        }
    }

    private void displayCourseList(List<Course> courses) {
        String[] courseColumns = {"Mã môn", "Tên môn", "Số tín chỉ"};
        tableModel.setColumnIdentifiers(courseColumns);
        for (Course c : courses) {
            tableModel.addRow(new Object[]{c.getCourseId(), c.getCourseName(), c.getCredits()});
        }
    }

    private void displayGradeList(List<Grade> grades) {
        String[] gradeColumns = {"Mã Sinh viên", "Mã Môn học", "Điểm"};
        tableModel.setColumnIdentifiers(gradeColumns);
        for (Grade g : grades) {
            tableModel.addRow(new Object[]{g.getStudentId(), g.getCourseId(), String.format("%.2f", g.getScore())});
        }
    }

    private void displayStudentCountByClassStats(List<Map<String, Object>> classStats) {
        String[] statsColumns = {"Mã Lớp", "Tên Lớp", "Số lượng Sinh viên"};
        tableModel.setColumnIdentifiers(statsColumns);
        for (Map<String, Object> stat : classStats) {
            tableModel.addRow(new Object[]{
                    stat.getOrDefault("classId", ""),
                    stat.getOrDefault("className", ""),
                    stat.getOrDefault("studentCount", 0L)
            });
        }
    }

    private void displayAverageScoreByCourseStats(List<Map<String, Object>> courseStats) {
        String[] statsColumns = {"Mã Môn học", "Tên Môn học", "Điểm Trung bình"};
        tableModel.setColumnIdentifiers(statsColumns);
        for (Map<String, Object> stat : courseStats) {
            Object avgScoreObj = stat.get("averageScore");
            String avgScoreFormatted = "N/A";
            if (avgScoreObj != null) {
                try {
                    double avgScore = (avgScoreObj instanceof Number) ?
                            ((Number) avgScoreObj).doubleValue() :
                            Double.parseDouble(avgScoreObj.toString());
                    avgScoreFormatted = String.format("%.2f", avgScore);
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi định dạng điểm TB cho môn: " + stat.get("courseId"));
                }
            }
            tableModel.addRow(new Object[]{
                    stat.getOrDefault("courseId", ""),
                    stat.getOrDefault("courseName", ""),
                    avgScoreFormatted
            });
        }
    }

    private void exportReport(ActionEvent e) {
        if (currentReportData == null || currentReportData.isEmpty() || currentReportType.isEmpty() || currentReportType.equals("-- Chọn báo cáo --")) {
            showWarningMessage("Chưa có dữ liệu báo cáo để xuất hoặc chưa chọn loại báo cáo.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file báo cáo CSV");
        String suggestedFileName = currentReportType.replace(" ", "_").replace("/", "-") + ".csv";
        fileChooser.setSelectedFile(new File(suggestedFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            String finalFilePath = filePath;
            SwingWorker<Void, Void> exportWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    switch (currentReportType) {
                        case "Danh sách Sinh viên":
                            reportGenerator.generateStudentReport((List<Student>) currentReportData, finalFilePath);
                            break;
                        case "Danh sách Môn học":
                            reportGenerator.generateCourseReport((List<Course>) currentReportData, finalFilePath);
                            break;
                        case "Bảng điểm Tổng hợp":
                            reportGenerator.generateGradeReport((List<Grade>) currentReportData, finalFilePath);
                            break;
                        case "Thống kê Sinh viên theo Lớp":
                            reportGenerator.generateStudentCountByClassReport((List<Map<String, Object>>) currentReportData, finalFilePath);
                            break;
                        case "Thống kê Điểm trung bình theo Môn":
                            reportGenerator.generateAverageScoreByCourseReport((List<Map<String, Object>>) currentReportData, finalFilePath);
                            break;
                        default:
                            throw new UnsupportedOperationException("Chức năng xuất file cho loại báo cáo '" + currentReportType + "' chưa được hỗ trợ.");
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        showSuccessMessage("Xuất báo cáo thành công!\nĐã lưu tại: " + finalFilePath);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        showErrorMessage("Quá trình xuất báo cáo bị gián đoạn.");
                    } catch (ExecutionException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof IOException) {
                            showErrorMessage("Lỗi khi ghi file báo cáo: " + cause.getMessage());
                        } else if (cause instanceof UnsupportedOperationException) {
                            showWarningMessage(cause.getMessage());
                        } else {
                            showErrorMessage("Lỗi không xác định khi xuất file: " + cause.getMessage());
                        }
                        cause.printStackTrace();
                    } catch (Exception ex) {
                        showErrorMessage("Lỗi không mong muốn khi xuất file: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            exportWorker.execute();
        }
    }

    // --- UI Styling Helper Methods ---

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR_DARK);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_NORMAL_BG);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(BUTTON_MARGIN);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_BG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_NORMAL_BG);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(BUTTON_PRESSED_BG);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(BUTTON_HOVER_BG);
                } else {
                    button.setBackground(BUTTON_NORMAL_BG);
                }
            }
        });

        return button;
    }

    // --- Message Dialog Methods ---

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    // --- Main method (Chỉ dùng để test độc lập) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Reports Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new ReportsPanel());
            frame.setVisible(true);
        });
    }
}