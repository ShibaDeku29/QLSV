package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // Đăng nhập
    public User login(String username, String password) throws SQLException {
        if (username.isEmpty() || password.isEmpty()) {
            throw new SQLException("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        }
        User user = userDAO.authenticate(username, password);
        if (user == null) {
            throw new SQLException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        return user;
    }

    // Thêm người dùng (cho quản trị viên)
    public void addUser(User user) throws SQLException {
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            throw new SQLException("Tên đăng nhập đã tồn tại!");
        }
        userDAO.addUser(user);
    }
}