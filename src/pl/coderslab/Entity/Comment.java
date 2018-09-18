package pl.coderslab.Entity;

public class Comment {

    private int id;
    private int grade;
    private String comment;

    public Comment() {
    }

    public Comment(int grade, String comment) {
        this.grade = grade;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
