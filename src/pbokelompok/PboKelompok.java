package pbokelompok;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PboKelompok extends JFrame {
    private JTextField tfNoPendaftaran, tfNISN, tfNama, tfAlamat, tfTempatTglLahir, tfAsalSekolah, tfTahunLulusan;
    private JComboBox<String> cbKelamin;
    private JTable table;
    private DefaultTableModel model;
    
    public PboKelompok() {
        setTitle("Form Pendaftaran Siswa");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Main Panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel Form with better layout
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Form Pendaftaran"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add components with GridBagLayout
        addFormField(panelForm, "No Pendaftaran:", tfNoPendaftaran = new JTextField(), gbc, 0);
        addFormField(panelForm, "NISN:", tfNISN = new JTextField(), gbc, 1);
        addFormField(panelForm, "Nama Lengkap:", tfNama = new JTextField(), gbc, 2);
        
        cbKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        addFormField(panelForm, "Jenis Kelamin:", cbKelamin, gbc, 3);
        
        addFormField(panelForm, "Alamat:", tfAlamat = new JTextField(), gbc, 4);
        addFormField(panelForm, "Tempat & Tgl Lahir:", tfTempatTglLahir = new JTextField(), gbc, 5);
        addFormField(panelForm, "Asal Sekolah:", tfAsalSekolah = new JTextField(), gbc, 6);
        addFormField(panelForm, "Tahun Lulusan:", tfTahunLulusan = new JTextField(), gbc, 7);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnSubmit);

        // Add button panel to form
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(buttonPanel, gbc);

        // Table Panel
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"No Pendaftaran", "NISN", "Nama", "Kelamin", "Alamat", "Tempat Tgl Lahir", "Asal Sekolah", "Tahun Lulusan"});
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Data Siswa"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Add components to main panel
        mainPanel.add(panelForm, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Event Listener Submit
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanData();
            }
        });

        tampilkanData();
    }
    
    private void simpanData() {
        String sql = "INSERT INTO tb_siswa (no_pendaftaran, nisn, nama_lengkap, kelamin, alamat, tempat_tgl_lahir, asal_sekolah, tahun_lulusan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(tfNoPendaftaran.getText()));
            stmt.setInt(2, Integer.parseInt(tfNISN.getText()));
            stmt.setString(3, tfNama.getText());
            stmt.setString(4, cbKelamin.getSelectedItem().toString());
            stmt.setString(5, tfAlamat.getText());
            stmt.setString(6, tfTempatTglLahir.getText());
            stmt.setString(7, tfAsalSekolah.getText());
            stmt.setInt(8, Integer.parseInt(tfTahunLulusan.getText()));
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
            resetForm();
            tampilkanData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void tampilkanData() {
        model.setRowCount(0);
        String sql = "SELECT * FROM tb_siswa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("no_pendaftaran"),
                    rs.getInt("nisn"),
                    rs.getString("nama_lengkap"),
                    rs.getString("kelamin"),
                    rs.getString("alamat"),
                    rs.getString("tempat_tgl_lahir"),
                    rs.getString("asal_sekolah"),
                    rs.getInt("tahun_lulusan")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void resetForm() {
        tfNoPendaftaran.setText("");
        tfNISN.setText("");
        tfNama.setText("");
        cbKelamin.setSelectedIndex(0);
        tfAlamat.setText("");
        tfTempatTglLahir.setText("");
        tfAsalSekolah.setText("");
        tfTahunLulusan.setText("");
    }
    
    // Helper method to add form fields
    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(new Dimension(200, 25));
        }
        panel.add(field, gbc);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PboKelompok().setVisible(true));
    }
}

class DatabaseConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/pbo_kelompok", "root", "");
    }
}