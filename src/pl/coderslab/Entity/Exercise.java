package pl.coderslab.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Exercise {

    private static String SAVE_EXERCISE = "INSERT INTO exercise(title, description) VALUES (?,?)";
    private static String EDIT_EXERCISE = "UPDATE exercise SET title = ?, description = ? WHERE id = ?";
    private static String DELETE_EXERCISE = "DELETE FROM exercise WHERE id = ?";
    private static String LOAD_EXERCISE_BY_ID = "SELECT * FROM exercise WHERE id = ?";
    private static String LOAD_ALL_EXERCISES = "SELECT * FROM exercise";

    private int id;
    private String title;
    private String description;

    public Exercise() {
    }

    public Exercise(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if (this.id == 0) {
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_EXERCISE, generatedColumns);
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.description);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_EXERCISE);
            preparedStatement.setString(1, this.title);
            preparedStatement.setString(2, this.description);
            preparedStatement.setInt(3, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if (this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_EXERCISE);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Exercise loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_EXERCISE_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Exercise loadedExercise = new Exercise();
            loadedExercise.id = resultSet.getInt("id");
            loadedExercise.title = resultSet.getString("title");
            loadedExercise.description = resultSet.getString("description");
            return loadedExercise;
        }
        return null;
    }

    public static Exercise[] loadAll(Connection connection) throws SQLException {
        ArrayList<Exercise> exercises = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_EXERCISES);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Exercise exercise = new Exercise();
            exercise.id = resultSet.getInt("id");
            exercise.title = resultSet.getString("title");
            exercise.description = resultSet.getString("description");
            exercises.add(exercise);
        }
        Exercise[] exerciseTable = new Exercise[exercises.size()];
        exerciseTable = exercises.toArray(exerciseTable);
        return exerciseTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        Exercise[] exercises = Exercise.loadAll(connection);
        for (Exercise exercise : exercises) {
            System.out.println(exercise.id + " - " + exercise.title + " - " + exercise.description);
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")) {
            String option = "";
            Scanner scanner = new Scanner(System.in);
            Scanner scanner1 = new Scanner(System.in);
            Scanner scanner2 = new Scanner(System.in);
            while (!option.equals("quit")) {
                showAll(connection);
                System.out.println("Wybierz jedną z opcji add, edit, delete, quit");
                option = scanner.next();
                if (option.equals("add")) {
                    Exercise exercise = new Exercise();
                    System.out.println("Podaj tytuł");
                    String title = scanner1.nextLine();
                    exercise.title = title;
                    System.out.println("Podaj opis");
                    String description = scanner1.nextLine();
                    exercise.description = description;
                    exercise.saveToDB(connection);
                }
                if (option.equals("edit")) {
                    System.out.println("Podaj id");
                    int exerciseId = Integer.parseInt(scanner1.nextLine());
                    Exercise exercise = Exercise.loadById(connection, exerciseId);
                    System.out.println("Podaj tytuł");
                    exercise.title = scanner1.nextLine();
                    System.out.println("Podaj opis");
                    exercise.description = scanner1.nextLine();
                    exercise.saveToDB(connection);
                }
                if (option.equals("delete")) {
                    System.out.println("Podaj id");
                    int exerciseId = scanner1.nextInt();
                    Exercise exercise = Exercise.loadById(connection, exerciseId);
                    exercise.delete(connection);
                    exercise.id = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
