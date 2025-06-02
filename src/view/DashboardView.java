package view;

import model.User;
import service.AuthService; // Import nếu cần cho logic khác
import service.StudentService; // Import StudentService
import model.Student; // Import Student model

import javax.swing.*;
import javax.swing.border.Border; // Import Border
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder; // Import LineBorder
import javax.swing.border.CompoundBorder; // Import CompoundBorder
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException; // Import IOException
import javax.imageio.ImageIO; // Import ImageIO

public class DashboardView extends JFrame {
    private User currentUser;

    // --- Copy Styling Constants từ các View đã style ---
    private static final Color SIDE_PANEL_BG = new Color(238, 242, 247);
    private static final Color CONTENT_PANEL_BG = new Color(255, 255, 255, 220);
    private static final Color BUTTON_NORMAL_BG = SIDE_PANEL_BG;
    private static final Color BUTTON_HOVER_BG = new Color(215, 225, 235);
    private static final Color BUTTON_PRESSED_BG = new Color(195, 205, 215);
    private static final Color BUTTON_TEXT_COLOR = new Color(50, 50, 90);
    private static final Color BORDER_COLOR = new Color(200, 200, 220);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font WELCOME_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private static final Border BUTTON_BORDER = new EmptyBorder(12, 20, 12, 20);
    // --- Hết phần copy constants ---

