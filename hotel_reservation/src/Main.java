
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    private static  final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static  final String username = "root";
    private static final String password = "Suresht@k8696";
    static  Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            System.out.println("connection made successful");
            boolean flag = true;
            while (flag){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");

                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("6. Exit");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection);
                        break;
                    case 4:
                        updateReservation(connection);
                        break;
                    case 5:
                        deleteReservation(connection);
                        break;
                    case 6:
                        System.out.println();
                        flag = false;
                        break;
                }
            }
            connection.close();
            scanner.close();
            System.out.println("Thanks for Reservation !");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private static  void reserveRoom(Connection connection)  {
        try {
            System.out.println();

            System.out.println("Enter Guest Name: ");
            scanner.nextLine();
            String guestName = scanner.nextLine();

            System.out.println("Enter Room Number: ");
            int roomNumber = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter Contact Number: ");
            String contactNumber = scanner.nextLine();

            String sql = "insert into reservation (guest_name,room_number, contact_number)" +
                    "Values('" + guestName + "'," + roomNumber + ", '" + contactNumber + "' )";

            try (Statement stmt = connection.createStatement()) {
                int afftectsRows = stmt.executeUpdate(sql);

                if (afftectsRows > 0) {
                    System.out.println("Reservation successfully!");
                } else {
                    System.out.println("Reservation failed!");
                }

            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private static void viewReservation(Connection connection){
        String sql = "select reservation_id , guest_name , room_number , contact_number, reservation_date from reservation;";

        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            System.out.println("Current Reservation: ");
            System.out.println("+----------------+-------------------+-------------+----------------+----------------------------+");
            System.out.println("| Reservation ID | Guest             | Room Number | Contact Number | Reservation Date           |");
            System.out.println("+----------------+-------------------+-------------+----------------+----------------------------+");

            while(rs.next()){
                int reservationID = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-17s | %-11d | %-14s | %-19s      |\n",
                        reservationID,guestName,roomNumber,contactNumber,reservationDate);
                rs.close();
            }
            System.out.println("+----------------+-------------------+-------------+----------------+----------------------------+");

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
    private static  void getRoomNumber(Connection connection){

        scanner.nextLine();
        System.out.println("Enter the Reservation ID: ");
        int idNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter Guest Name: ");

        String guestName = scanner.nextLine();

        String sql = "select room_number from reservation " +
                "where reservation_id = "+ idNumber +" and guest_name = '" + guestName+ "';";

        try( Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            if(rs.next()){
                int roomNumber = rs.getInt("room_number");
                System.out.println("Room Number for Reservation Id " + idNumber
                        + " And Guest " + guestName + " is: " + roomNumber);
            }
            else{
                System.out.println("Reservation not found for given Reservation Id and Guest !");
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
    private static boolean existReservation(int id, Connection connection){
        String sql = "select count(*) as reservation_count from reservation where reservation_id = "+id +" ;";
        try(Statement stmt = connection.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                int rowAfftected = rs.getInt("reservation_count");
                return rowAfftected>0;
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    private static  void updateReservation(Connection connection){
        System.out.println("Enter the Reservation ID : ");
        int reservationID = scanner.nextInt();

        if(!existReservation(reservationID,connection)){
            System.out.println("Reservation ID not Exists ");
            return;
        }

        System.out.println("Enter new guest name: ");
        scanner.nextLine();
        String guestName = scanner.nextLine();
        System.out.println("Enter new room number: ");
        int roomNumber = scanner.nextInt();
        System.out.println("Enter new contact number: ");
        scanner.nextLine();
        String contactNumber = scanner.nextLine();

        String sql = "update  reservation set guest_name = '"+guestName+"', room_number= "
                +roomNumber+ " , contact_number = '"+ contactNumber + "' "
                +" where reservation_id = " + reservationID+" ;";
        try (Statement stmt = connection.createStatement()){
            int rowAfftected = stmt.executeUpdate(sql);
            if(rowAfftected>0){
                System.out.println("Update Reservation Successfully !");
            }else{
                System.out.println("Update Reservation Failed");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
    private static void deleteReservation(Connection connection){
        System.out.println("Enter the Reservation ID you want to delete : ");
        scanner.nextLine();
        int id = scanner.nextInt();

        String sql = "delete from reservation where reservation_id = " + id;
        try(Statement stmt = connection.createStatement()){
            int rowAffected = stmt.executeUpdate(sql);
            if(rowAffected>0){
                System.out.println("Delete Reservation Successfully !");
            }else {
                System.out.println("Delete Reservation Failed !");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}