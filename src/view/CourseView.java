package view;

import model.Course;
// import model.User; // Keep if needed for future role-based access control
import service.CourseService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // Needed for alternating rows
import javax.swing.table.TableColumnModel; // Optional: for column width
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import java.net.URL; // No longer needed for icons
import java.sql.SQLException;
import java.util.List;

public class CourseView extends JFrame {

    // --- Styling Constants (Consistent Theme - No Icon Paths) ---
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

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font TITLE_BORDER_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Border TEXT_FIELD_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 8, 5, 8) // Top, Left, Bottom, Right padding
    );
    private static final Insets BUTTON_MARGIN = new Insets(6, 15, 6, 15);

    // --- UI Components ---
    private JTextField courseIdField, courseNameField, creditsField;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, searchButton, clearButton, backButton;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private CourseService courseService;
    private JFrame previousView; // Reference to the previous screen

    public CourseView(JFrame previous) {
        this.previousView = previous;
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

        setTitle("Quản lý Môn học");
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- Panel Form Nhập liệu (NORTH) ---
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Trung tâm (Tìm kiếm và Bảng) ---
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // --- Panel Nút Chức năng (SOUTH) ---
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action listeners ---
        setupActionListeners();

        // --- Load initial data ---
        refreshTable();
    }

    // --- Panel Creation Methods ---

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Thông tin Môn học ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        formPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Mã môn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        courseIdField = createStyledTextField();
        formPanel.add(courseIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Tên môn:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.6;
        courseNameField = createStyledTextField();
        formPanel.add(courseNameField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Số tín chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        creditsField = createStyledTextField();
        formPanel.add(creditsField, gbc);

        return formPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(createStyledLabel("Tìm kiếm:"));
        searchField = createStyledTextField(25);
        searchButton = createStyledButton("Tìm"); // No icon filename
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = { "Mã môn", "Tên môn", "Số tín chỉ" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel) {
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
        configureTableAppearance();

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFields();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(courseTable);
        tableScrollPane.setBorder(new LineBorder(BORDER_COLOR));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private void configureTableAppearance() {
        courseTable.setFont(TABLE_FONT);
        courseTable.setRowHeight(28);
        courseTable.setGridColor(TABLE_GRID_COLOR);
        courseTable.setShowGrid(true);
        courseTable.setIntercellSpacing(new Dimension(0, 0));

        courseTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        courseTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        courseTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        courseTable.getTableHeader().setOpaque(false);
        courseTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));

        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setFillsViewportHeight(true);

        // Optional: Set column widths if needed
    }


    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        addButton = createStyledButton("Thêm");       // No icon filename
        updateButton = createStyledButton("Sửa");     // No icon filename
        deleteButton = createStyledButton("Xóa");     // No icon filename
        clearButton = createStyledButton("Xóa Form"); // No icon filename
        backButton = createStyledButton("Quay lại");  // No icon filename

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // --- Action Listener Setup ---

    private void setupActionListeners() {
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        searchButton.addActionListener(e -> searchCourses());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            if (previousView != null) {
                previousView.setVisible(true);
            }
            dispose();
        });

        searchField.addActionListener(e -> searchCourses());
    }


    // --- Logic Methods (addCourse, updateCourse, deleteCourse, etc.) ---
    // Logic remains the same as the previous version.
    // Included here for completeness.

    private void addCourse() {
        try {
            Course course = createCourseFromForm();
            if (courseService.getCourseById(course.getCourseId()) != null) {
                showErrorMessage("Mã môn học '" + course.getCourseId() + "' đã tồn tại.");
                return;
            }
            courseService.addCourse(course);
            showSuccessMessage("Thêm môn học thành công!");
            refreshTable();
            clearForm();
        } catch (NumberFormatException nfe) {
            showErrorMessage(nfe.getMessage());
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi thêm: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn một môn học từ bảng để sửa.");
            return;
        }
        String originalCourseId = tableModel.getValueAt(selectedRow, 0).toString();

        try {
            Course course = createCourseFromForm();
            // Assuming updateCourse uses the ID from the course object passed
            courseService.updateCourse(course);
            showSuccessMessage("Cập nhật môn học thành công!");
            refreshTable();
            clearForm();
        } catch (NumberFormatException nfe) {
            showErrorMessage(nfe.getMessage());
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi cập nhật: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn một môn học từ bảng để xóa.");
            return;
        }
        String courseId = tableModel.getValueAt(selectedRow, 0).toString();
        String courseName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa môn học:\nMã môn: " + courseId + "\nTên môn: " + courseName + "\n",
                "Xác nhận xóa môn học",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteCourse(courseId);
                showSuccessMessage("Xóa môn học thành công!");
                refreshTable();
                clearForm();
            } catch (SQLException se) {
                if (se.getMessage().toLowerCase().contains("foreign key constraint")) {
                    showErrorMessage(
                            "Không thể xóa môn học '" + courseName + "' vì đang có dữ liệu điểm số hoặc lớp học liên quan.\n" +
                                    "Vui lòng xóa các dữ liệu liên quan trước."
                    );
                } else {
                    showErrorMessage("Lỗi cơ sở dữ liệu khi xóa: " + se.getMessage());
                }
                se.printStackTrace();
            } catch (Exception ex) {
                showErrorMessage("Lỗi không xác định khi xóa: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void searchCourses() {
        try {
            String keyword = searchField.getText().trim();
            List<Course> courses = courseService.searchCourses(keyword);
            if (courses.isEmpty() && !keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy môn học nào khớp với '" + keyword + "'.",
                        "Không tìm thấy", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshTable(courses);
        } catch (SQLException e) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi tìm kiếm: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Course createCourseFromForm() throws NumberFormatException, Exception {
        String courseId = courseIdField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (courseId.isEmpty()) {
            throw new Exception("Mã môn học không được để trống.");
        }
        if (courseName.isEmpty()) {
            throw new Exception("Tên môn học không được để trống.");
        }
        if (creditsText.isEmpty()) {
            throw new Exception("Số tín chỉ không được để trống.");
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
            if (credits <= 0 || credits > 20) {
                throw new NumberFormatException("Số tín chỉ phải là số nguyên dương (ví dụ: 1-20).");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Số tín chỉ không hợp lệ. Vui lòng nhập một số nguyên dương.");
        }

        return new Course(courseId, courseName, credits);
    }


    private void refreshTable() {
        try {
            List<Course> courses = courseService.getAllCourses();
            refreshTable(courses);
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải danh sách môn học: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshTable(List<Course> courses) {
        tableModel.setRowCount(0);
        if (courses != null) {
            for (Course c : courses) {
                if (c != null) {
                    tableModel.addRow(new Object[]{
                            c.getCourseId(),
                            c.getCourseName(),
                            c.getCredits()
                    });
                }
            }
        }
    }

    private void clearForm() {
        courseIdField.setText("");
        courseNameField.setText("");
        creditsField.setText("");
        courseTable.clearSelection();
        courseIdField.requestFocus();
    }

    private void populateFields() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            courseIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            courseNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            creditsField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        }
    }

    // --- UI Styling Helper Methods ---

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

    // Updated createStyledButton without icon parameters/logic
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_NORMAL_BG);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(BUTTON_MARGIN);
        button.setFocusPainted(false);

        // Hover/Press Effects (kept)
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

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }


    // --- Main method (for testing) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseView courseManagementView = new CourseView(null);
            courseManagementView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            courseManagementView.setVisible(true);
        });
    }
}