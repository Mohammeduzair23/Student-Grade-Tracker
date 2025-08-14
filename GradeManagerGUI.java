import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

class GStudent {
    String name;
    double score;
    GStudent(String name, double score) { this.name = name.trim(); this.score = score; }
}

public class GradeManagerGUI extends JFrame {
    private final ArrayList<GStudent> students = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Score"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField nameField = new JTextField();
    private final JTextField scoreField = new JTextField();
    private final JLabel summaryLabel = new JLabel("Summary: —");

    public GradeManagerGUI() {
        super("Student Grade Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        JButton addBtn = new JButton("Add");
        inputPanel.add(addBtn);
        inputPanel.add(new JLabel("Score:"));
        inputPanel.add(scoreField);
        JButton updateBtn = new JButton("Update Selected");
        inputPanel.add(updateBtn);

        // Center table
        JScrollPane scroll = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Bottom panel with actions + summary
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        JButton deleteBtn = new JButton("Delete Selected");
        bottom.add(deleteBtn, BorderLayout.WEST);
        bottom.add(summaryLabel, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        refreshSummary();
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return;
        }
        Double score = parseScore(scoreField.getText());
        if (score == null) return;
        students.add(new GStudent(name, score));
        model.addRow(new Object[]{name, String.format(Locale.US, "%.2f", score)});
        nameField.setText("");
        scoreField.setText("");
        refreshSummary();
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = (String) model.getValueAt(row, 0);
        Double score = parseScore(scoreField.getText());
        if (score == null) {
            // allow updating just the name if score field blank is intended? keep simple: require valid score
            return;
        }
        students.get(row).name = name;
        students.get(row).score = score;
        model.setValueAt(name, row, 0);
        model.setValueAt(String.format(Locale.US, "%.2f", score), row, 1);
        refreshSummary();
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        students.remove(row);
        model.removeRow(row);
        refreshSummary();
    }

    private void refreshSummary() {
        if (students.isEmpty()) {
            summaryLabel.setText("Summary: No data.");
            return;
        }
        int count = students.size();
        double sum = students.stream().mapToDouble(s -> s.score).sum();
        double avg = sum / count;
        var max = students.stream().max(Comparator.comparingDouble(s -> s.score)).get();
        var min = students.stream().min(Comparator.comparingDouble(s -> s.score)).get();
        summaryLabel.setText(String.format(Locale.US,
                "Summary: Count=%d | Avg=%.2f | Highest=%.2f (%s) | Lowest=%.2f (%s)",
                count, avg, max.score, max.name, min.score, min.name));
    }

    private Double parseScore(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid number for score (e.g., 87 or 92.5).");
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradeManagerGUI().setVisible(true));
    }
}
