package model;

import login.LoginFrame;
import java.sql.Connection;
import java.sql.Statement;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DatenbankBewerbungenGUI extends JFrame implements ActionListener {
    // Hauptmethode zum Starten der Anwendung

    // GUI-Komponenten
    private JButton lesenButton;
    private JTextField nameField;
    private JTextField datumField;
    private JTextField adresseField;
    private JButton hinzufugenButton;
    private JTable datenTabelle;
    private JButton loschenButton;
    private JButton abgesagtButton;

    // JDBC-Verbindung
    private Connection connection;
    private Statement statement;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }

    // Konstruktor
    public DatenbankBewerbungenGUI(Connection connection) {
        this.connection = connection;
        try {
            this.statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // GUI initialisieren
        setTitle("Datenbank Bewerbung GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Größenänderung deaktivieren
        setResizable(false);
        // Erscheinen auf dem Display
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int frameWidth = getWidth();
        int frameHeight = getHeight();
        // Anpassung hier Mitte vom Bildschirm starten
        int frameX = (int) ((screenWidth - frameWidth) / 3);
        int frameY = (int) ((screenHeight - frameHeight) / 7);
        setLocation(frameX, frameY);

        // Menüleiste erstellen
        JMenuBar menuBar = new JMenuBar();

        // Menü "File" erstellen
        JMenu fileMenu = new JMenu("File");

        // Menüelement "Exit" erstellen
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Hier den Code einfügen, der beim Klicken auf "Exit" ausgeführt werden soll
                System.exit(0);
            }
        }); // Menüelement "Exit" erstellen
        JMenuItem loginMenuItem = new JMenuItem("Login");
        loginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                           }
        });

        // Menüelemente zum Menü "File" hinzufügen
        fileMenu.add(loginMenuItem);
        fileMenu.add(exitMenuItem);

        // Menü "File" zur Menüleiste hinzufügen
        menuBar.add(fileMenu);

        // Menüleiste zum JFrame hinzufügen
        setJMenuBar(menuBar);

        // GridBagLayout verwenden
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10); // Abstand zwischen den Komponenten

        lesenButton = new JButton("Tabelle auslesen");
        lesenButton.addActionListener(this);
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(lesenButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(new JLabel("Name:"), constraints);

        nameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(new JLabel("Datum:"), constraints);

        datumField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        add(datumField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(new JLabel("Adresse:"), constraints);

        adresseField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        add(adresseField, constraints);

        abgesagtButton = new JButton("Abgesagt");
        abgesagtButton.addActionListener(this);
        constraints.gridx = 0;
        constraints.gridy = 4;
        add(abgesagtButton, constraints);

        hinzufugenButton = new JButton("Daten hinzufügen");
        hinzufugenButton.addActionListener(this);
        constraints.gridx = 1;
        constraints.gridy = 4;
        add(hinzufugenButton, constraints);

        loschenButton = new JButton("Felder löschen");
        loschenButton.addActionListener(this);
        constraints.gridx = 2;
        constraints.gridy = 4;
        add(loschenButton, constraints);

        datenTabelle = new JTable();
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(datenTabelle), constraints);

        // Größe des JFrame automatisch anpassen
        pack();
    }


    // ActionListener-Methode für die Buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lesenButton) {
            lesenTabelle();
        } else if (e.getSource() == hinzufugenButton) {
            hinzufugenDatensatz();
        } else if (e.getSource() == loschenButton) {
            loschenDatensatz();
        } else if (e.getSource() == abgesagtButton) {
            setzeAbgesagt();
        }
    }

    // Methode zum Auslesen der Tabelle mit Sortierung
    public void lesenTabelle() {
        try {
            String query = "SELECT * FROM firmen";
            ResultSet resultSet = statement.executeQuery(query);

            // ResultSet abrufen
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Spaltennamen für die Tabelle erstellen
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Daten erstellen
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            // Tabelle aktualisieren
            datenTabelle.setModel(tableModel);

            // TableRowSorter hinzufügen und sortieren
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            datenTabelle.setRowSorter(sorter);
            // Sortiere nach der ersten Spalte (Index 0)
            sorter.toggleSortOrder(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Hinzufügen einesDatensatzes
    private void hinzufugenDatensatz() {
        String name = nameField.getText();
        String datumString = datumField.getText();
        String adresse = adresseField.getText();

        try {
            // Convert the date string to a java.sql.Date object
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date parsedDate = format.parse(datumString);
            java.sql.Date datum = new java.sql.Date(parsedDate.getTime());

            // Check for duplicate entries
            String duplicateQuery = "SELECT COUNT(*) FROM firmen WHERE name = ? AND datum = ? AND adresse = ?";
            PreparedStatement duplicateStatement = connection.prepareStatement(duplicateQuery);
            duplicateStatement.setString(1, name);
            duplicateStatement.setDate(2, datum);
            duplicateStatement.setString(3, adresse);
            ResultSet duplicateResult = duplicateStatement.executeQuery();
            duplicateResult.next();
            int duplicateCount = duplicateResult.getInt(1);

            if (duplicateCount > 0) {
                JOptionPane.showMessageDialog(this, "Duplicate entry found. Entry not added.");
                return;
            }

            // Insert the new record
            String insertQuery = "INSERT INTO firmen (name, datum, adresse, abgesagt) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, name);
            insertStatement.setDate(2, datum);
            insertStatement.setString(3, adresse);
            // Wert für die Spalte "abgesagt" festlegen
            insertStatement.setString(4, "nein");
            insertStatement.executeUpdate();


            // Data fields reset
            nameField.setText("");
            datumField.setText("");
            adresseField.setText("");

            // Update the table
            lesenTabelle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //lösche Daten
    private void loschenDatensatz() {
        int selectedRow = datenTabelle.getSelectedRow();
        if (selectedRow != -1) {
            int columnCount = datenTabelle.getColumnCount();
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rowData[i] = datenTabelle.getValueAt(selectedRow, i);
            }

            try {
                String deleteQuery = "DELETE FROM firmen WHERE name = ? AND datum = ? AND adresse = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setString(1, rowData[0].toString());
                deleteStatement.setDate(2, (java.sql.Date) rowData[1]);
                deleteStatement.setString(3, rowData[2].toString());
                deleteStatement.executeUpdate();
                // Tabelle aktualisieren
                lesenTabelle();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Abgesagt
    private void setzeAbgesagt() {
        int selectedRow = datenTabelle.getSelectedRow();
        if (selectedRow != -1) {
            String name = datenTabelle.getValueAt(selectedRow, 0).toString();

            try {
                String updateQuery = "UPDATE firmen SET abgesagt = 'ja' WHERE name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, name);
                updateStatement.executeUpdate();
                // Tabelle aktualisieren
                lesenTabelle();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}