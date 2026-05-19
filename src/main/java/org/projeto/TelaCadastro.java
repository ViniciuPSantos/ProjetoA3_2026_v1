package org.projeto;


import org.projeto.DBConnector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class TelaCadastro extends JFrame {

    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JComboBox<String> tipoUsuarioComboBox;
    private JButton cadastrarButton;
    private JButton loginButton;
    private JCheckBox mostrarSenhaCheckBox;

    private static final String ADMIN_PASSWORD = "admin123";

    public TelaCadastro() {
        setTitle("Cadastro de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Linha 0: Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        add(nomeField, gbc);
        gbc.weightx = 0;

        // Linha 1: Email
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Linha 2: Senha
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        senhaField = new JPasswordField(20);
        add(senhaField, gbc);

        // Linha 3: Mostrar Senha CheckBox
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        mostrarSenhaCheckBox = new JCheckBox("Mostrar Senha");
        mostrarSenhaCheckBox.setFont(new Font("Arial", Font.PLAIN, 10));
        mostrarSenhaCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    senhaField.setEchoChar((char) 0);
                } else {
                    senhaField.setEchoChar((Character) UIManager.get("PasswordField.echoChar"));
                }
            }
        });
        add(mostrarSenhaCheckBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 4: Tipo de Usuário
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Tipo de Usuário:"), gbc);
        gbc.gridx = 1;
        String[] tipos = {"Cliente", "Bazar"};
        tipoUsuarioComboBox = new JComboBox<>(tipos);
        add(tipoUsuarioComboBox, gbc);

        // Linha 5: Botão Cadastrar
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cadastrarButton = new JButton("Cadastrar");
        add(cadastrarButton, gbc);

        // Linha 6 : Botão Já tem uma conta
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Já possui uma conta? Faça login aqui.");
        add(loginButton, gbc);


        this.getRootPane().setDefaultButton(cadastrarButton);

        loginButton.addActionListener(e -> {
            TelaLogin login = new TelaLogin();
            login.setVisible(true);
            dispose();
        });

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = nomeField.getText().trim();
                String email = emailField.getText().trim();
                String senha = new String(senhaField.getPassword());
                String tipoSelecionado = (String) tipoUsuarioComboBox.getSelectedItem();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Por favor, preencha todos os campos.", "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Por favor, insira um formato de email válido.", "Email Inválido", JOptionPane.WARNING_MESSAGE);
                    emailField.requestFocusInWindow();
                    return;
                }

                String tipoParaBanco;
                if ("bazar".equals(tipoSelecionado)) {
                    JPasswordField pf = new JPasswordField();
                    int okCxl = JOptionPane.showConfirmDialog(null, pf, "Senha de Administrador:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (okCxl == JOptionPane.OK_OPTION) {
                        String adminSenhaInserida = new String(pf.getPassword());
                        if (!adminSenhaInserida.equals(ADMIN_PASSWORD)) {
                            JOptionPane.showMessageDialog(TelaCadastro.this, "Senha de administrador incorreta. Cadastro como bazar não permitido.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        tipoParaBanco = "bazar";
                    } else {
                        JOptionPane.showMessageDialog(TelaCadastro.this, "Cadastro como bazar cancelado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } else {
                    tipoParaBanco = "cliente";
                }

                if (cadastrarUsuario(nome, email, senha, tipoParaBanco)) {
                    JOptionPane.showMessageDialog(TelaCadastro.this, "Usuário '" + nome + "' cadastrado com sucesso como " + tipoParaBanco + "!", "Cadastro Realizado", JOptionPane.INFORMATION_MESSAGE);
                    TelaCadastro.this.dispose();
                    SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
                } else {

                }
            }
        });
        setVisible(true);

        SwingUtilities.invokeLater(() -> nomeField.requestFocusInWindow());
    }

    private boolean cadastrarUsuario(String nome, String email, String senha, String tipo) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?)";
        DBConnector dbConnector = new DBConnector();

        try (Connection conn = dbConnector.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt(12));


            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senhaHash); // MUITO INSEGURO! Hashear esta senha!
            pstmt.setString(4, tipo);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar: O email '" + email + "' já está em uso.", "Email Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro no banco de dados ao tentar cadastrar: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastro());
    }
}
