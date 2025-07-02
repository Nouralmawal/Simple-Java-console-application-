import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LoginFrame extends JFrame {
    // private StudentManagementSystem system;
    private AdminService adminService;
    // global variable font to use it in labels
    final private Font loginFont = new Font("Segoe print", Font.BOLD, 18);
    // golbal variable text filed ,password filed
    JTextField tfEmail;
    JPasswordField pwfPassword;
    JLabel lbWelcome;

    public LoginFrame(AdminService adminService) {
        this.adminService = adminService;
    }

    public void initialize() {

        ///// ### form panel ###/////
        JLabel lbEmail = new JLabel("Email");
        lbEmail.setFont(loginFont);
        JLabel lbPassword = new JLabel("Password");
        lbPassword.setFont(loginFont);
        tfEmail = new JTextField();
        tfEmail.setFont(loginFont);
        pwfPassword = new JPasswordField();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 1, 5, 5));
        formPanel.setOpaque(false);
        formPanel.add(lbEmail);
        formPanel.add(tfEmail);
        formPanel.add(lbPassword);
        formPanel.add(pwfPassword);

        ///// ######welcom label ###////
        lbWelcome = new JLabel();
        lbWelcome.setFont(loginFont); /// initalization
        ///// ######buttons ###///
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(loginFont);
        btnLogin.addActionListener(new ActionListener() {
            // here we click to the quik fix then unimplement method then we got the code
            // auto-generated method
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = new String(pwfPassword.getPassword());

                Admin admin = adminService.loginAdmin(email, password);
                if (admin != null) {
                    setVisible(false);
                    AdminFrame adminFrame = new AdminFrame(adminService);
                    adminFrame.initialize();
                } else {
                    lbWelcome.setText("Invalid email or password");
                }

            }

        });

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(loginFont);
        btnRegister.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = new String(pwfPassword.getPassword());

                boolean success = adminService.registerAdmin(email, password);
                if (success) {
                    lbWelcome.setText("Registration successful. Please login.");
                } else {
                    lbWelcome.setText("Email already exists.");
                }

            }

        });

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(loginFont);
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tfEmail.setText("");
                pwfPassword.setText("");
                lbWelcome.setText("");

                // throw new UnsupportedOperationException("Unimplemented method
                // 'actionPerformed'");
            }

        });
        /// buttuns panel (the panel that will contain the buttons)
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnRegister);
        buttonsPanel.add(btnClear);

        /// our main panel (login panel)
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(new Color(128, 128, 255));
        loginPanel.add(formPanel, BorderLayout.NORTH);
        loginPanel.add(lbWelcome, BorderLayout.CENTER);// add the welcom label to the center of the main panel (login
                                                       // panel)
        loginPanel.add(buttonsPanel, BorderLayout.SOUTH); // add the buttons panel to the mainpanel (login panel) in
                                                          // south
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(loginPanel); // add main panel(login panel ) to Jframe

        setTitle("Welcome");
        setSize(500, 600);
        setMinimumSize(new Dimension(300, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }

    /********** main method */
    
/**    WE don't need a main method in LoginFrame at all.
 * public static void main(String[] args) {
        
       // AdminService adminService = new AdminService(conn);
        LoginFrame myFrame = new LoginFrame(adminService);
        myFrame.initialize();

    }*/
}
