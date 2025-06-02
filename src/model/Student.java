package model;

import java.util.Date;

public class Student {
    private String id;
    private String name;
    private Date dob;
    private String gender;
    private String address;
    private String email;
    private String phone;
    private String classId;
    private String status;

    // Constructor
    public Student(String id, String name, Date dob, String gender, String address,
                   String email, String phone, String classId, String status) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.classId = classId;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return id + " - " + name + " - " + email + " - " + status;
    }
}