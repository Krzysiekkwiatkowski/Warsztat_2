package pl.coderslab.Entity;

import javax.jws.soap.SOAPBinding;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class User {

    private static String SAVE_USER = "INSERT INTO users(username, email, password, user_group_id) VALUES (?,?,?,?)";
    private static String EDIT_USER = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static String LOAD_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static String LOAD_ALL_USERS = "SELECT * FROM users";
    private static String LOAD_ALL_USERS_BY_GROUP_ID = "SELECT * FROM users WHERE user_group_id = ?";

    private long id;
    private String username;
    private String email;
    private String password;
    private Integer userGroupId;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserGroup() {
        return userGroupId;
    }

    public void setUserGroup(int userGroup) {
        this.userGroupId = userGroup;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if (this.id == 0) {
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER, generatedColumns);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.password);
            if (this.userGroupId == null) {
                preparedStatement.setNull(4, Types.INTEGER);
            } else {
                preparedStatement.setInt(4, this.userGroupId);
            }
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                this.id = resultSet.getLong(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_USER);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, password);
            preparedStatement.setLong(4, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if (this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER);
            preparedStatement.setLong(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0L;
        }
    }

    public static User loadById(Connection connection, long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_USER_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getLong("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.email = resultSet.getString("email");
            loadedUser.password = resultSet.getString("password");
            if (resultSet.getInt("user_group_id") != Types.NULL) {
                loadedUser.userGroupId = resultSet.getInt("user_group_id");
            }
            return loadedUser;
        }
        return null;
    }

    public static User[] loadAll(Connection connection) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_USERS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getLong("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.email = resultSet.getString("email");
            loadedUser.password = resultSet.getString("password");
            if (resultSet.getInt("user_group_id") != Types.NULL) {
                loadedUser.userGroupId = resultSet.getInt("user_group_id");
            }
            users.add(loadedUser);
        }
        User[] userTable = new User[users.size()];
        userTable = users.toArray(userTable);
        return userTable;
    }

    public static User[] loadAllByGroupId(Connection connection, int id) throws SQLException {
        ArrayList<User> groupUsers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_USERS_BY_GROUP_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            User loadedUser = new User();
            loadedUser.id = resultSet.getLong("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.email = resultSet.getString("email");
            loadedUser.password = resultSet.getString("password");
            loadedUser.userGroupId = resultSet.getInt("user_group_id");
            groupUsers.add(loadedUser);
        }
        User[] userTable = new User[groupUsers.size()];
        userTable = groupUsers.toArray(userTable);
        return userTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        User[] users = loadAll(connection);
        for (User user : users) {
            System.out.println(user.getId() + " - " + user.getUsername() + " - " + user.getEmail() + " - " + user.getPassword() + " - " + user.getUserGroup());
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")) {
            String option = "";
            Scanner scanner = new Scanner(System.in);
            Scanner scanner1 = new Scanner(System.in);
            while (!option.equals("quit")) {
                User.showAll(connection);
                System.out.println("Wybierz jedną z opcji add, edit, delete, quit");
                option = scanner.next();
                if (option.equals("add")) {
                    User user = new User();
                    System.out.println("Podaj imie");
                    user.username = scanner1.next();
                    System.out.println("Podaj email");
                    user.email = scanner1.next();
                    System.out.println("Podaj hasło");
                    user.password = scanner1.next();
                    System.out.println("Podaj id grupy");
                    user.userGroupId = scanner1.nextInt();
                    user.saveToDB(connection);
                }
                if (option.equals("edit")) {
                    System.out.println("Podaj id");
                    long usersId = scanner1.nextLong();
                    User user = User.loadById(connection, usersId);
                    System.out.println("Podaj imie");
                    user.username = scanner1.next();
                    System.out.println("Podaj email");
                    user.email = scanner1.next();
                    System.out.println("Podaj hasło");
                    user.password = scanner1.next();
                    System.out.println("Podaj id grupy");
                    user.userGroupId = scanner1.nextInt();
                    user.saveToDB(connection);
                }
                if (option.equals("delete")) {
                    System.out.println("Podaj id");
                    long usersId = scanner1.nextLong();
                    User user = User.loadById(connection, usersId);
                    user.delete(connection);
                    user.id = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
