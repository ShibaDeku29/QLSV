package view;

import model.Grade;
import model.User; // Import User
import service.GradeService;
import service.StudentService;
import service.CourseService;
import model.Student;
import model.Course;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // Needed for alternating rows
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer; // For alignment
import javax.swing.*;
import javax.swing.border.*; // Import all borders
import java.awt.*;
import java.awt.event.ItemEvent; // Import ItemEvent
import java.awt.event.MouseAdapter; // For button effects
import java.awt.event.MouseEvent;  // For button effects
import java.sql.SQLException;
import java.util.List;
// No icon imports needed

public class GradeView extends JFrame {

    // --- Styling Constants (Consistent Theme - Copied from previous views) ---
    private static final Color PRIMARY_COLOR_LIGHT = new Color(238, 242, 247); // Light background
    private static final Color PRIMARY_COLOR_DARK = new Color(50, 50, 90);    // Dark text/elements
    private static final Color BORDER_COLOR = new Color(200, 200, 220);      // Subtle borders
    private static final Color BUTTON_NORMAL_BG = new Color(100, 149, 237); // Cornflower Blue
    private static final Color BUTTON_HOVER_BG = new Color(135, 206, 250);  // Lighter Blue (SkyBlue)
    private static final Color BUTTON_PRESSED_BG = new Color(70, 130, 180);   // SteelBlue
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color TABLE_HEADER_BG = new Color(210, 218, 226);
    private static final Color TABLE_GRID_COLOR = new Color(220, 220, 220);
    private static final Color TABLE_ALT_ROW_COLOR = new Color(245, 248, 251);
    private static final Color GPA_LABEL_COLOR = new Color(0, 100, 0); // Dark Green for GPA/Rank

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font TITLE_BORDER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INFO_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font INFO_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 13);


    private static final Border TEXT_FIELD_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 8, 5, 8) // Top, Left, Bottom, Right padding
    );
    private static final Insets BUTTON_MARGIN = new Insets(6, 15, 6, 15);

    // --- UI Components ---
    // Giữ nguyên kiểu dữ liệu String cho ComboBox như code gốc
    private JComboBox<String> studentComboBox, courseComboBox;
    private JTextField scoreField;
    private JButton addButton, updateButton, deleteButton, calculateGPAButton, viewStudentGradesButton, clearButton, backButton;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private GradeService gradeService;
    private StudentService studentService;
    private CourseService courseService;
    private JLabel gpaLabel, rankLabel;
    private User currentUser;
    private JFrame previousView; // Reference to the previous screen

    // Constructor gốc (đã sửa tên tham số)
    public GradeView(User user, JFrame previous) { // Sử dụng tên `previous` thay vì `currentDashboard`
        this.currentUser = user;
        this.previousView = previous; // Gán đúng tham số được truyền vào
        gradeService = new GradeService();
        studentService = new StudentService();
        courseService = new CourseService();

        // --- Apply Nimbus Look and Feel ---
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

        setTitle("Quản lý Điểm số");
        setSize(850, 650); // Adjusted size
        setMinimumSize(new Dimension(750, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15)); // Gaps
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding
        getContentPane().setBackground(Color.WHITE); // Background

        // --- Panel Form Nhập liệu (NORTH) ---
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Trung tâm (Bảng và GPA) ---
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // --- Panel Nút chức năng (SOUTH) ---
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action listeners ---
        setupActionListeners(); // Gọi hàm cài đặt listener

        // --- Load initial data ---
        loadStudents(); // Giữ nguyên logic load ID String
        loadCourses();  // Giữ nguyên logic load ID String
        refreshTable(); // Refresh theo logic gốc
    }

    // --- Panel Creation Methods (Đã được style) ---

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Nhập/Sửa Điểm ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        formPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 15, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Sinh viên, Môn học
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Sinh viên:"), gbc); // Label đã style
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        studentComboBox = new JComboBox<>(); // Vẫn dùng JComboBox<String>
        studentComboBox.setFont(INPUT_FONT); // Áp dụng font
        formPanel.add(studentComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Môn học:"), gbc); // Label đã style
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5;
        courseComboBox = new JComboBox<>(); // Vẫn dùng JComboBox<String>
        courseComboBox.setFont(INPUT_FONT); // Áp dụng font
        formPanel.add(courseComboBox, gbc);

        // Row 2: Điểm, Nút Xem điểm SV
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Điểm số:"), gbc); // Label đã style
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        scoreField = createStyledTextField(); // TextField đã style
        formPanel.add(scoreField, gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        viewStudentGradesButton = createStyledButton("Xem Điểm SV"); // Button đã style (ko icon)
        formPanel.add(viewStudentGradesButton, gbc);

        return formPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setOpaque(false);

        // Table Panel
        // Giữ nguyên các cột của bảng gốc
        String[] columns = {"Mã Sinh viên", "Mã Môn học", "Điểm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradeTable = new JTable(tableModel) {
            // Áp dụng màu xen kẽ
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW_COLOR);
                } else {
                    c.setBackground(super.prepareRenderer(renderer, row, column).getBackground());
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        };
        configureTableAppearance(); // Gọi hàm cấu hình bảng (mới)

        // Giữ nguyên listener gốc để điền form
        gradeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Sử dụng lại logic populateFields gốc (được giữ nguyên)
                populateFields();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(gradeTable);
        tableScrollPane.setBorder(new LineBorder(BORDER_COLOR)); // Thêm border
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Info Panel (GPA, Rank, Calculate Button) - Đã được style
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.setBackground(PRIMARY_COLOR_LIGHT);
        infoPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        infoPanel.add(createStyledLabel("GPA Tổng kết:"));
        infoPanel.add(Box.createHorizontalStrut(5));
        gpaLabel = new JLabel("0.00"); // Format ban đầu
        gpaLabel.setFont(INFO_VALUE_FONT);
        gpaLabel.setForeground(GPA_LABEL_COLOR);
        infoPanel.add(gpaLabel);

        infoPanel.add(Box.createHorizontalStrut(25));

        infoPanel.add(createStyledLabel("Xếp loại:"));
        infoPanel.add(Box.createHorizontalStrut(5));
        rankLabel = new JLabel("Chưa có");
        rankLabel.setFont(INFO_VALUE_FONT);
        rankLabel.setForeground(GPA_LABEL_COLOR);
        infoPanel.add(rankLabel);

        infoPanel.add(Box.createHorizontalGlue());

        calculateGPAButton = createStyledButton("Tính GPA"); // Button đã style
        infoPanel.add(calculateGPAButton);

        centerPanel.add(infoPanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    // Hàm cấu hình giao diện bảng (mới)
    private void configureTableAppearance() {
        gradeTable.setFont(TABLE_FONT);
        gradeTable.setRowHeight(28);
        gradeTable.setGridColor(TABLE_GRID_COLOR);
        gradeTable.setShowGrid(true);
        gradeTable.setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        gradeTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        gradeTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        gradeTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        gradeTable.getTableHeader().setOpaque(false);
        gradeTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));

        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.setFillsViewportHeight(true);

        // Căn giữa cột Điểm (cột thứ 3, index 2)
        TableColumnModel columnModel = gradeTable.getColumnModel();
        if (columnModel.getColumnCount() > 2) { // Đảm bảo cột tồn tại
            columnModel.getColumn(2).setCellRenderer(new CenterAlignRenderer());
            columnModel.getColumn(2).setPreferredWidth(60); // Điều chỉnh độ rộng cột điểm
        }
        if (columnModel.getColumnCount() > 0) {
            columnModel.getColumn(0).setPreferredWidth(100); // Mã SV
        }
        if (columnModel.getColumnCount() > 1) {
            columnModel.getColumn(1).setPreferredWidth(100); // Mã MH
        }
    }

    // Renderer căn giữa (mới)
    static class CenterAlignRenderer extends DefaultTableCellRenderer {
        public CenterAlignRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW_COLOR);
            }
            // Format score to 1 decimal place if it's a number
            if (value instanceof Number) {
                setText(String.format("%.1f", ((Number) value).floatValue()));
            }
            return c;
        }
    }

    // Panel nút chức năng (đã style)
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        addButton = createStyledButton("Thêm Điểm"); // Sử dụng button đã style
        updateButton = createStyledButton("Sửa Điểm");
        deleteButton = createStyledButton("Xóa Điểm");
        clearButton = createStyledButton("Xóa Form");
        backButton = createStyledButton("Quay lại");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // --- Action Listener Setup (Giữ nguyên logic gọi hàm gốc) ---
    private void setupActionListeners() {
        addButton.addActionListener(e -> addGrade()); // Gọi hàm logic gốc
        updateButton.addActionListener(e -> updateGrade()); // Gọi hàm logic gốc
        deleteButton.addActionListener(e -> deleteGrade()); // Gọi hàm logic gốc
        calculateGPAButton.addActionListener(e -> calculateAndDisplayGPA()); // Gọi hàm logic gốc
        viewStudentGradesButton.addActionListener(e -> refreshTable()); // Gọi hàm logic gốc
        clearButton.addActionListener(e -> clearForm()); // Gọi hàm logic gốc
        backButton.addActionListener(e -> {
            if (previousView != null) {
                previousView.setVisible(true);
            }
            dispose();
        });

        studentComboBox.addItemListener(e -> { // Giữ nguyên listener gốc
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshTable();
                gpaLabel.setText("0.0"); // Giữ nguyên reset gốc
                rankLabel.setText("Chưa có"); // Giữ nguyên reset gốc
            }
        });
    }


    // --- Logic Methods (GIỮ NGUYÊN TỪ CODE GỐC BẠN CUNG CẤP) ---

    private void addGrade() {
        try {
            Grade grade = createGradeFromForm(); // Dùng hàm gốc
            gradeService.addGrade(grade);
            showSuccessMessage("Thêm điểm số thành công!");
            refreshTable(); // Dùng hàm gốc
        } catch (NumberFormatException nfe) {
            showErrorMessage("Điểm số phải là một số hợp lệ.");
        } catch (IllegalArgumentException iae) {
            showErrorMessage(iae.getMessage());
        } catch (SQLException se) {
            // Xử lý lỗi duplicate key (nếu có)
            if (se.getSQLState().equals("23000") || se.getMessage().toLowerCase().contains("duplicate entry")) {
                showErrorMessage("Sinh viên đã có điểm cho môn học này. Vui lòng dùng chức năng Sửa.");
            } else {
                showErrorMessage("Lỗi cơ sở dữ liệu: " + se.getMessage());
            }
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateGrade() {
        try {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow < 0) {
                showWarningMessage("Vui lòng chọn một dòng điểm trong bảng để sửa.");
                return;
            }
            Grade grade = createGradeFromForm(); // Dùng hàm gốc
            gradeService.updateGrade(grade);
            showSuccessMessage("Cập nhật điểm số thành công!");
            refreshTable(); // Dùng hàm gốc
        } catch (NumberFormatException nfe) {
            showErrorMessage("Điểm số phải là một số hợp lệ.");
        } catch (IllegalArgumentException iae) {
            showErrorMessage(iae.getMessage());
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteGrade() {
        try {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow < 0) {
                showWarningMessage("Vui lòng chọn một dòng điểm trong bảng để xóa.");
                return;
            }
            String studentId = tableModel.getValueAt(selectedRow, 0).toString();
            String courseId = tableModel.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa điểm môn '" + courseId + "' của sinh viên '" + studentId + "'?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                gradeService.deleteGrade(studentId, courseId);
                showSuccessMessage("Xóa điểm số thành công!");
                refreshTable(); // Dùng hàm gốc
                clearForm(); // Dùng hàm gốc
            }
        } catch (IllegalArgumentException iae) {
            showErrorMessage(iae.getMessage());
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi xóa: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi xóa: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void calculateAndDisplayGPA() {
        Object selectedStudentItem = studentComboBox.getSelectedItem();
        // Giữ nguyên kiểm tra null/placeholder gốc
        if (selectedStudentItem == null || selectedStudentItem.toString().startsWith("--")) {
            showWarningMessage("Vui lòng chọn một sinh viên từ danh sách.");
            return;
        }
        String studentId = selectedStudentItem.toString(); // Lấy ID String

        try {
            double gpa = gradeService.calculateGPA(studentId);
            String rank = gradeService.rankStudent(studentId); // Giữ nguyên hàm rank
            gpaLabel.setText(String.format("%.2f", gpa)); // Format GPA
            rankLabel.setText(rank);
        } catch (IllegalArgumentException iae) {
            showErrorMessage(iae.getMessage());
            gpaLabel.setText("Lỗi"); rankLabel.setText("Lỗi");
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi tính GPA: " + se.getMessage());
            se.printStackTrace();
            gpaLabel.setText("Lỗi"); rankLabel.setText("Lỗi");
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi tính GPA: " + ex.getMessage());
            ex.printStackTrace();
            gpaLabel.setText("Lỗi"); rankLabel.setText("Lỗi");
        }
    }

    // Giữ nguyên hàm createGradeFromForm gốc
    private Grade createGradeFromForm() throws NumberFormatException, Exception {
        Object selectedStudentItem = studentComboBox.getSelectedItem();
        Object selectedCourseItem = courseComboBox.getSelectedItem();
        String scoreText = scoreField.getText().trim();

        // Giữ nguyên kiểm tra null/placeholder gốc
        if (selectedStudentItem == null || selectedCourseItem == null || selectedStudentItem.toString().startsWith("--") || selectedCourseItem.toString().startsWith("--")) {
            throw new Exception("Vui lòng chọn sinh viên và môn học!");
        }
        if (scoreText.isEmpty()) {
            throw new Exception("Vui lòng nhập điểm số!");
        }

        String studentId = selectedStudentItem.toString(); // Lấy ID String
        String courseId = selectedCourseItem.toString();   // Lấy ID String

        float score;
        try {
            score = Float.parseFloat(scoreText);
            // Thêm lại validation điểm số (nếu cần thiết và logic gốc có)
            if (score < 0 || score > 10) {
                throw new NumberFormatException("Điểm số phải từ 0 đến 10.");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Điểm số phải là một số hợp lệ (0-10).");
        }

        // Giữ nguyên constructor gốc Grade(String, String, float)
        return new Grade(studentId, courseId, score);
    }

    // Giữ nguyên hàm loadStudents gốc (load String ID)
    private void loadStudents() {
        try {
            studentComboBox.removeAllItems();
            // studentComboBox.addItem("-- Chọn Sinh viên --"); // Bỏ placeholder nếu không dùng trong logic gốc
            List<Student> students = studentService.getAllStudents();
            if (students != null) {
                for (Student s : students) {
                    studentComboBox.addItem(s.getId()); // Thêm ID String
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải danh sách sinh viên: " + e.getMessage());
        }
    }

    // Giữ nguyên hàm loadCourses gốc (load String ID)
    private void loadCourses() {
        try {
            courseComboBox.removeAllItems();
            // courseComboBox.addItem("-- Chọn Môn học --"); // Bỏ placeholder nếu không dùng
            List<Course> courses = courseService.getAllCourses();
            if (courses != null) {
                for (Course c : courses) {
                    courseComboBox.addItem(c.getCourseId()); // Thêm ID String
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải danh sách môn học: " + e.getMessage());
        }
    }

    // Giữ nguyên hàm refreshTable gốc (chỉ hiển thị ID, Điểm)
    private void refreshTable() {
        tableModel.setRowCount(0);
        Object selectedStudentItem = studentComboBox.getSelectedItem();

        // Giữ nguyên kiểm tra null/placeholder gốc
        if (selectedStudentItem != null && !selectedStudentItem.toString().startsWith("--")) {
            String studentId = selectedStudentItem.toString(); // Lấy ID String
            try {
                List<Grade> grades = gradeService.getGradesByStudent(studentId);
                if (grades != null) {
                    for (Grade g : grades) {
                        // Chỉ thêm 3 cột như bảng gốc
                        tableModel.addRow(new Object[]{
                                g.getStudentId(),
                                g.getCourseId(),
                                g.getScore()
                        });
                    }
                }
                // Giữ nguyên việc gọi calculateAndDisplayGPA ở đây
                calculateAndDisplayGPA();

            } catch (SQLException e) {
                showErrorMessage("Lỗi khi tải danh sách điểm: " + e.getMessage());
                e.printStackTrace();
                gpaLabel.setText("Lỗi"); rankLabel.setText("Lỗi");
            } catch (IllegalArgumentException iae) {
                showErrorMessage(iae.getMessage());
                gpaLabel.setText("Lỗi"); rankLabel.setText("Lỗi");
            }
        } else {
            // Giữ nguyên việc reset GPA khi không chọn SV
            gpaLabel.setText("0.0");
            rankLabel.setText("Chưa có");
        }
    }

    // Giữ nguyên hàm clearForm gốc
    private void clearForm() {
        scoreField.setText("");
        gradeTable.clearSelection();
        scoreField.requestFocus();
        // Không reset ComboBoxes
    }

    // Giữ nguyên hàm populateFields gốc (dùng setSelectedItem với String)
    private void populateFields() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow >= 0) {
            String studentId = tableModel.getValueAt(selectedRow, 0).toString();
            String courseId = tableModel.getValueAt(selectedRow, 1).toString(); // Cột 1 trong bảng gốc
            scoreField.setText(tableModel.getValueAt(selectedRow, 2).toString()); // Cột 2 trong bảng gốc
            studentComboBox.setSelectedItem(studentId); // Chọn item là String ID
            courseComboBox.setSelectedItem(courseId);   // Chọn item là String ID
        }
    }


    // --- UI Styling Helper Methods (Đã được thêm) ---

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR_DARK);
        return label;
    }

    private JTextField createStyledTextField() {
        return createStyledTextField(0);
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = (columns > 0) ? new JTextField(columns) : new JTextField();
        textField.setFont(INPUT_FONT);
        textField.setBorder(TEXT_FIELD_BORDER);
        return textField;
    }

    // Icon-less button creation
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
            public void mouseEntered(MouseEvent e) { button.setBackground(BUTTON_HOVER_BG); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(BUTTON_NORMAL_BG); }
            @Override
            public void mousePressed(MouseEvent e) { button.setBackground(BUTTON_PRESSED_BG); }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) { button.setBackground(BUTTON_HOVER_BG); }
                else { button.setBackground(BUTTON_NORMAL_BG); }
            }
        });
        return button;
    }


    // --- Message Dialog Methods (Giữ nguyên) ---
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, "Lỗi: " + message, "Đã có lỗi xảy ra", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }


    // --- Main method (Giữ nguyên như code gốc bạn cung cấp) ---
    public static void main(String[] args) {
        // Giữ nguyên cách set L&F trong main của code gốc
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User testUser = new User("U001", "admin", "admin123", "admin", null);
        // Giữ nguyên cách truyền null cho màn hình trước đó trong main gốc
        JFrame currentDashboard = null;
        SwingUtilities.invokeLater(() -> new GradeView(testUser, currentDashboard).setVisible(true));
    }
}