package view;

import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthService authService;

    public LoginView() {
        authService = new AuthService();
        setTitle("Đăng nhập Hệ thống");
        setSize(2000, 1333); // Giữ nguyên kích thước
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel với hình nền
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout()); // Thay đổi layout thành GridBagLayout

        // Tạo panel con chứa các thành phần đăng nhập
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        // Thay đổi kích thước viền để panel nhỏ hơn
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setBackground(new Color(255, 255, 255, 200)); // Tăng độ đục một chút

        // Thêm các thành phần giao diện
        loginPanel.add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        usernameField.setOpaque(true);
        usernameField.setBackground(Color.WHITE);
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField();
        passwordField.setOpaque(true);
        passwordField.setBackground(Color.WHITE);
        loginPanel.add(passwordField);

        loginButton = new JButton("Đăng nhập");
        loginButton.setOpaque(true);
        loginButton.setBackground(Color.WHITE);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        // Xử lý sự kiện nút đăng nhập
        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User user = authService.login(username, password);
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Vai trò: " + user.getRole());
                // Chuyển đến DashboardView
                new DashboardView(user).setVisible(true);
                dispose(); // Đóng cửa sổ đăng nhập
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Tạo một JPanel cho form đăng nhập với kích thước cố định
        JPanel formContainer = new JPanel();
        formContainer.setOpaque(false); // Panel này trong suốt
        formContainer.setLayout(new BorderLayout());
        formContainer.add(loginPanel, BorderLayout.CENTER);
        formContainer.setPreferredSize(new Dimension(400, 200)); // Kích thước vừa phải

        // Thêm formContainer vào backgroundPanel
        backgroundPanel.add(formContainer);

        // Thêm backgroundPanel vào frame
        add(backgroundPanel);
    }

    // Lớp panel tùy chỉnh để vẽ hình nền
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Tải hình ảnh (đảm bảo đường dẫn đúng)
            try {
                backgroundImage = new ImageIcon("src/resource/image/anhnen.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể tải hình nền!");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Vẽ hình ảnh nền, co giãn để vừa với kích thước panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}