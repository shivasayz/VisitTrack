import java.sql.*;
import java.util.Scanner;

public class visitors_db {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/students";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded successfully");

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connected to database successfully.....");

                while(true){
                    Scanner sc = new Scanner(System.in);
                    System.out.println();
                    System.out.println("1. Display Visitors");
                    System.out.println("2. Add New Visitor");
                    System.out.println("3. Update Visitor Details");
                    System.out.println("4. Mark Exit Time ");
                    System.out.println("5. Exit");
                    System.out.print("Enter your Choice: ");
                    int choice = sc.nextInt();
                    System.out.println();

                    switch (choice){
                        case 1:
                            displayVisitors(connection);
                            break;

                        case 2:
                            addNewVisitor(connection,sc);
                            break;

                        case 3:
                            updateVisitorDetails(connection, sc);
                            break;

                        case 4:
                            markExitTimeOfVisitor(connection, sc);
                            break;

                        case 5:
                            System.exit(0);
                            break;

                        default:
                            System.out.println("Invalid Choice, please try again....");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void displayVisitors(Connection connection){

        String Query = "SELECT * FROM visitors";

        try {
            PreparedStatement ps = connection.prepareStatement(Query);
            ResultSet res = ps.executeQuery();

            System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");
            System.out.println("|                                                    CURRENT VISITORS                                                       |");
            System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");
            System.out.printf("| %-14s | %-15s | %-13s | %-12s | %-13s | %-13s | %-22s |\n",
                    "Serial No", "Visitor Name", "Date of Visit", "Time of Visit", "Visited From", "Gender", "Time of Exit");
            System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");

            while(res.next()){
                int sno = res.getInt("Sno");
                String name = res.getString("Name");
                String date = String.valueOf(res.getDate("Date_of_visit"));
                String time = String.valueOf(res.getTime("Time_of_visit"));
                String from = res.getString("visited_from");
                String gender = res.getString("Gender");
                String exitTime = String.valueOf(res.getTimestamp("Time_of_Exit"));

                System.out.printf("| %-14s | %-15s | %-13s | %-13s | %-13s | %-13s | %-22s |\n", sno, name, date, time, from, gender, exitTime);
            }
            System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void addNewVisitor(Connection connection, Scanner scanner){

        String Query = "INSERT INTO visitors (Name, visited_from, Gender, Date_of_visit) VALUES (?, ?, ?, ?)";

        try {
            scanner.nextLine();

            System.out.print("Enter New Visitor Name: ");
            String visitorName = scanner.nextLine().trim();

            System.out.print("Coming from: ");
            String ComingFrom = scanner.nextLine().trim();

            System.out.print("Gender: ");
            String gender = scanner.nextLine().trim();

            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            PreparedStatement ps = connection.prepareStatement(Query);
            ps.setString(1, visitorName);
            ps.setString(2, ComingFrom);
            ps.setString(3, gender);
            ps.setDate(4, currentDate);

            int rowsEffected = ps.executeUpdate();

            if (rowsEffected > 0){
                System.out.println("Visitor added successfully...");
            } else {
                System.out.println("Failed to add visitor...");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void markExitTimeOfVisitor(Connection connection, Scanner scanner){

        String Query = "UPDATE visitors SET Time_of_Exit = ? WHERE sno = ? AND Name = ?";
        java.sql.Timestamp curr_timestamp = new java.sql.Timestamp(System.currentTimeMillis());

        try {

            System.out.print("Enter Sno to mark exit: ");
            int sno = scanner.nextInt();

            scanner.nextLine();
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            if (!isExists(connection, sno)){
                System.out.println("Invalid Name or Sno, please try again...");
            } else {
                PreparedStatement ps = connection.prepareStatement(Query);
                ps.setTimestamp(1, curr_timestamp);
                ps.setInt(2, sno);
                ps.setString(3, name);
                int rowsEffected = ps.executeUpdate();

                if (rowsEffected > 0){
                    System.out.println(name+" visit bearing Sno "+sno+" Marked as Exit....");
                } else {
                    System.out.println("Failed to update the exit, please try again....");
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void updateVisitorDetails(Connection connection, Scanner scanner){

        String Query = "UPDATE visitors SET Name = ?, visited_from = ? WHERE Sno = ?";

        try {
            System.out.print("Enter Sno: ");
            int sno = scanner.nextInt();

            scanner.nextLine();

            System.out.print("Enter Visitor Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Visited From: ");
            String visitedFrom = scanner.nextLine().trim();


            if (!isExists(connection, sno)){
                System.out.println("Invalid Entries, please try again...");
            } else {
                PreparedStatement ps = connection.prepareStatement(Query);
                ps.setString(1, name);
                ps.setString(2, visitedFrom);
                ps.setInt(3, sno);
                int res = ps.executeUpdate();

                if (res > 0){
                    System.out.println("Details updated successfully....");
                } else {
                    System.out.println("Failed to update, please try again....");
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static boolean isExists(Connection connection, int sno){

        String Query = "SELECT * from visitors WHERE Sno = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(Query);
            ps.setInt(1, sno);
            ResultSet res = ps.executeQuery();

            return res.next();

        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}