    public DashboardView(User user) {
        if (user == null) {
            showErrorDialog("Lỗi: Thông tin người dùng không hợp lệ. Không thể khởi tạo Dashboard.");
            System.exit(1);
            return;
        }
        this.currentUser = user;

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
                ex.printStackTrace();
            }
        }

        setTitle("Hệ thống Quản lý Sinh viên - Bảng điều khiển");
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Main Panel with Background ---
        // Cập nhật đường dẫn ảnh nền nếu cần
        BackgroundPanel backgroundPanel = new BackgroundPanel("src/resource/image/anhnen.jpg");
        backgroundPanel.setLayout(new BorderLayout(0, 0));

        // --- Left Menu Panel ---
        JPanel menuPanel = createMenuPanel(); // Gọi hàm helper
        backgroundPanel.add(menuPanel, BorderLayout.WEST);

        // --- Center Content Panel ---
        JPanel contentPanel = createContentPanel(); // Gọi hàm helper
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
    }

    // --- Helper Method to Create the Menu Panel ---
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(SIDE_PANEL_BG.getRed(), SIDE_PANEL_BG.getGreen(), SIDE_PANEL_BG.getBlue(), 230));
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center; padding: 10px;'>"
                + "<b>Xin chào,</b><br>"
                + escapeHtml(currentUser.getUsername()) + "<br>"
                + "<font color='gray'>(" + escapeHtml(currentUser.getRole()) + ")</font>"
                + "</div></html>", SwingConstants.CENTER);
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        menuPanel.add(welcomeLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Menu Buttons
        JButton studentButton = createMenuButton("Quản lý Sinh viên");
        JButton classRoomButton = createMenuButton("Quản lý Lớp học");
        JButton courseButton = createMenuButton("Quản lý Môn học");
        JButton gradeButton = createMenuButton("Quản lý Điểm số");
        JButton reportButton = createMenuButton("Báo cáo/Thống kê");
        JButton viewGradesButton = createMenuButton("Xem Bảng điểm");
        JButton viewProfileButton = createMenuButton("Thông tin cá nhân"); // Nút mới
        JButton logoutButton = createMenuButton("Đăng xuất");

        // Add buttons based on role
        boolean isStudent = "student".equalsIgnoreCase(currentUser.getRole());
        if (isStudent) {
            menuPanel.add(viewProfileButton); // Thêm nút thông tin cá nhân
            menuPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Spacing
            menuPanel.add(viewGradesButton);
        } else { // Admin or Staff
            menuPanel.add(studentButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            menuPanel.add(classRoomButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            menuPanel.add(courseButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            menuPanel.add(gradeButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            menuPanel.add(reportButton);
        }

        menuPanel.add(Box.createVerticalGlue()); // Push logout to bottom
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Action Listeners ---
        addMenuButtonActions(studentButton, classRoomButton, courseButton, gradeButton,
                viewGradesButton, reportButton, viewProfileButton, logoutButton, isStudent); // Truyền thêm nút mới và role

        return menuPanel;
    }

    // --- Helper Method to Create the Content Panel ---
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_PANEL_BG);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel mainWelcome = new JLabel("Chào mừng đến với Hệ thống Quản lý Sinh viên!", SwingConstants.CENTER);
        mainWelcome.setFont(TITLE_FONT);
        mainWelcome.setForeground(BUTTON_TEXT_COLOR.darker());
        contentPanel.add(mainWelcome, BorderLayout.CENTER);
        return contentPanel;
    }

    // --- Helper Method to Create Styled Menu Buttons (Ko icon)---
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MENU_FONT);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBackground(BUTTON_NORMAL_BG);
        button.setFocusPainted(false);
        button.setBorder(BUTTON_BORDER); // Chỉ padding
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height + 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

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

    // --- Helper Method to Add Action Listeners (Thêm viewProfileButton) ---
    private void addMenuButtonActions(JButton studentButton, JButton classRoomButton, JButton courseButton,
                                      JButton gradeButton, JButton viewGradesButton, JButton reportButton,
                                      JButton viewProfileButton, JButton logoutButton, boolean isStudent) {

        final JFrame currentDashboard = this;

        // Chỉ thêm listener cho các nút tồn tại dựa trên vai trò
        if (!isStudent) {
            studentButton.addActionListener(e -> {
                StudentView studentManagementView = new StudentView(currentDashboard);
                studentManagementView.setVisible(true);
            });

            classRoomButton.addActionListener(e -> {
                ClassRoomView classRoomManagementView = new ClassRoomView(currentDashboard);
                classRoomManagementView.setVisible(true);
            });

            courseButton.addActionListener(e -> {
                CourseView courseManagementView = new CourseView(currentDashboard);
                courseManagementView.setVisible(true);
            });

            gradeButton.addActionListener(e -> {
                try {
                    GradeView gradeManagementView = new GradeView(currentUser, currentDashboard);
                    gradeManagementView.setVisible(true);
                } catch (Exception ex) {
                    showErrorDialog("Không thể mở màn hình quản lý điểm số: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            reportButton.addActionListener(e -> {
                JFrame reportFrame = new JFrame("Báo cáo / Thống kê");
                reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                reportFrame.setSize(800, 600);
                reportFrame.setLocationRelativeTo(currentDashboard);
                try {
                    reportFrame.add(new ReportsPanel());
                    reportFrame.setVisible(true);
                } catch (Exception ex) {
                    showErrorDialog("Không thể mở màn hình báo cáo: " + ex.getMessage());
                    ex.printStackTrace();
                    reportFrame.dispose();
                }
            });
        } else { // isStudent
            viewProfileButton.addActionListener(e -> {
                // Kiểm tra studentId trước khi mở profile
                if (currentUser.getStudentId() == null || currentUser.getStudentId().trim().isEmpty()) {
                    showErrorDialog("Lỗi: Tài khoản sinh viên này chưa được liên kết với Mã Sinh viên cụ thể.");
                    return;
                }
                // Mở StudentProfileView, truyền currentUser
                StudentProfileView profileView = new StudentProfileView(currentUser, currentDashboard); // Truyền cả dashboard để quay lại
                profileView.setVisible(true);
                // Không ẩn dashboard khi mở profile
            });

            viewGradesButton.addActionListener(e -> {
                try {
                    // Kiểm tra studentId trước khi mở xem điểm
                    if (currentUser.getStudentId() == null || currentUser.getStudentId().trim().isEmpty()) {
                        showErrorDialog("Lỗi: Tài khoản sinh viên này chưa được liên kết với Mã Sinh viên cụ thể để xem điểm.");
                        return;
                    }
                    StudentGradeView studentGradeView = new StudentGradeView(currentUser);
                    studentGradeView.setVisible(true);
                } catch (Exception ex) {
                    showErrorDialog("Không thể mở màn hình xem điểm: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }

        // Logout Listener (cho mọi vai trò)
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(currentDashboard,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận Đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                });
            }
        });
    }

    // --- Utility methods ---
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // --- Custom Background Panel (Đã cải tiến) ---
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private String imagePath;

        public BackgroundPanel(String imagePath) {
            this.imagePath = imagePath;
            loadImage();
        }

        private void loadImage() {
            try {
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    System.err.println("Lỗi: Hình nền không tồn tại tại: " + imageFile.getAbsolutePath());
                    backgroundImage = null;
                    return;
                }
                backgroundImage = ImageIO.read(imageFile);
                if (backgroundImage == null) {
                    System.err.println("Lỗi: Không thể đọc tệp hình ảnh nền từ: " + imageFile.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Lỗi I/O khi tải hình nền: " + imagePath);
                e.printStackTrace();
                backgroundImage = null;
            } catch (Exception e) {
                System.err.println("Lỗi không xác định khi tải hình nền: " + imagePath);
                e.printStackTrace();
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(220, 225, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // --- Main Method for Testing ---
    public static void main(String[] args) {
        // User testAdmin = new User("U001", "admin", "pass", "admin", null);
        User testStudent = new User("U004", "sv001", "sv001123", "student", "SV001"); // SV hợp lệ

        SwingUtilities.invokeLater(() -> {
            DashboardView studentDashboard = new DashboardView(testStudent);
            studentDashboard.setTitle(studentDashboard.getTitle() + " (Student View)");
            studentDashboard.setVisible(true);
        });
    }
}