package pbokelompok;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Kelas utama aplikasi pendaftaran siswa yang mewarisi JFrame
 * Kelas ini menangani tampilan form pendaftaran dan tabel data siswa
 */
public class PboKelompok extends JFrame {
    // Deklarasi komponen-komponen form yang akan digunakan di seluruh kelas
    private JTextField tfNoPendaftaran, tfAgama, tfNama, tfAlamat, tfTempatTglLahir, tfAsalSekolah, tfTahunLulusan;
    private JComboBox<String> cbKelamin;
    private JComboBox<String> cbJurusan;
    private JTable table;
    private DefaultTableModel model;
    
    // Definisi warna-warna yang akan digunakan dalam aplikasi untuk konsistensi tampilan
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);    // Warna utama: Steel Blue
    private final Color SECONDARY_COLOR = new Color(176, 196, 222); // Warna sekunder: Light Steel Blue
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Warna latar: Alice Blue
    private final Color TEXT_COLOR = new Color(25, 25, 112);        // Warna teks: Midnight Blue
    
    /**
     * Konstruktor kelas PboKelompok
     * Mengatur semua komponen GUI dan layoutnya
     */
    public PboKelompok() {
        // Mengatur properti dasar frame/jendela utama
        setTitle("Form Pendaftaran Siswa");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Menutup aplikasi saat tombol close diklik
        setLocationRelativeTo(null); // Menempatkan jendela di tengah layar
        setLayout(new BorderLayout(10, 10)); // Mengatur layout dengan jarak 10 pixel
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Membuat panel utama dengan padding untuk margin
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Panel wrapper untuk form yang dibagi menjadi dua bagian (kiri dan kanan)
        JPanel formWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        formWrapper.setBackground(BACKGROUND_COLOR);

        // Membuat dua panel form dengan judul yang berbeda
        JPanel leftFormPanel = createFormPanel("Data Pribadi");
        JPanel rightFormPanel = createFormPanel("Data Pribadi");

        // Mengatur constraint untuk layout grid bag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Komponen mengisi ruang horizontal
        gbc.insets = new Insets(8, 8, 8, 8); // Padding untuk setiap komponen
        gbc.anchor = GridBagConstraints.WEST; // Rata kiri untuk label

        // Menambahkan komponen-komponen ke panel kiri (Data Pribadi)
        addFormField(leftFormPanel, "No Pendaftaran:", tfNoPendaftaran = createStyledTextField(), gbc, 0);
        addFormField(leftFormPanel, "Agama:", tfAgama = createStyledTextField(), gbc, 1);
        addFormField(leftFormPanel, "Nama Lengkap:", tfNama = createStyledTextField(), gbc, 2);
        
        // Membuat dan menambahkan combo box jenis kelamin
        cbKelamin = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        styleComboBox(cbKelamin);
        addFormField(leftFormPanel, "Jenis Kelamin:", cbKelamin, gbc, 3);

        // Menambahkan komponen-komponen ke panel kanan (Data Sekolah)
        addFormField(rightFormPanel, "Alamat:", tfAlamat = createStyledTextField(), gbc, 0);
        addFormField(rightFormPanel, "Tempat & Tgl Lahir:", tfTempatTglLahir = createStyledTextField(), gbc, 1);
        addFormField(rightFormPanel, "Asal Sekolah:", tfAsalSekolah = createStyledTextField(), gbc, 2);
        
        // Membuat dan menambahkan combo box jurusan
        cbJurusan = new JComboBox<>(new String[]{"RPL", "TKJ", "MM", "AN"});
        styleComboBox(cbJurusan);
        addFormField(rightFormPanel, "Jurusan:", cbJurusan, gbc, 3);
        
        addFormField(rightFormPanel, "Tahun Lulusan:", tfTahunLulusan = createStyledTextField(), gbc, 4);

        // Menggabungkan kedua panel form ke dalam wrapper
        formWrapper.add(leftFormPanel);
        formWrapper.add(rightFormPanel);

        // Membuat panel untuk tombol submit
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton btnSubmit = createStyledButton("Submit");
        buttonPanel.add(btnSubmit);

        // Panel yang menampung form dan tombol submit
        JPanel mainFormPanel = new JPanel(new BorderLayout());
        mainFormPanel.setBackground(BACKGROUND_COLOR);
        mainFormPanel.add(formWrapper, BorderLayout.CENTER);
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Inisialisasi dan konfigurasi tabel data siswa
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"No Pendaftaran", "Agama", "Nama", "Kelamin", "Alamat", 
            "Tempat Tgl Lahir", "Asal Sekolah", "Jurusan", "Tahun Lulusan"});
        table = new JTable(model);
        styleTable(table);
        
        // Membuat scroll pane untuk tabel dengan border dan judul
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2), "Data Siswa",
                    javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Menambahkan semua komponen ke panel utama
        mainPanel.add(mainFormPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Menambahkan panel utama ke frame
        add(mainPanel);

        // Menambahkan event listener untuk tombol submit
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanData(); // Memanggil method untuk menyimpan data
            }
        });

        // Menampilkan data yang sudah ada di database
        tampilkanData();
    }

    /**
     * Method untuk membuat panel form dengan style yang konsisten
     * @param title Judul panel yang akan ditampilkan
     * @return JPanel yang sudah distyle
     */
    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                title,
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    /**
     * Method untuk membuat text field dengan style yang konsisten
     * @return JTextField yang sudah distyle
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        return field;
    }

    /**
     * Method untuk memberikan style pada combo box
     * @param comboBox ComboBox yang akan distyle
     */
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
    }

    /**
     * Method untuk membuat tombol dengan style yang konsisten
     * @param text Teks yang akan ditampilkan pada tombol
     * @return JButton yang sudah distyle
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Method untuk memberikan style pada tabel
     * @param table Tabel yang akan distyle
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(SECONDARY_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
    }

    /**
     * Method untuk menambahkan field ke form panel
     * @param panel Panel tempat field akan ditambahkan
     * @param labelText Teks label untuk field
     * @param component Komponen yang akan ditambahkan (TextField/ComboBox)
     * @param gbc GridBagConstraints untuk layout
     * @param row Posisi baris dalam grid
     */
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
    
    /**
     * Method untuk menyimpan data ke database
     * Mengambil nilai dari semua field dan menyimpannya ke database
     */
    private void simpanData() {
        String sql = "INSERT INTO tb_siswa (no_pendaftaran, agama, nama_lengkap, kelamin, alamat, " +
                    "tempat_tgl_lahir, asal_sekolah, jurusan, tahun_lulusan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Mengisi prepared statement dengan nilai dari form
            stmt.setInt(1, Integer.parseInt(tfNoPendaftaran.getText()));
            stmt.setString(2, tfAgama.getText());
            stmt.setString(3, tfNama.getText());
            stmt.setString(4, cbKelamin.getSelectedItem().toString());
            stmt.setString(5, tfAlamat.getText());
            stmt.setString(6, tfTempatTglLahir.getText());
            stmt.setString(7, tfAsalSekolah.getText());
            stmt.setString(8, cbJurusan.getSelectedItem().toString());
            stmt.setInt(9, Integer.parseInt(tfTahunLulusan.getText()));
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan");
            resetForm();
            tampilkanData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    /**
     * Method untuk menampilkan data dari database ke tabel
     */
    private void tampilkanData() {
        model.setRowCount(0); // Mengosongkan tabel
        String sql = "SELECT * FROM tb_siswa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("no_pendaftaran"),
                    rs.getString("agama"),
                    rs.getString("nama_lengkap"),
                    rs.getString("kelamin"),
                    rs.getString("alamat"),
                    rs.getString("tempat_tgl_lahir"),
                    rs.getString("asal_sekolah"),
                    rs.getString("jurusan"),
                    rs.getInt("tahun_lulusan")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    /**
     * Method untuk mengosongkan semua field setelah data disimpan
     */
    private void resetForm() {
        tfNoPendaftaran.setText("");
        tfAgama.setText("");
        tfNama.setText("");
        cbKelamin.setSelectedIndex(0);
        tfAlamat.setText("");
        tfTempatTglLahir.setText("");
        tfAsalSekolah.setText("");
        cbJurusan.setSelectedIndex(0);
        tfTahunLulusan.setText("");
    }
    
    /**
     * Method main untuk menjalankan aplikasi
     */
    public static void main(String[] args) {
        // Menjalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new PboKelompok().setVisible(true));
    }
}

/**
 * Kelas untuk menangani koneksi database
 */
class DatabaseConnection {
    /**
     * Method untuk mendapatkan koneksi ke database
     * @return Connection object untuk koneksi ke database
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/pbo_kelompok", "root", "");
    }
}