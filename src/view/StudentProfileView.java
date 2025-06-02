package view;

import model.ClassRoom; // Import ClassRoom
import model.Student;
import model.User;
import service.ClassRoomService; // Import ClassRoomService
import service.StudentService;

import javax.swing.*;
import javax.swing.border.*; // Import borders
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat; // Import SimpleDateFormat

public class StudentProfileView extends JFrame {

    // --- Styling Constants ---
    private static final Color PRIMARY_COLOR_LIGHT = new Color(238, 242, 247);
    private static final Color PRIMARY_COLOR_DARK = new Color(50, 50, 90);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final Color BUTTON_NORMAL_BG = new Color(100, 149, 237);
    private static final Color BUTTON_HOVER_BG = new Color(135, 206, 250);
    private static final Color BUTTON_PRESSED_BG = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14); // Slightly larger label font
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Insets BUTTON_MARGIN = new Insets(8, 20, 8, 20); // Larger button padding

    // --- UI Components ---
    private JLabel studentIdValueLabel, nameValueLabel, dobValueLabel, genderValueLabel,
            addressValueLabel, emailValueLabel, phoneValueLabel, classNameValueLabel, statusValueLabel;
    private JButton backButton;

    // --- Services ---
    private StudentService studentService;
    private ClassRoomService classRoomService;
    private JFrame previousView; // Để quay lại dashboard

    // Constructor nhận User và JFrame trước đó
    public StudentProfileView(User loggedInUser, JFrame previous) {
        this.previousView = previous;
        this.studentService = new StudentService();
        this.classRoomService = new ClassRoomService(); // Khởi tạo

        // --- Apply Nimbus Look and Feel ---
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        }

        setTitle("Thông Tin Cá Nhân Sinh Viên");
        setSize(550, 450); // Adjusted size
        setMinimumSize(new Dimension(450, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center relative to the dashboard? Maybe previousView

        // --- Layout Setup ---
        setLayout(new BorderLayout(15, 15));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Thông Tin Cá Nhân", JLabel.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(PRIMARY_COLOR_DARK);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Padding below header
        add(headerLabel, BorderLayout.NORTH);

        // Information Panel using GridBagLayout for alignment
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1), // Add a border around info
                new EmptyBorder(15, 20, 15, 20)  // Inner padding
        ));
        infoPanel.setBackground(PRIMARY_COLOR_LIGHT); // Light background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 15); // Spacing (top, left, bottom, right)
        gbc.anchor = GridBagConstraints.WEST; // Align labels left

        int gridy = 0;

        // Row 1: Mã SV
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0;
        infoPanel.add(createStyledLabel("Mã sinh viên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        studentIdValueLabel = createStyledValueLabel("..."); // Placeholder
        infoPanel.add(studentIdValueLabel, gbc);
        gridy++;

        // Row 2: Họ tên
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Họ và tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        nameValueLabel = createStyledValueLabel("...");
        infoPanel.add(nameValueLabel, gbc);
        gridy++;

        // Row 3: Ngày sinh
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        dobValueLabel = createStyledValueLabel("...");
        infoPanel.add(dobValueLabel, gbc);
        gridy++;

        // Row 4: Giới tính
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        genderValueLabel = createStyledValueLabel("...");
        infoPanel.add(genderValueLabel, gbc);
        gridy++;

        // Row 5: Địa chỉ
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        addressValueLabel = createStyledValueLabel("...");
        infoPanel.add(addressValueLabel, gbc);
        gridy++;

        // Row 6: Email
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        emailValueLabel = createStyledValueLabel("...");
        infoPanel.add(emailValueLabel, gbc);
        gridy++;

        // Row 7: Số điện thoại
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneValueLabel = createStyledValueLabel("...");
        infoPanel.add(phoneValueLabel, gbc);
        gridy++;

        // Row 8: Lớp
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Lớp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        classNameValueLabel = createStyledValueLabel("...");
        infoPanel.add(classNameValueLabel, gbc);
        gridy++;

        // Row 9: Trạng thái
        gbc.gridx = 0; gbc.gridy = gridy; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        infoPanel.add(createStyledLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        statusValueLabel = createStyledValueLabel("...");
        infoPanel.add(statusValueLabel, gbc);
        // gridy++;

        add(infoPanel, BorderLayout.CENTER);

        // Back Button Panel (SOUTH)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        buttonPanel.setOpaque(false); // Make transparent
        backButton = createStyledButton("Quay lại"); // Use styled button
        backButton.addActionListener(e -> {
            if (previousView != null) {
                previousView.setVisible(true); // Show the dashboard again
            }
            dispose(); // Close this profile window
        });
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data after UI is set up
        loadProfileData(loggedInUser);
    }

    // Load student data based on the logged-in user
    private void loadProfileData(User loggedInUser) {
        if (loggedInUser == null || loggedInUser.getStudentId() == null || loggedInUser.getStudentId().trim().isEmpty()) {
            showErrorMessage("Không thể tải thông tin: Thiếu thông tin sinh viên.");
            // Set labels to error state
            studentIdValueLabel.setText("Lỗi");
            nameValueLabel.setText("Lỗi");
            // ... set others to "Lỗi" or empty
            return;
        }

        String studentId = loggedInUser.getStudentId();

        try {
            Student student = studentService.getStudent(studentId); // Fetch student details

            if (student != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Format for display
                studentIdValueLabel.setText(student.getId());
                nameValueLabel.setText(student.getName());
                dobValueLabel.setText(student.getDob() != null ? sdf.format(student.getDob()) : "Chưa cập nhật");
                genderValueLabel.setText(student.getGender() != null ? student.getGender() : "Chưa cập nhật");
                addressValueLabel.setText(student.getAddress() != null ? student.getAddress() : "Chưa cập nhật");
                emailValueLabel.setText(student.getEmail() != null ? student.getEmail() : "Chưa cập nhật");
                phoneValueLabel.setText(student.getPhone() != null ? student.getPhone() : "Chưa cập nhật");
                statusValueLabel.setText(student.getStatus() != null ? student.getStatus() : "Chưa cập nhật");

                // Fetch Class Name
                if (student.getClassId() != null && !student.getClassId().trim().isEmpty()) {
                    try {
                        ClassRoom classRoom = classRoomService.getClassRoomById(student.getClassId());
                        classNameValueLabel.setText(classRoom != null ? (classRoom.getClassName() + " (" + classRoom.getClassId() + ")") : "Không rõ");
                    } catch (SQLException ce) {
                        System.err.println("Lỗi khi lấy tên lớp: " + ce.getMessage());
                        classNameValueLabel.setText("Không rõ (lỗi)");
                    }
                } else {
                    classNameValueLabel.setText("Chưa xếp lớp");
                }

            } else {
                showErrorMessage("Không tìm thấy thông tin sinh viên với mã: " + studentId);
                // Set labels to indicate not found
                studentIdValueLabel.setText(studentId);
                nameValueLabel.setText("Không tìm thấy");
                // ... set others
            }
        } catch (SQLException e) {
            showErrorMessage("Lỗi khi tải thông tin cá nhân: " + e.getMessage());
            e.printStackTrace();
            // Set labels to error state
            studentIdValueLabel.setText("Lỗi CSDL");
            nameValueLabel.setText("Lỗi CSDL");
            // ... set others
        } catch (Exception ex) {
            showErrorMessage("Lỗi không xác định: " + ex.getMessage());
            ex.printStackTrace();
            // Set labels to error state
            studentIdValueLabel.setText("Lỗi");
            nameValueLabel.setText("Lỗi");
            // ... set others
        }
    }

    // --- Styling Helper Methods ---
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR_DARK);
        return label;
    }

    private JLabel createStyledValueLabel(String value) {
        JLabel label = new JLabel(value != null ? value : ""); // Handle potential null values
        label.setFont(VALUE_FONT);
        label.setForeground(PRIMARY_COLOR_DARK.darker()); // Slightly darker value text
        return label;
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
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(BUTTON_HOVER_BG); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(BUTTON_NORMAL_BG); }
            @Override public void mousePressed(MouseEvent e) { button.setBackground(BUTTON_PRESSED_BG); }
            @Override public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) { button.setBackground(BUTTON_HOVER_BG); }
                else { button.setBackground(BUTTON_NORMAL_BG); }
            }
        });
        return button;
    }

    // --- Message Dialog ---
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, "Lỗi: " + message, "Đã có lỗi xảy ra", JOptionPane.ERROR_MESSAGE);
    }

    // --- Main Method for Testing ---
    public static void main(String[] args) {
        // Apply L&F early
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        // Simulated User for testing (Make sure studentId exists in your DB)
        User testStudentUser = new User("U004", "sv001", "pass", "student", "SV001");
        User testStudentNotFound = new User("U999", "nosuchstudent", "pass", "student", "SV999");

        SwingUtilities.invokeLater(() -> {
            // Test with a valid student user
            StudentProfileView profileView1 = new StudentProfileView(testStudentUser, null); // No previous view for standalone test
            profileView1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            profileView1.setVisible(true);

            // Test with a user whose studentId might not exist
            // StudentProfileView profileView2 = new StudentProfileView(testStudentNotFound, null);
            // profileView2.setLocation(100,100);
            // profileView2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // profileView2.setVisible(true);
        });
    }
}