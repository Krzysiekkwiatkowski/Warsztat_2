package pl.coderslab.App;

import pl.coderslab.Entity.Exercise;
import pl.coderslab.Entity.Group;
import pl.coderslab.Entity.Solution;
import pl.coderslab.Entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
	    try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")){
            String option = "";
	        Scanner scanner = new Scanner(System.in);
	        Scanner scanner1 = new Scanner(System.in);
            System.out.println("Podaj swoje id");
            long userId = Long.valueOf(scanner.nextLine());
            while (!option.equals("quit")){
                try {
                    System.out.println("Wybierz jedną z opcji add lub view");
                    option = scanner.nextLine();
                    if (option.equals("add")) {
                        Exercise[] exercises = Exercise.loadAllWithoutSolution(connection, userId);
                        for (Exercise exercise : exercises) {
                            System.out.println(exercise.getId() + " - " + exercise.getTitle() + " - " + exercise.getDescription());
                        }
                        System.out.println("Podaj id zadania, do którergo chcesz dodać rozwiązanie");
                        int exerciseId = Integer.parseInt(scanner1.nextLine());
                        System.out.println("Podaj rozwiązanie zadania");
                        String exerciseSolution = scanner1.nextLine();
                        Solution solution = Solution.loadByUserAndExerciseId(connection, exerciseId, userId);
                        solution.setDescription(exerciseSolution);
                        solution.saveToDB(connection);
                    }
                    if (option.equals("view")) {
                        Solution[] solutions = Solution.loadAllByUserId(connection, userId);
                        for (Solution solution : solutions) {
                            System.out.println(solution.getExercise_id() + " - " + solution.getCreated() + " - " + solution.getUpdated() + " - " + solution.getDescription());
                        }
                    }
                } catch (NullPointerException e){
                    System.out.println("To zadanie ma już rozwiązanie");
                    continue;
                }
            }
        } catch (SQLException e){
	        e.printStackTrace();
        }
    }
}
