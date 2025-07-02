import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.events.MouseEvent;

//////
/////////AdminFrame is only  responsible for UI layout&design and user interactions.
/// 
public class AdminFrame extends JFrame {
    private final Font adminFont = new Font("Segoe print", Font.BOLD, 18);
    private final Color bgColor = new Color(128, 128, 255);

    private JTable studentTable;

    private DefaultTableModel tableModel;
    private final AdminService adminService; 
   

    public AdminFrame(AdminService adminService) {
        this.adminService = adminService;
        initialize();
        loadStudentData(); // Load data from service
    }

    void initialize() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe print", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = { "ID", "Name", "Email", "Courses", "Midterm", "Final", "GPA" };
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setFont(adminFont);
        studentTable.setRowHeight(25);
        studentTable.setRowSelectionAllowed(true);

        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { // fixed import
                int row = studentTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < studentTable.getRowCount()) {
                    studentTable.setRowSelectionInterval(row, row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        buttonPanel.setOpaque(false);

        JButton addStudentButton = createStyledButton("Add Student");
        JButton updateStudentButton = createStyledButton("Update");
        JButton deleteStudentButton = createStyledButton("Delete");
        JButton logoutButton = createStyledButton("Logout");

        // Add action listeners that delegate to service methods
        addStudentButton.addActionListener(e -> {
            adminService.showAddStudentDialog(this, this::loadStudentData);
        });

        updateStudentButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                adminService.showUpdateStudentDialog(this, id, this::loadStudentData);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to update.");
            }

        });

        deleteStudentButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = studentTable.convertRowIndexToModel(selectedRow);
                int id = (int) tableModel.getValueAt(modelRow, 0);

                int confirm = JOptionPane.showConfirmDialog(
                        AdminFrame.this,
                        "Delete student with ID " + id + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    adminService.delete(id);
                    loadStudentData();
                }
            } else {
                JOptionPane.showMessageDialog(
                        AdminFrame.this,
                        "Please select a student to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        logoutButton.addActionListener(e -> {
            dispose(); // Close admin window
            new LoginFrame(adminService).initialize();
        });

        buttonPanel.add(addStudentButton);
        buttonPanel.add(updateStudentButton);
        buttonPanel.add(deleteStudentButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        setTitle("Admin Dashboard");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStudentData() {
        tableModel.setRowCount(0); // Clear table
        adminService.loadDataToTable(tableModel); // Reload from DB
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(adminFont);
        button.setBackground(new Color(180, 180, 255));
        button.setForeground(Color.BLACK);
        return button;
    }
}
