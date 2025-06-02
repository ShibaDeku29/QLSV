-- Tạo cơ sở dữ liệu
CREATE DATABASE IF NOT EXISTS student_management1;
USE student_management1;

-- Tạo bảng classrooms (Lớp học)
CREATE TABLE classrooms (
                            class_id VARCHAR(10) PRIMARY KEY,
                            class_name VARCHAR(50),
                            teacher VARCHAR(100)
);

-- Tạo bảng students (Sinh viên)
CREATE TABLE students (
                          id VARCHAR(10) PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          dob DATE,
                          gender VARCHAR(10),
                          address VARCHAR(200),
                          email VARCHAR(100),
                          phone VARCHAR(15),
                          class_id VARCHAR(10),
                          status VARCHAR(20),
                          FOREIGN KEY (class_id) REFERENCES classrooms(class_id)
);

-- Tạo bảng courses (Môn học)
CREATE TABLE courses (
                         course_id VARCHAR(10) PRIMARY KEY,
                         course_name VARCHAR(100),
                         credits INT
);

-- Tạo bảng grades (Điểm số)
CREATE TABLE grades (
                        student_id VARCHAR(10),
                        course_id VARCHAR(10),
                        score FLOAT,
                        PRIMARY KEY (student_id, course_id),
                        FOREIGN KEY (student_id) REFERENCES students(id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- Tạo bảng users (Người dùng)
CREATE TABLE users (   userid VARCHAR(10) PRIMARY KEY ,
                       username VARCHAR(50) ,
                       password VARCHAR(100),
                       role VARCHAR(20)
);