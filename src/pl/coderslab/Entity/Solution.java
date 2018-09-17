package pl.coderslab.Entity;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Solution {

    private static String SAVE_SOLUTION = "INSERT INTO solution (created, updated, description, exercise_id, users_id) VALUES (?,?,?,?,?)";
    private static String EDIT_SOLUTION = "UPDATE solution SET updated = ?, description = ?, exercise_id = ?, users_id = ? WHERE id = ?";
    private static String DELETE_SOLUTION = "DELETE FROM solution WHERE id = ?";
    private static String LOAD_SOLUTION_BY_ID = "SELECT * FROM solution WHERE id = ?";
    private static String LOAD_ALL_SOLUTIONS = "SELECT * FROM solution";

    private int id;
    private Date created;
    private Date updated;
    private String description;
    private int exercise_id;
    private long user_id;

    public Solution() {
    }

    public Solution(String description) {
        this.created = Date.valueOf(LocalDate.now());
        this.updated = Date.valueOf(LocalDate.now());
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if(this.id == 0){
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SOLUTION, generatedColumns);
            preparedStatement.setDate(1, Date.valueOf(this.created.toLocalDate()));
            this.updated = Date.valueOf(LocalDate.now());
            preparedStatement.setDate(2, this.updated);
            preparedStatement.setString(3, this.description);
            if(this.exercise_id != 0){
                preparedStatement.setInt(4, this.exercise_id);
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            if(this.user_id != 0){
                preparedStatement.setLong(5, this.user_id);
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_SOLUTION);
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(2, this.description);
            if(this.exercise_id != 0) {
                preparedStatement.setInt(3, this.exercise_id);
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            if(this.user_id != 0){
                preparedStatement.setLong(4, this.user_id);
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            preparedStatement.setInt(5, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if(this.id != 0){
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SOLUTION);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Solution loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_SOLUTION_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            if(resultSet.getInt("exercise_id") != Types.NULL){
                loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            }
            if(resultSet.getLong("users_id") != Types.NULL){
                loadedSolution.user_id = resultSet.getLong("users_id");
            }
            return loadedSolution;
        }
        return null;
    }

    public static Solution[] loadAll(Connection connection) throws SQLException {
        ArrayList<Solution> solutions = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_SOLUTIONS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Solution loadedSolution = new Solution();
            loadedSolution.id = resultSet.getInt("id");
            loadedSolution.created = resultSet.getDate("created");
            loadedSolution.updated = resultSet.getDate("updated");
            loadedSolution.description = resultSet.getString("description");
            if(resultSet.getInt("exercise_id") != 0){
                loadedSolution.exercise_id = resultSet.getInt("exercise_id");
            }
            if(resultSet.getInt("users_id") != 0){
                loadedSolution.user_id = resultSet.getLong("users_id");
            }
            solutions.add(loadedSolution);
        }
        Solution[] solutionTable = new Solution[solutions.size()];
        solutionTable = solutions.toArray(solutionTable);
        return solutionTable;
    }
}
