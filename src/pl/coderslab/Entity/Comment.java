package pl.coderslab.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Comment {

    private static String SAVE_COMMENT = "INSERT INTO comment(grade, description, solution_id) VALUES (?,?,?)";
    private static String EDIT_COMMENT = "UPDATE comment SET grade = ?, description = ?, solution_id = ? WHERE id = ?";
    private static String DELETE_COMMENT = "DELETE FROM comment WHERE id = ?";
    private static String LOAD_COMMENT_BY_ID = "SELECT * FROM comment WHERE id = ?";
    private static String LOAD_ALL_COMMENTS = "SELECT * FROM comment";

    private int id;
    private int grade;
    private String description;
    private int solution_id;

    public Comment() {
    }

    public Comment(int grade, String description, int solution_id) {
        this.grade = grade;
        this.description = description;
        this.solution_id = solution_id;
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
        return description;
    }

    public void setComment(String description) {
        this.description = description;
    }

    public int getSolution_id() {
        return solution_id;
    }

    public void setSolution_id(int solution_id) {
        this.solution_id = solution_id;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if(this.id == 0){
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_COMMENT, generatedColumns);
            preparedStatement.setInt(1, this.grade);
            preparedStatement.setString(2, this.description);
            preparedStatement.setInt(3, this.solution_id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_COMMENT);
            preparedStatement.setInt(1, this.grade);
            preparedStatement.setString(2, this.description);
            preparedStatement.setInt(3, this.solution_id);
            preparedStatement.setInt(4, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if(this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_COMMENT);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Comment loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_COMMENT_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Comment comment = new Comment();
            comment.id = resultSet.getInt("id");
            comment.grade = resultSet.getInt("grade");
            comment.description = resultSet.getString("description");
            comment.solution_id = resultSet.getInt("solution_id");
            return comment;
        }
        return null;
    }

    public static Comment[] loadAll(Connection connection) throws SQLException {
        ArrayList<Comment> comments = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_COMMENTS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Comment comment = new Comment();
            comment.id = resultSet.getInt("id");
            comment.grade = resultSet.getInt("grade");
            comment.description = resultSet.getString("description");
            comment.solution_id = resultSet.getInt("solution_id");
            comments.add(comment);
        }
        Comment[] commentTable = new Comment[comments.size()];
        commentTable = comments.toArray(commentTable);
        return commentTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        Comment[] comments = loadAll(connection);
        for (Comment comment : comments) {
            System.out.println(comment.id + " " + comment.grade + " " + comment.description + " " + comment.solution_id);
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")) {
            Comment.showAll(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
