package view;

import model.ClassRoom;
// import model.User; // Keep if needed for future role-based access control
import service.ClassRoomService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // Needed for alternating rows
import javax.swing.table.TableColumnModel; // Optional: for column width
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// No icon imports needed
import java.sql.SQLException;
import java.util.List;

public class ClassRoomView extends JFrame {

    // --- Styling Constants (Consistent Theme - Copied from CourseView) ---
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
    private JTextField classIdField, classNameField, teacherField;
    private JTextField searchField;
    private JButton addButton, updateButton, deleteButton, searchButton, clearButton, backButton;
    private JTable classRoomTable;
    private DefaultTableModel tableModel;
    private ClassRoomService classRoomService;
    private JFrame previousView; // Reference to the previous screen

    public ClassRoomView(JFrame previous) {
        this.previousView = previous;
        classRoomService = new ClassRoomService();

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

        setTitle("Quản lý Lớp học");
        setSize(800, 600); // Adjusted size
        setMinimumSize(new Dimension(700, 500)); // Minimum size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15)); // Increased gaps
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding for the whole frame
        getContentPane().setBackground(Color.WHITE); // Set background

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
        // No separate applyStyling() needed, styles applied during creation
    }

    // --- Panel Creation Methods ---

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR_LIGHT); // Light background
        // Styled TitledBorder
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                " Thông tin Lớp học ", // Spaces for padding
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_BORDER_FONT,
                PRIMARY_COLOR_DARK);
        formPanel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10))); // Title + Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); // Consistent spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Mã lớp, Tên lớp
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // Label Mã lớp
        formPanel.add(createStyledLabel("Mã lớp:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4; // Field Mã lớp
        classIdField = createStyledTextField();
        formPanel.add(classIdField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; // Label Tên lớp
        formPanel.add(createStyledLabel("Tên lớp:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.6; // Field Tên lớp (wider)
        classNameField = createStyledTextField();
        formPanel.add(classNameField, gbc);

        // Row 2: Giáo viên CN
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; // Label GVCN
        formPanel.add(createStyledLabel("Giáo viên CN:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.gridwidth = 3; // Field GVCN spans 3 columns
        teacherField = createStyledTextField();
        formPanel.add(teacherField, gbc);
        // gbc.gridwidth = 1; // Reset gridwidth if needed later

        return formPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false); // Show frame background

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(createStyledLabel("Tìm kiếm:"));
        searchField = createStyledTextField(25); // Wider search field
        searchButton = createStyledButton("Tìm"); // No icon
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = { "Mã lớp", "Tên lớp", "Giáo viên chủ nhiệm" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classRoomTable = new JTable(tableModel) {
            // Override prepareRenderer for alternating row colors
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

        classRoomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFields();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(classRoomTable);
        tableScrollPane.setBorder(new LineBorder(BORDER_COLOR)); // Border for scroll pane
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private void configureTableAppearance() {
        classRoomTable.setFont(TABLE_FONT);
        classRoomTable.setRowHeight(28);
        classRoomTable.setGridColor(TABLE_GRID_COLOR);
        classRoomTable.setShowGrid(true);
        classRoomTable.setIntercellSpacing(new Dimension(0, 0));

        // Header Styling
        classRoomTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        classRoomTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        classRoomTable.getTableHeader().setForeground(PRIMARY_COLOR_DARK);
        classRoomTable.getTableHeader().setOpaque(false);
        classRoomTable.getTableHeader().setBorder(new LineBorder(BORDER_COLOR));

        classRoomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classRoomTable.setFillsViewportHeight(true);

        // Optional: Set column widths
        // TableColumnModel columnModel = classRoomTable.getColumnModel();
        // columnModel.getColumn(0).setPreferredWidth(100); // Mã lớp
        // columnModel.getColumn(1).setPreferredWidth(250); // Tên lớp
        // columnModel.getColumn(2).setPreferredWidth(200); // GVCN
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Align buttons right
        buttonPanel.setOpaque(false);

        addButton = createStyledButton("Thêm");
        updateButton = createStyledButton("Sửa");
        deleteButton = createStyledButton("Xóa");
        clearButton = createStyledButton("Xóa Form");
        backButton = createStyledButton("Quay lại"); // Simpler text

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Space before back button
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    // --- Action Listener Setup ---

    private void setupActionListeners() {
        addButton.addActionListener(e -> addClassRoom());
        updateButton.addActionListener(e -> updateClassRoom());
        deleteButton.addActionListener(e -> deleteClassRoom());
        searchButton.addActionListener(e -> searchClassRooms());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> {
            if (previousView != null) {
                previousView.setVisible(true);
            }
            dispose(); // Close this window
        });

        // Add action listener for search field (press Enter to search)
        searchField.addActionListener(e -> searchClassRooms());
    }


    // --- Logic Methods (Mostly unchanged, ensure consistency) ---

    private void addClassRoom() {
        try {
            ClassRoom classRoom = createClassRoomFromForm();
            // Optional: Check for duplicate ID if service doesn't
            // if (classRoomService.getClassRoomById(classRoom.getClassId()) != null) {
            //     showErrorMessage("Mã lớp '" + classRoom.getClassId() + "' đã tồn tại.");
            //     return;
            // }
            classRoomService.addClassRoom(classRoom);
            showSuccessMessage("Thêm lớp học thành công!");
            refreshTable();
            clearForm();
        } catch (SQLException se) {
            // Check specific SQL state for duplicate key violation
            if ("23000".equals(se.getSQLState()) || se.getMessage().toLowerCase().contains("duplicate entry")) {
                showErrorMessage("Lỗi: Mã lớp '" + classIdField.getText() + "' đã tồn tại.");
            } else {
                showErrorMessage("Lỗi cơ sở dữ liệu khi thêm: " + se.getMessage());
            }
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage()); // Display validation message
            ex.printStackTrace();
        }
    }

    private void updateClassRoom() {
        int selectedRow = classRoomTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn một lớp học từ bảng để sửa.");
            return;
        }
        String originalClassId = tableModel.getValueAt(selectedRow, 0).toString();

        try {
            ClassRoom classRoom = createClassRoomFromForm();
            // Important: Decide if classId can be updated.
            // If not, ensure the originalClassId is used for the update call.
            // classRoom.setClassId(originalClassId); // Force original ID if needed

            // Assuming updateClassRoom uses the ID within the passed object
            classRoomService.updateClassRoom(classRoom);
            showSuccessMessage("Cập nhật lớp học thành công!");
            refreshTable();
            clearForm();
        } catch (SQLException se) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi cập nhật: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteClassRoom() {
        int selectedRow = classRoomTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn một lớp học từ bảng để xóa.");
            return;
        }
        String classId = tableModel.getValueAt(selectedRow, 0).toString();
        String className = tableModel.getValueAt(selectedRow, 1).toString();

        // Use styled confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa lớp học:\n" +
                        "Mã lớp: " + classId + "\n" +
                        "Tên lớp: " + className + "\n" +
                        "\nLưu ý: Xóa lớp có thể ảnh hưởng đến sinh viên thuộc lớp này.", // Added warning
                "Xác nhận xóa lớp học",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                classRoomService.deleteClassRoom(classId);
                showSuccessMessage("Xóa lớp học thành công!");
                refreshTable();
                clearForm();
            } catch (SQLException se) {
                // Check for foreign key constraint violation
                if (se.getMessage().toLowerCase().contains("foreign key constraint")) {
                    showErrorMessage(
                            "Không thể xóa lớp học '" + className + "' vì đang có sinh viên hoặc dữ liệu khác liên quan.\n" +
                                    "Vui lòng kiểm tra lại dữ liệu liên kết."
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

    private void searchClassRooms() {
        try {
            String keyword = searchField.getText().trim();
            List<ClassRoom> classRooms = classRoomService.searchClassRooms(keyword);
            if (classRooms.isEmpty() && !keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy lớp học nào khớp với '" + keyword + "'.",
                        "Không tìm thấy", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshTable(classRooms); // Show results or all if keyword is empty
        } catch (SQLException e) {
            showErrorMessage("Lỗi cơ sở dữ liệu khi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định khi tìm kiếm: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // createClassRoomFromForm - Check against your ClassRoom model
    private ClassRoom createClassRoomFromForm() throws Exception {
        String classId = classIdField.getText().trim();
        String className = classNameField.getText().trim();
        String teacher = teacherField.getText().trim(); // Teacher name is optional?

        if (classId.isEmpty()) {
            throw new Exception("Mã lớp không được để trống.");
        }
        if (className.isEmpty()) {
            throw new Exception("Tên lớp không được để trống.");
        }
        // Teacher name can potentially be empty/null depending on requirements

        // Assumes constructor ClassRoom(String id, String name, String teacher)
        return new ClassRoom(classId, className, teacher);
    }


    private void refreshTable() {
        try {
            List<ClassRoom> classRooms = classRoomService.getAllClassRooms();
            refreshTable(classRooms);
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải danh sách lớp học: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // refreshTable - Check getters against your ClassRoom model
    private void refreshTable(List<ClassRoom> classRooms) {
        tableModel.setRowCount(0); // Clear existing data
        if (classRooms != null) {
            for (ClassRoom cr : classRooms) {
                if (cr != null) {
                    // Assumes getters: getClassId(), getClassName(), getTeacher()
                    tableModel.addRow(new Object[]{
                            cr.getClassId(),
                            cr.getClassName(),
                            cr.getTeacher() != null ? cr.getTeacher() : "" // Handle null teacher name
                    });
                }
            }
        }
    }

    private void clearForm() {
        classIdField.setText("");
        classNameField.setText("");
        teacherField.setText("");
        classRoomTable.clearSelection();
        classIdField.requestFocus();
    }

    private void populateFields() {
        int selectedRow = classRoomTable.getSelectedRow();
        if (selectedRow >= 0) {
            classIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            classNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            Object teacherValue = tableModel.getValueAt(selectedRow, 2);
            teacherField.setText(teacherValue != null ? teacherValue.toString() : "");
            // Optionally make classIdField non-editable after selection
            // classIdField.setEditable(false);
        } else {
            // Optionally make classIdField editable again if selection is cleared
            // classIdField.setEditable(true);
        }
    }

    // --- UI Styling Helper Methods (Copied from CourseView - icon logic removed) ---

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

    // createStyledButton without icon logic
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_NORMAL_BG);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(BUTTON_MARGIN);
        button.setFocusPainted(false);

        // Hover/Press Effects
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
            ClassRoomView view = new ClassRoomView(null); // Pass null for standalone testing
            view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit on close only for test
            view.setVisible(true);
        });
    }
}