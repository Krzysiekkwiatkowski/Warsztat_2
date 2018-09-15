package pl.coderslab.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
	    try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/warsztaty2?useSSL=false&characterEncoding=utf8", "root", "coderslab")){
            // Write here
        } catch (SQLException e){
	        e.printStackTrace();
        }
    }
}
