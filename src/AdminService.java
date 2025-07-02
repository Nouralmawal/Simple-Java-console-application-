import java.awt.GridLayout;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import interfaces.CRUDService;

public class AdminService implements CRUDService<Student> {
    private Connection conn;

    public AdminService(Connection conn) {
        this.studentSystem = new MainClass();
        this.conn = conn;
        createAdminTable();
        createStudentTable();
    }

    public void createAdminTable() {
        String sql = "CREATE TABLE IF NOT EXISTS admin (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createStudentTable() {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "courses TEXT," +
                "midterm REAL," +
                "final REAL," +
                "gpa REAL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show dialog and add student
    public void showAddStudentDialog(JFrame parent, Runnable onSuccess) {
        JDialog dialog = new JDialog(parent, "Add Student", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField coursesField = new JTextField();
        JTextField midtermField = new JTextField();
        JTextField finalField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Courses:"));
        dialog.add(coursesField);
        dialog.add(new JLabel("Midterm:"));
        dialog.add(midtermField);
        dialog.add(new JLabel("Final:"));
        dialog.add(finalField);

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(addButton);
        dialog.add(cancelButton);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String courses = coursesField.getText();
                double midterm = Double.parseDouble(midtermField.getText());
                double finalExam = Double.parseDouble(finalField.getText());
                double gpa = (midterm * 0.4 + finalExam * 0.6);

                Student student = new Student(0, name, email, courses, midterm, finalExam, gpa);
                create(student); // Save to DB
                dialog.dispose();
                onSuccess.run(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    ///////// show dialog to update student info
    /////// 1-Capture Selected Row Data(Get data from the selected row in the table)
    ///// 2-Display the Data in a Form(Show a dialog to edit the selected student's
    ///////// data)
    //// 3-Update in Database(Pass the edited data to adminService.update())
    //
    public void showUpdateStudentDialog(JFrame parent, int studentId, Runnable onSuccess) {
        Student student = getStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(parent, "Student not found!");
            return;
        }

        JDialog dialog = new JDialog(parent, "Update Student", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JTextField nameField = new JTextField(student.name);
        JTextField emailField = new JTextField(student.email);
        JTextField coursesField = new JTextField(student.courses);
        JTextField midtermField = new JTextField(String.valueOf(student.midterm));
        JTextField finalField = new JTextField(String.valueOf(student.finalExam));

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Courses:"));
        dialog.add(coursesField);
        dialog.add(new JLabel("Midterm:"));
        dialog.add(midtermField);
        dialog.add(new JLabel("Final:"));
        dialog.add(finalField);

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(updateButton);
        dialog.add(cancelButton);

        updateButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                String courses = coursesField.getText();
                double midterm = Double.parseDouble(midtermField.getText());
                double finalExam = Double.parseDouble(finalField.getText());
                double gpa = midterm * 0.4 + finalExam * 0.6;

                Student updatedStudent = new Student(studentId, name, email, courses, midterm, finalExam, gpa);
                update(updatedStudent);
                dialog.dispose();
                onSuccess.run(); // refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    ///// helper method for updating student info
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("courses"),
                        rs.getDouble("midterm"),
                        rs.getDouble("final"),
                        rs.getDouble("gpa"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final MainClass studentSystem;

    public void loadDataToTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear table

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String courses = rs.getString("courses");
                double midterm = rs.getDouble("midterm");
                double finalExam = rs.getDouble("final");
                double gpa = rs.getDouble("gpa");

                model.addRow(new Object[] { id, name, email, courses, midterm, finalExam, gpa });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implement CRUD for Student
    @Override
    public void create(Student student) {
        String sql = "INSERT INTO students(name, email, courses, midterm, final, gpa) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.name);
            pstmt.setString(2, student.email);
            pstmt.setString(3, student.courses);
            pstmt.setDouble(4, student.midterm);
            pstmt.setDouble(5, student.finalExam);
            pstmt.setDouble(6, student.gpa);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Student> readAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("courses"),
                        rs.getDouble("midterm"),
                        rs.getDouble("final"),
                        rs.getDouble("gpa"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Student student) {
        String sql = "UPDATE students SET name=?, email=?, courses=?, midterm=?, final=?, gpa=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.name);
            pstmt.setString(2, student.email);
            pstmt.setString(3, student.courses);
            pstmt.setDouble(4, student.midterm);
            pstmt.setDouble(5, student.finalExam);
            pstmt.setDouble(6, student.gpa);
            pstmt.setInt(7, student.id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Admin loginAdmin(String email, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("id"), email, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerAdmin(String email, String password) {
        String checkSql = "SELECT id FROM admin WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next())
                return false; // Email already exists
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO admin (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
