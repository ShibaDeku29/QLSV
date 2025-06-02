package model;

public class User {
    private String userId; // Đổi thành String để khớp với VARCHAR trong DB
    private String username;
    private String password;
    private String role;
    private String studentId; // Đổi thành String để khớp với VARCHAR trong DB, có thể null

    // Constructor chính, nhận String userId và String studentId
    public User(String userId, String username, String password, String role, String studentId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId; // Gán String studentId
    }


    public User(String username, String password, String role) {
        // Gọi constructor chính với giá trị mặc định (ví dụ: userId=null, studentId=null)
        this(null, username, password, role, null);
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getStudentId() { return studentId; } // Trả về String

    // Setters (Thêm nếu cần)
    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    // Không cần phương thức getStudentIdAsString() nữa vì getStudentId() đã trả về String

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", role=" + role + ", studentId=" + studentId + "]";
    }

    public String getId() {
        return userId;
    }

    public String getName() {
        return username;
    }
}