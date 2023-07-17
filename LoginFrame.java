package login;

import model.DatenbankBewerbungenGUI;
import java.sql.Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField hostnameTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel hostnameLabel = new JLabel("Hostname:");
        hostnameTextField = new JTextField(20);
        JLabel usernameLabel = new JLabel("Benutzername:");
        usernameTextField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Passwort:");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Anmelden");

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(hostnameLabel, constraints);

        constraints.gridx = 1;
        panel.add(hostnameTextField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(usernameLabel, constraints);

        constraints.gridx = 1;
        panel.add(usernameTextField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostname = hostnameTextField.getText();
                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());
                // Verbindung herstellen und prüfen
                connection = login(hostname, username, password);
                if (connection != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Erfolgreich eingeloggt!");

                    // Verbindung an DatenbankBewerbungenGUI übergeben und öffnen
                    DatenbankBewerbungenGUI bewerbungenGUI = new DatenbankBewerbungenGUI(connection);
                    bewerbungenGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Ungültige Anmeldedaten.");
                }
            }
        });

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Connection login(String hostname, String username, String password) {
        String jdbcUrl = "jdbc:mysql://" + hostname + ":3306/bewerbung";
        String dbUsername = "root";
        String dbPassword = "root";

        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            String query = "SELECT COUNT(*) FROM firmen WHERE name = ? AND datum = ? AND adresse = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, jdbcUrl);

            ResultSet resultSet = statement.executeQuery();
            boolean loggedIn = resultSet.next();

            if (loggedIn) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        DatenbankBewerbungenGUI bewerbungenGUI = new DatenbankBewerbungenGUI(conn);
                        bewerbungenGUI.setVisible(true);
                        LoginFrame.this.dispose();
                    }
                });
                // Rückgabe der Verbindungsinstanz
                return conn;
            } else {
                // Schließen der Verbindung bei ungültigen Anmeldedaten
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
