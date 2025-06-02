package view;

import model.Student;
// import model.User; // Keep if needed
import service.StudentService;

import javax.swing.*;
import javax.swing.border.*; // Import all borders
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // For alternating rows
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer; // For alignment
import java.awt.*;
import java.awt.event.MouseAdapter; // For button effects
import java.awt.event.MouseEvent;  // For button effects
import java.sql.SQLException;
import java.text.ParseException; // Import ParseException
import java.text.SimpleDateFormat;
import java.util.Date; // Import Date
import java.util.List;
// No icon imports needed

public class StudentView extends JFrame {

    // --- Styling Constants (Consistent Theme) ---
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
    private JTextField idField, nameField, dobField, genderField, addressField, emailField, phoneField, classIdField, statusField;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, searchButton, clearButton, backButton;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private JFrame previousView;

    public StudentView(JFrame previous) {
        this.previousView = previous; // Correctly assign previous view reference
        studentService = new StudentService();

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

        setTitle("Quản lý Sinh viên");
        setSize(950, 700); // Increased size for more fields
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15)); // Gaps
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding
        getContentPane().setBackground(Color.WHITE); // Background

        // --- Panel Form Nhập liệu (NORTH) ---
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // --- Panel Trung tâm (Tìm kiếm và Bảng) ---
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // --- Panel Nút Chức năng (SOUTH) ---
        // Moved button panel creation here, added to SOUTH
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action listeners ---
        setupActionListeners();

        // --- Load initial data ---
        refreshTable(); // Initial load
    }

    // --- Panel Creation Methods (Styled) ---

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR_LIGHT);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Thông tin Sinh viên ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        // Add inner padding to the titled border panel
        formPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8); // Consistent spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int gridy = 0; // Row counter

        // Row 1: Mã SV, Họ tên
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Mã SV:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; // Distribute weight
        idField = createStyledTextField();
        formPanel.add(idField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Họ tên:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        nameField = createStyledTextField();
        formPanel.add(nameField, gbc);
        gridy++;

        // Row 2: Ngày sinh, Giới tính
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Ngày sinh (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        dobField = createStyledTextField();
        formPanel.add(dobField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Giới tính:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        genderField = createStyledTextField(); // Keep as JTextField as per original
        formPanel.add(genderField, gbc);
        gridy++;

        // Row 3: Địa chỉ (spans multiple columns)
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; // Span 3 columns
        addressField = createStyledTextField();
        formPanel.add(addressField, gbc);
        gbc.gridwidth = 1; // Reset gridwidth
        gridy++;

        // Row 4: Email, Số điện thoại
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        emailField = createStyledTextField();
        formPanel.add(emailField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        phoneField = createStyledTextField();
        formPanel.add(phoneField, gbc);
        gridy++;

        // Row 5: Mã lớp, Trạng thái
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Mã lớp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        classIdField = createStyledTextField();
        formPanel.add(classIdField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createStyledLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        statusField = createStyledTextField();
        statusField.setText("Active"); // Default value
        formPanel.add(statusField, gbc);
        // gridy++; // Increment if adding more rows

        return formPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10)); // Gap between search and table
        centerPanel.setOpaque(false); // Show frame background

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(createStyledLabel("Tìm kiếm:"));
        searchField = createStyledTextField(30); // Wider search field
        searchButton = createStyledButton("Tìm"); // Styled, no icon
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Email", "Số ĐT", "Mã lớp", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel) {
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
        configureTableAppearance(); // Apply styling

        // Use original listener logic
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFields(); // Call original populate logic
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(new LineBorder(BORDER_COLOR)); // Add border
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private void configureTableAppearance() {
        studentTable.setFont(TABLE_FONT);
        studentTable.setRowHeight(28);
        studentTable.setGridColor(TABLE_GRID_COLOR);
        studentTable.setShowGrid(true);
        studentTable.setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        studentTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        studentTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        studentTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        studentTable.getTableHeader().setOpaque(false);
        studentTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));

        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setFillsViewportHeight(true);

        // Optional: Set column widths (adjust as needed)
        TableColumnModel columnModel = studentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Mã SV
        columnModel.getColumn(1).setPreferredWidth(150); // Họ tên
        columnModel.getColumn(2).setPreferredWidth(90);  // Ngày sinh
        columnModel.getColumn(3).setPreferredWidth(60);  // Giới tính
        columnModel.getColumn(4).setPreferredWidth(200); // Địa chỉ
        columnModel.getColumn(5).setPreferredWidth(150); // Email
        columnModel.getColumn(6).setPreferredWidth(90);  // Số ĐT
        columnModel.getColumn(7).setPreferredWidth(80);  // Mã lớp
        columnModel.getColumn(8).setPreferredWidth(70);  // Trạng thái
        // Optional: Center align specific columns like Gender, Status
        // columnModel.getColumn(3).setCellRenderer(new CenterAlignRenderer());
        // columnModel.getColumn(8).setCellRenderer(new CenterAlignRenderer());
    }

    // Optional: Renderer for center alignment if needed later
    static class CenterAlignRenderer extends DefaultTableCellRenderer {
        public CenterAlignRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) { c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW_COLOR); }
            return c;
        }
    }


    // Create Button Panel (Styled) - Moved out of formPanel
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Align right
        buttonPanel.setOpaque(false); // Show frame background

        addButton = createStyledButton("Thêm");
        updateButton = createStyledButton("Sửa");
        deleteButton = createStyledButton("Xóa");
        clearButton = createStyledButton("Xóa Form");
        backButton = createStyledButton("Quay lại"); // Renamed from "Quay lại Dashboard"

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Space
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // --- Action Listener Setup (Uses original logic methods) ---
    private void setupActionListeners() {
        addButton.addActionListener(e -> addStudent()); // Call original logic
        updateButton.addActionListener(e -> updateStudent()); // Call original logic
        deleteButton.addActionListener(e -> deleteStudent()); // Call original logic
        searchButton.addActionListener(e -> searchStudents()); // Call original logic
        clearButton.addActionListener(e -> clearForm()); // Call original logic
        backButton.addActionListener(e -> {
            if (previousView != null) {
                previousView.setVisible(true);
            }
            dispose();
        });
        // Optional: Add listener for search field Enter key
        searchField.addActionListener(e -> searchStudents());
    }


    // --- Logic Methods (KEPT EXACTLY AS PROVIDED IN THE ORIGINAL CODE) ---

    private void addStudent() {
        try {
            Student student = createStudentFromForm(); // Original method
            // Original code didn't explicitly check for duplicates here, rely on DB or Service
            studentService.addStudent(student);
            // Use styled messages
            showSuccessMessage("Thêm sinh viên thành công!");
            refreshTable(); // Original method
            clearForm(); // Original method
        } catch (ParseException pe) {
            showErrorMessage("Lỗi định dạng ngày sinh (yyyy-MM-dd)."); // Styled message
        } catch (SQLException se) {
            // Handle potential duplicate key error from DB
            if (se.getSQLState().equals("23000") || se.getMessage().toLowerCase().contains("duplicate entry")) {
                showErrorMessage("Lỗi: Mã sinh viên '" + idField.getText() + "' đã tồn tại.");
            } else {
                showErrorMessage("Lỗi cơ sở dữ liệu: " + se.getMessage());
            }
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage()); // Show validation message
            ex.printStackTrace();
        }
    }

    private void updateStudent() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow < 0) {
                showWarningMessage("Vui lòng chọn một sinh viên từ bảng để sửa."); // Styled message
                return;
            }
            Student student = createStudentFromForm(); // Original method
            studentService.updateStudent(student);
            showSuccessMessage("Cập nhật sinh viên thành công!"); // Styled message
            refreshTable(); // Original method
            clearForm(); // Original method
        } catch (ParseException pe) {
            showErrorMessage("Lỗi định dạng ngày sinh (yyyy-MM-dd)."); // Styled message
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu: " + se.getMessage()); // Styled message
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage()); // Styled message
            ex.printStackTrace();
        }
    }

    private void deleteStudent() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow < 0) {
                showWarningMessage("Vui lòng chọn một sinh viên từ bảng để xóa."); // Styled message
                return;
            }
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String name = tableModel.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa sinh viên '" + name + "' (ID: " + id + ")?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); // Added icon

            if (confirm == JOptionPane.YES_OPTION) {
                studentService.deleteStudent(id);
                showSuccessMessage("Xóa sinh viên thành công!"); // Styled message
                refreshTable(); // Original method
                clearForm(); // Original method
            }
        } catch (SQLException se) {
            if (se.getSQLState().startsWith("23") || se.getMessage().toLowerCase().contains("foreign key constraint")) { // Check FK error
                showErrorMessage( // Styled message
                        "Không thể xóa sinh viên này vì có dữ liệu liên quan (ví dụ: điểm số).\n" +
                                "Vui lòng xóa các dữ liệu liên quan trước.");
            } else {
                showErrorMessage("Lỗi cơ sở dữ liệu khi xóa: " + se.getMessage()); // Styled message
            }
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi xóa: " + ex.getMessage()); // Styled message
            ex.printStackTrace();
        }
    }

    private void searchStudents() {
        try {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                refreshTable(); // Original method
            } else {
                List<Student> students = studentService.searchStudents(keyword);
                if (students.isEmpty()) {
                    // Use styled message, but keep original JOptionPane type
                    JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên nào khớp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
                refreshTable(students); // Original method
            }
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi tìm kiếm: " + se.getMessage()); // Styled message
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi tìm kiếm: " + ex.getMessage()); // Styled message
            ex.printStackTrace();
        }
    }

    // Kept original createStudentFromForm exactly
    private Student createStudentFromForm() throws ParseException, Exception {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String dobText = dobField.getText().trim();
        String gender = genderField.getText().trim();
        String address = addressField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String classId = classIdField.getText().trim();
        String status = statusField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || dobText.isEmpty() || classId.isEmpty() || status.isEmpty()) {
            throw new Exception("Vui lòng nhập đầy đủ các trường bắt buộc: Mã SV, Họ tên, Ngày sinh, Mã lớp, Trạng thái!");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        Date dob;
        try {
            dob = sdf.parse(dobText);
        } catch (ParseException pe) {
            throw new ParseException("Định dạng ngày sinh không hợp lệ. Vui lòng nhập theo dạng yyyy-MM-dd.", pe.getErrorOffset());
        }

        if (!email.isEmpty() && !email.contains("@")) {
            throw new Exception("Địa chỉ email không hợp lệ.");
        }
        if (!phone.isEmpty() && !phone.matches("\\d+")) {
            throw new Exception("Số điện thoại chỉ được chứa các chữ số.");
        }

        // Assumes constructor Student(id, name, dob, gender, address, email, phone, classId, status)
        return new Student(id, name, dob, gender, address, email, phone, classId, status);
    }

    // Kept original refreshTable()
    private void refreshTable() {
        try {
            List<Student> students = studentService.getAllStudents();
            refreshTable(students); // Calls the overloaded version
        } catch (SQLException e) {
            // Use styled message
            showErrorMessage("Lỗi khi tải danh sách sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kept original refreshTable(List<Student> students) exactly
    private void refreshTable(List<Student> students) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (students != null) {
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        (s.getDob() != null) ? sdf.format(s.getDob()) : "",
                        s.getGender(),
                        s.getAddress(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getClassId(),
                        s.getStatus()
                });
            }
        }
    }

    // Kept original clearForm exactly
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        dobField.setText("");
        genderField.setText("");
        addressField.setText("");
        emailField.setText("");
        phoneField.setText("");
        classIdField.setText("");
        statusField.setText("Active");
        studentTable.clearSelection();
        idField.requestFocus();
    }

    // Kept original populateFields exactly
    private void populateFields() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            dobField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            genderField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            Object addressValue = tableModel.getValueAt(selectedRow, 4);
            addressField.setText(addressValue != null ? addressValue.toString() : "");
            emailField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            classIdField.setText(tableModel.getValueAt(selectedRow, 7).toString());
            statusField.setText(tableModel.getValueAt(selectedRow, 8).toString());
        }
    }

    // --- UI Styling Helper Methods (Copied from previous styled views) ---

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

    // --- Message Dialog Methods (Using styled versions) ---
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        // Ensure message isn't prefixed with "Lỗi: " twice
        String displayMessage = message.startsWith("Lỗi: ") ? message : "Lỗi: " + message;
        JOptionPane.showMessageDialog(this, displayMessage, "Đã có lỗi xảy ra", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    // --- Main method (Kept as provided in original code) ---
    public static void main(String[] args) {
        // Original main method sets System L&F, but Nimbus will override in constructor
        SwingUtilities.invokeLater(() -> {
            StudentView studentManagementView = new StudentView(null); // Original instantiation
            studentManagementView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            studentManagementView.setVisible(true);
        });
    }
}