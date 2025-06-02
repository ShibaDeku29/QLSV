package model;

public class Course {
    private String courseId;
    private String courseName;
    private int credits;

    // Constructor
    public Course(String courseId, String courseName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return courseId + " - " + courseName + " - " + credits + " tín chỉ";
    }
}