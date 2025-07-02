import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;


public class MainClass {
   public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:students.db");

            AdminService adminService = new AdminService(conn);//// Pass the connection to the AdminService constructor

            LoginFrame loginFrame = new LoginFrame(adminService);//// Start the login frame
            loginFrame.initialize();
            // new AdminFrame(adminService); // only UI
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
