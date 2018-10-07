package pl.coderslab.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Skill {

    private static String SAVE_SKILL = "INSERT INTO skill(skill, explanation) VALUES (?,?)";
    private static String EDIT_SKILL = "UPDATE skill SET skill = ?, explanation = ? WHERE id = ?";
    private static String DELETE_SKILL = "DELETE FROM skill WHERE id = ?";
    private static String LOAD_SKILL_BY_ID = "SELECT * FROM skill WHERE id = ?";
    private static String LOAD_ALL_SKILLS = "SELECT * FROM skill";

    private int id;
    private String skill;
    private String explanation;

    public Skill() {
    }

    public Skill(String skill, String explain) {
        this.skill = skill;
        this.explanation = explain;
    }

    public int getId() {
        return id;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getExplain() {
        return explanation;
    }

    public void setExplain(String explanation) {
        this.explanation = explanation;
    }

    public void saveToDB(Connection connection) throws SQLException {
        if(this.id == 0){
            String[] generatedColumns = {"ID"};
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SKILL, generatedColumns);
            preparedStatement.setString(1, this.skill);
            preparedStatement.setString(2, this.explanation);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                this.id = resultSet.getInt(1);
            }
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_SKILL);
            preparedStatement.setString(1, this.skill);
            preparedStatement.setString(2, this.explanation);
            preparedStatement.setInt(3, this.id);
            preparedStatement.executeUpdate();
        }
    }

    public void delete(Connection connection) throws SQLException {
        if(this.id != 0) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SKILL);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Skill loadById(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_SKILL_BY_ID);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Skill skill = new Skill();
            skill.id = resultSet.getInt("id");
            skill.skill = resultSet.getString("skill");
            skill.explanation = resultSet.getString("explanation");
            return skill;
        }
        return null;
    }

    public static Skill[] loadAll(Connection connection) throws SQLException {
        ArrayList<Skill> skills = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(LOAD_ALL_SKILLS);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Skill skill =  new Skill();
            skill.id = resultSet.getInt("id");
            skill.skill = resultSet.getString("skill");
            skill.explanation = resultSet.getString("explanation");
            skills.add(skill);
        }
        Skill[] skillTable = new Skill[skills.size()];
        skillTable = skills.toArray(skillTable);
        return skillTable;
    }

    public static void showAll(Connection connection) throws SQLException {
        Skill[] skills = loadAll(connection);
        for (Skill skill : skills) {
            System.out.println(skill.id + " " + skill.skill + " " + skill.explanation);
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")) {
            String option = "";
            Scanner scanner = new Scanner(System.in);
            Scanner scanner1 = new Scanner(System.in);
            while (!option.equals("quit")){
                Skill.showAll(connection);
                System.out.println("Wybierz jedną z opcji add, edit, delete, quit");
                option = scanner.next();
                if(option.equals("add")){
                    Skill skill = new Skill();
                    System.out.println("Podaj umiejętność");
                    String newSkill = scanner1.nextLine();
                    skill.skill = newSkill;
                    System.out.println("Podaj wyjaśnienie");
                    String explanation = scanner1.nextLine();
                    skill.explanation = explanation;
                    skill.saveToDB(connection);
                }
                if(option.equals("edit")){
                    System.out.println("Podaj id umiejętności do edycji");
                    int id = Integer.parseInt(scanner1.nextLine());
                    Skill skill = Skill.loadById(connection, id);
                    System.out.println("Podaj umiejętność");
                    String newSkill = scanner1.nextLine();
                    skill.skill = newSkill;
                    System.out.println("Podaj wyjaśnienie");
                    String explanation = scanner1.nextLine();
                    skill.explanation = explanation;
                    skill.saveToDB(connection);
                }
                if(option.equals("delete")){
                    System.out.println("Podaj id umiejętności do usunięcia");
                    int id = scanner1.nextInt();
                    Skill skill = Skill.loadById(connection, id);
                    skill.delete(connection);
                    skill.id = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
