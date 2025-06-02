package model;

public class ClassRoom {
    private String classId;
    private String className;
    private String teacher;

    // Constructor
    public ClassRoom(String classId, String className, String teacher) {
        this.classId = classId;
        this.className = className;
        this.teacher = teacher;
    }

    // Getters and Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    @Override
    public String toString() {
        return classId + " - " + className + " - " + teacher;
    }
}