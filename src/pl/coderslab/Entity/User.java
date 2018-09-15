package pl.coderslab.Entity;

import java.sql.*;

public class User {

    private static String SAVE_USER = "INSERT INTO users(username, email, password, user_group_id) VALUES (?,?,?,?)";
    private static String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static String LOAD_USER_BY_ID = "SELECT * FROM users WHERE id = ?";

    private Long id;
    private String username;
    private String email;
    private String password;
    private Integer userGroupId;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
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
            while (resultSet.next()){
                this.id = resultSet.getLong(1);
            }
        }
    }

    public void delete(Connection connection) throws SQLException {
        if(this.id != 0){
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER);
            preparedStatement.setLong(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0L;
        }
    }

    public User loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_USER_BY_ID);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            User loadedUser = new User();
            loadedUser.id = resultSet.getLong("id");
            loadedUser.username = resultSet.getString("username");
            loadedUser.email = resultSet.getString("email");
            loadedUser.password = resultSet.getString("password");
            if(resultSet.getInt("user_group_id") != Types.NULL){
                loadedUser.userGroupId = resultSet.getInt("user_group_id");
            }
            return loadedUser;
        }
        return null;
    }

    public User[] loadAll(Connection connection) throws SQLException {
        return null;
    }
}
