-- Sử dụng cơ sở dữ liệu đã tạo
USE student_management1;

-- Chèn dữ liệu mẫu cho bảng classrooms
INSERT INTO classrooms (class_id, class_name, teacher) VALUES
                                                           ('CNTT01', 'Lớp Công nghệ Thông tin 01', 'Nguyễn Văn Hùng'),
                                                           ('CNTT02', 'Lớp Công nghệ Thông tin 02', 'Trần Thị Mai'),
                                                           ('KTPM01', 'Lớp Kỹ thuật Phần mềm 01', 'Lê Văn Tâm');

-- Chèn dữ liệu mẫu cho bảng students
INSERT INTO students (id, name, dob, gender, address, email, phone, class_id, status) VALUES
                                                                                          ('SV001', 'Nguyễn Văn A', '2000-01-01', 'Nam', 'Hà Nội', 'a@gmail.com', '0123456789', 'CNTT01', 'Active'),
                                                                                          ('SV002', 'Trần Thị B', '2000-05-10', 'Nữ', 'TP.HCM', 'b@gmail.com', '0987654321', 'CNTT01', 'Active'),
                                                                                          ('SV003', 'Lê Văn C', '2001-03-15', 'Nam', 'Đà Nẵng', 'c@gmail.com', '0912345678', 'CNTT02', 'Active'),
                                                                                          ('SV004', 'Phạm Thị D', '2000-07-20', 'Nữ', 'Hà Nội', 'd@gmail.com', '0932145678', 'KTPM01', 'Active');

-- Chèn dữ liệu mẫu cho bảng courses
INSERT INTO courses (course_id, course_name, credits) VALUES
                                                          ('JAVA01', 'Lập trình Java', 3),
                                                          ('DBMS01', 'Cơ sở Dữ liệu', 4),
                                                          ('WEB01', 'Lập trình Web', 3),
                                                          ('ALGO01', 'Thuật toán', 3);

-- Chèn dữ liệu mẫu cho bảng grades
INSERT INTO grades (student_id, course_id, score) VALUES
                                                      ('SV001', 'JAVA01', 8.5),
                                                      ('SV001', 'DBMS01', 7.0),
                                                      ('SV001', 'ALGO01', 6.0), -- Thêm điểm cho SV001
                                                      ('SV002', 'JAVA01', 9.0),
                                                      ('SV002', 'WEB01', 6.5),
                                                      ('SV003', 'ALGO01', 5.0),
                                                      ('SV003', 'DBMS01', 7.5), -- Thêm điểm cho SV003
                                                      ('SV004', 'JAVA01', 8.0),
                                                      ('SV004', 'WEB01', 7.0); -- Thêm điểm cho SV004

-- Chèn dữ liệu mẫu cho bảng users (ĐÃ CẬP NHẬT)
-- Bao gồm userid (khóa chính), username, password, role, và studentId (liên kết tới students.id)
INSERT INTO users (userid, username, password, role, studentId) VALUES
                                                                    ('U001', 'admin', 'admin123', 'admin', NULL),          -- Admin không liên kết với sinh viên nào
                                                                    ('U002', 'staff', 'staff123', 'staff', NULL),          -- Staff không liên kết với sinh viên nào
                                                                    ('U003', 'student1', 'student123', 'student', NULL),       -- User sinh viên nhưng không có mã SV cụ thể trong bảng students
                                                                    ('U004', 'sv001', 'sv001123', 'student', 'SV001'),       -- User này liên kết với sinh viên có id='SV001'
                                                                    ('U005', 'sv002', 'sv002123', 'student', 'SV002'),       -- User này liên kết với sinh viên có id='SV002'
                                                                    ('U006', 'sv003', 'sv003123', 'student', 'SV003'),       -- User này liên kết với sinh viên có id='SV003'
                                                                    ('U007', 'sv004', 'sv004123', 'student', 'SV004');       -- User này liên kết với sinh viên có id='SV004'

