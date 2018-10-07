package pl.coderslab.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Skill_User {

    private static String SAVE_SKILL_USER = "INSERT INTO skill_user(skill_id, user_id) VALUES (?,?)";
    private static String EDIT_SKILL_USER = "UPDATE skill_user SET skill_id = ?, user_id = ? WHERE id = ?";
    private static String DELETE_SKILL_USER = "DELETE FROM skill_user WHERE id = ?";
    private static String LOAD_SKILL_USER_BY_ID = "SELECT * FROM skill_user WHERE id = ?";
    private static String LOAD_ALL_SKILL_USER = "SELECT * FROM skill_user";

    private int id;
    private int skill_id;
    private int user_id;

    public Skill_User() {
    }

    public Skill_User(int skill_id, int user_id) {
        this.skill_id = skill_id;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public int getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(int skill_id) {
        this.skill_id = skill_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if(this.id == 0){
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SKILL_USER, generatedColumns);
            preparedStatement.setInt(1, this.skill_id);
            preparedStatement.setInt(2, this.user_id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_SKILL_USER);
            preparedStatement.setInt(1, this.skill_id);
            preparedStatement.setInt(2, this.user_id);
            preparedStatement.setInt(3, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if(this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SKILL_USER);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Skill_User loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_SKILL_USER_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Skill_User skill_user = new Skill_User();
            skill_user.id = resultSet.getInt("id");
            skill_user.skill_id = resultSet.getInt("skill_user");
            skill_user.user_id = resultSet.getInt("user_id");
            return skill_user;
        }
        return null;
    }

    public static Skill_User[] loadAll(Connection connection) throws SQLException {
        ArrayList<Skill_User> skill_users = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_SKILL_USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Skill_User skill_user = new Skill_User();
            skill_user.id = resultSet.getInt("id");
            skill_user.skill_id = resultSet.getInt("skill_id");
            skill_user.user_id = resultSet.getInt("user_id");
            skill_users.add(skill_user);
        }
        Skill_User[] skill_userTable = new Skill_User[skill_users.size()];
        skill_userTable = skill_users.toArray(skill_userTable);
        return skill_userTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        Skill_User[] skill_users = loadAll(connection);
        for (Skill_User skill_user : skill_users) {
            System.out.println(skill_user.id + " " + skill_user.skill_id + " " + skill_user.user_id);
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")) {
            String option = "";
            Scanner scanner = new Scanner(System.in);
            Scanner scanner1 = new Scanner(System.in);
            while (!option.equals("quit")){
                Skill_User.showAll(connection);
                System.out.println("Wybierz jedną z opcji add, edit, delete, quit");
                option = scanner.next();
                if(option.equals("add")){
                    Skill_User skill_user = new Skill_User();
                    System.out.println("Podaj id umiejętności");
                    int skillId = Integer.parseInt(scanner1.nextLine());
                    skill_user.skill_id = skillId;
                    System.out.println("Podaj id użytkownika");
                    int userId = Integer.parseInt(scanner1.nextLine());
                    skill_user.user_id = userId;
                    skill_user.saveToDB(connection);
                }
                if(option.equals("edit")){
                    System.out.println("Podaj id przypisania umiejętności do edycji");
                    int id = Integer.parseInt(scanner1.nextLine());
                    Skill_User skill_user = Skill_User.loadById(connection, id);
                    System.out.println("Podaj id umiejętności");
                    int skillId = Integer.parseInt(scanner1.nextLine());
                    skill_user.skill_id = skillId;
                    System.out.println("Podaj id użytkownika");
                    int userId = Integer.parseInt(scanner1.nextLine());
                    skill_user.user_id = userId;
                    skill_user.saveToDB(connection);
                }
                if(option.equals("delete")){
                    System.out.println("Podaj id przypisania umiejętności do usunięcia");
                    int id = scanner1.nextInt();
                    Skill_User skill_user = Skill_User.loadById(connection, id);
                    skill_user.delete(connection);
                    skill_user.id = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
