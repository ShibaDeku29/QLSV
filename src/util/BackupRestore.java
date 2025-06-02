package util;

import java.io.*;

public class BackupRestore {
    private static final String MYSQLDUMP_PATH = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump"; // Đường dẫn đến mysqldump
    private static final String MYSQL_PATH = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysql"; // Đường dẫn đến mysql
    private static final String DB_NAME = "student_management";
    private static final String DB_USER = "root"; // Thay bằng username MySQL của bạn
    private static final String DB_PASSWORD = "your_password"; // Thay bằng password MySQL của bạn

    // Sao lưu cơ sở dữ liệu
    public void backupDatabase(String backupPath) throws Exception {
        String command = String.format("%s -u%s -p%s %s -r %s",
                MYSQLDUMP_PATH, DB_USER, DB_PASSWORD, DB_NAME, backupPath);
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Sao lưu thất bại! Mã lỗi: " + exitCode);
        }
    }

    // Phục hồi cơ sở dữ liệu
    public void restoreDatabase(String backupPath) throws Exception {
        String command = String.format("%s -u%s -p%s %s < %s",
                MYSQL_PATH, DB_USER, DB_PASSWORD, DB_NAME, backupPath);
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Phục hồi thất bại! Mã lỗi: " + exitCode);
        }
    }
}