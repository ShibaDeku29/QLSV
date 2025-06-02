package model;

public class Grade {
    private String studentId;
    private String courseId;
    private float score;

    // Constructor
    public Grade(String studentId, String courseId, float score) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    @Override
    public String toString() {
        return "Sinh viên: " + studentId + " - Môn: " + courseId + " - Điểm: " + score;
    }


}