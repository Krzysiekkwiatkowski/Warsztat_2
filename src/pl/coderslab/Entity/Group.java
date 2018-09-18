package pl.coderslab.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Group {

    private static String SAVE_GROUP = "INSERT INTO user_group (name) VALUES (?)";
    private static String EDIT_GROUP = "UPDATE user_group SET name = ? WHERE id = ?";
    private static String DELETE_GROUP = "DELETE FROM user_group WHERE id = ?";
    private static String LOAD_GROUP_BY_ID = "SELECT * FROM user_group WHERE id = ?";
    private static String LOAD_ALL_GROUPS = "SELECT * FROM user_group";

    private int id;
    private String name;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if (this.id == 0) {
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_GROUP, generatedColumns);
            preparedStatement.setString(1, this.name);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_GROUP);
            preparedStatement.setString(1, this.name);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if (this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GROUP);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Group loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_GROUP_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Group loadedGroup = new Group();
            loadedGroup.id = resultSet.getInt("id");
            loadedGroup.name = resultSet.getString("name");
            return loadedGroup;
        }
        return null;
    }

    public static Group[] loadAll(Connection connection) throws SQLException {
        ArrayList<Group> groups = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_GROUPS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Group loadedGroup = new Group();
            loadedGroup.id = resultSet.getInt("id");
            loadedGroup.name = resultSet.getString("name");
            groups.add(loadedGroup);
        }
        Group[] groupTable = new Group[groups.size()];
        groupTable = groups.toArray(groupTable);
        return groupTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        Group[] groups = Group.loadAll(connection);
        for (Group group : groups) {
            System.out.println(group.getId() + " - " + group.getName());
        }
    }

    public static void main(String[] args) {
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")){
            String option = "";
            Scanner scanner = new Scanner(System.in);
            Scanner scanner1 = new Scanner(System.in);
            while (!option.equals("quit")){
                showAll(connection);
                System.out.println("Wybierz jedną z opcji add, edit, delete, quit");
                option = scanner.next();
                if(option.equals("add")){
                    Group group = new Group();
                    System.out.println("Podaj nazwę");
                    group.name = scanner1.nextLine();
                    group.saveToDB(connection);
                }
                if(option.equals("edit")){
                    System.out.println("Podaj id");
                    int groupId = Integer.parseInt(scanner1.nextLine());
                    Group loadedGroup = Group.loadById(connection, groupId);
                    System.out.println("Podaj nazwę");
                    loadedGroup.name = scanner1.nextLine();
                    loadedGroup.saveToDB(connection);
                }
                if(option.equals("delete")){
                    System.out.println("Podaj id");
                    int groupId = Integer.parseInt(scanner1.nextLine());
                    Group group = Group.loadById(connection, groupId);
                    group.delete(connection);
                    group.id = 0;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
