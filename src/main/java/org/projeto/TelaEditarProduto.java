package org.projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaEditarProduto extends JFrame {

    // ================= CONSTANTES =================
    private static final String ROUPA = "ROUPA";
    private static final String TENIS = "TENIS";
    private static final String IMAGES_COPY_DIRECTORY = "imagens_produtos_copiados";

    // ================= TABELA =================
    private JTable produtosTable;
    private DefaultTableModel tableModel;

    // ================= CAMPOS =================
    private JTextField nomeProdutoField;
    private JComboBox<String> tipoProdutoComboBox;
    private JTextField valorField;
    private JTextArea descricaoArea;

    // roupas
    private JTextField quantidadePField;
    private JTextField quantidadeMField;
    private JTextField quantidadeGField;

    // tenis
    private JTextField quantidade38Field;
    private JTextField quantidade39Field;
    private JTextField quantidade40Field;
    private JTextField quantidade41Field;
    private JTextField quantidade42Field;

    // imagens
    private JButton imagem1Button;
    private JButton imagem2Button;
    private JButton imagem3Button;
    private final List<String> newImagePaths = new ArrayList<>();

    // ações
    private JButton salvarButton;
    private JButton removerButton;

    // controle
    private JPanel tamanhosPanel;
    private CardLayout cardLayout;
    private int selectedProductId = -1;
    private String currentImagePath1;
    private String currentImagePath2;
    private String currentImagePath3;

    public TelaEditarProduto() {
        configurarJanela();
        inicializarComponentes();
        montarTela();
        configurarEventos();
        carregarProdutos();
        setVisible(true);
    }

    // ================= CONFIG =================
    private void configurarJanela() {
        setTitle("Editar Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        Color fundo = new Color(180, 255, 180);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        setContentPane(painelPrincipal);

        JLabel titulo = new JLabel("Editar Produtos - EcoBazar");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        painelPrincipal.add(titulo, BorderLayout.NORTH);
    }
    private void inicializarComponentes() {
        for (int i = 0; i < 3; i++) newImagePaths.add(null);

        criarTabela();
        criarCampos();
    }

    private void criarTabela() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Tipo", "Valor", "Descrição", "Img1", "Img2", "Img3"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        produtosTable = new JTable(tableModel);
    }

    private void criarCampos() {
        nomeProdutoField = new JTextField(30);
        tipoProdutoComboBox = new JComboBox<>(new String[]{ROUPA, TENIS});
        valorField = new JTextField(10);
        descricaoArea = new JTextArea(5, 20);

        imagem1Button = new JButton("Alterar Imagem 1");
        imagem2Button = new JButton("Alterar Imagem 2");
        imagem3Button = new JButton("Alterar Imagem 3");

        salvarButton = new JButton("Salvar Alterações");
        removerButton = new JButton("Remover Produto");

        cardLayout = new CardLayout();
        tamanhosPanel = new JPanel(cardLayout);
        tamanhosPanel.add(criarPainelRoupas(), ROUPA);
        tamanhosPanel.add(criarPainelTenis(), TENIS);

        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);

        nomeProdutoField.setFont(new Font("Arial", Font.PLAIN, 14));
        valorField.setFont(new Font("Arial", Font.PLAIN, 14));

        salvarButton.setFont(new Font("Arial", Font.BOLD, 13));
        removerButton.setFont(new Font("Arial", Font.BOLD, 13));
    }

    // ================= UI =================
    private void montarTela() {

        Color fundo = new Color(180, 255, 180);

        JPanel painelCentral = new JPanel(new GridLayout(1, 2, 20, 20));
        painelCentral.setBackground(fundo);

        // ================= TABELA =================
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setBackground(fundo);
        tabelaPanel.setBorder(
                BorderFactory.createTitledBorder("Produtos Cadastrados")
        );

        produtosTable.setRowHeight(25);

        JScrollPane tabelaScroll = new JScrollPane(produtosTable);
        tabelaPanel.add(tabelaScroll);

        // ================= FORM =================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fundo);
        form.setBorder(
                BorderFactory.createTitledBorder("Editar Produto")
        );

        int linha = 0;

        adicionarCampo(form, "Nome:", nomeProdutoField, linha++);
        adicionarCampo(form, "Tipo:", tipoProdutoComboBox, linha++);
        adicionarComponente(form, tamanhosPanel, linha++);
        adicionarCampo(form, "Valor:", valorField, linha++);
        adicionarCampo(form,
                "Descrição:",
                new JScrollPane(descricaoArea),
                linha++
        );

        adicionarCampo(form, "Imagem 1:", imagem1Button, linha++);
        adicionarCampo(form, "Imagem 2:", imagem2Button, linha++);
        adicionarCampo(form, "Imagem 3:", imagem3Button, linha++);

        JPanel botoes = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        botoes.setBackground(fundo);

        salvarButton.setPreferredSize(
                new Dimension(180, 35)
        );

        removerButton.setPreferredSize(
                new Dimension(180, 35)
        );

        botoes.add(salvarButton);
        botoes.add(removerButton);

        adicionarComponente(form, botoes, linha);

        painelCentral.add(tabelaPanel);
        painelCentral.add(form);

        add(painelCentral, BorderLayout.CENTER);
    }

    private void adicionarCampo(JPanel panel, String label, Component component, int linha) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = linha;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private void adicionarComponente(JPanel panel, Component component, int linha) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        panel.add(component, gbc);
    }

    private JPanel criarPainelRoupas() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(180,255,180));

        quantidadePField = new JTextField(5);
        quantidadeMField = new JTextField(5);
        quantidadeGField = new JTextField(5);

        adicionarCampo(panel, "Qtd P:", quantidadePField, 0);
        adicionarCampo(panel, "Qtd M:", quantidadeMField, 1);
        adicionarCampo(panel, "Qtd G:", quantidadeGField, 2);

        return panel;
    }

    private JPanel criarPainelTenis() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(180,255,180));

        quantidade38Field = new JTextField(5);
        quantidade39Field = new JTextField(5);
        quantidade40Field = new JTextField(5);
        quantidade41Field = new JTextField(5);
        quantidade42Field = new JTextField(5);

        adicionarCampo(panel, "Qtd 38:", quantidade38Field, 0);
        adicionarCampo(panel, "Qtd 39:", quantidade39Field, 1);
        adicionarCampo(panel, "Qtd 40:", quantidade40Field, 2);
        adicionarCampo(panel, "Qtd 41:", quantidade41Field, 3);
        adicionarCampo(panel, "Qtd 42:", quantidade42Field, 4);

        return panel;
    }

    // ================= EVENTOS =================
    private void configurarEventos() {
        tipoProdutoComboBox.addActionListener(e ->
                cardLayout.show(tamanhosPanel, (String) tipoProdutoComboBox.getSelectedItem()));

        imagem1Button.addActionListener(e -> selecionarNovaImagem(0));
        imagem2Button.addActionListener(e -> selecionarNovaImagem(1));
        imagem3Button.addActionListener(e -> selecionarNovaImagem(2));

        salvarButton.addActionListener(e -> salvarAlteracoesProduto());
        removerButton.addActionListener(e -> removerProduto());

        produtosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && produtosTable.getSelectedRow() != -1) {
                selectedProductId = (int) tableModel.getValueAt(produtosTable.getSelectedRow(), 0);
                carregarDetalhesProduto();
            }
        });
    }

    // ================= BANCO =================
    private void carregarProdutos() {
        tableModel.setRowCount(0);

        String sql = "SELECT * FROM produtos ORDER BY id";

        try (Connection conn = new DBConnector().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo_produto"),
                        rs.getDouble("valor"),
                        rs.getString("descricao"),
                        rs.getString("imagens1_path"),
                        rs.getString("imagens2_path"),
                        rs.getString("imagens3_path")
                });
            }

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void carregarDetalhesProduto() {
        String sql = "SELECT * FROM produtos WHERE id=?";

        try (Connection conn = new DBConnector().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedProductId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nomeProdutoField.setText(rs.getString("nome"));
                tipoProdutoComboBox.setSelectedItem(rs.getString("tipo_produto"));
                valorField.setText(String.valueOf(rs.getDouble("valor")));
                descricaoArea.setText(rs.getString("descricao"));

                currentImagePath1 = rs.getString("imagens1_path");
                currentImagePath2 = rs.getString("imagens2_path");
                currentImagePath3 = rs.getString("imagens3_path");
            }

            carregarEstoque(conn);

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar produto: " + e.getMessage());
        }
    }

    private void carregarEstoque(Connection conn) throws SQLException {
        limparCamposQuantidade();

        String sql = "SELECT tamanho_descricao, quantidade FROM estoque_variacoes WHERE produto_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, selectedProductId);
            ResultSet rs = stmt.executeQuery();

            Map<String, Integer> estoque = new HashMap<>();
            while (rs.next()) {
                estoque.put(rs.getString("tamanho_descricao"), rs.getInt("quantidade"));
            }

            quantidadePField.setText(String.valueOf(estoque.getOrDefault("P", 0)));
            quantidadeMField.setText(String.valueOf(estoque.getOrDefault("M", 0)));
            quantidadeGField.setText(String.valueOf(estoque.getOrDefault("G", 0)));
            quantidade38Field.setText(String.valueOf(estoque.getOrDefault("38", 0)));
            quantidade39Field.setText(String.valueOf(estoque.getOrDefault("39", 0)));
            quantidade40Field.setText(String.valueOf(estoque.getOrDefault("40", 0)));
            quantidade41Field.setText(String.valueOf(estoque.getOrDefault("41", 0)));
            quantidade42Field.setText(String.valueOf(estoque.getOrDefault("42", 0)));
        }
    }

    private void salvarAlteracoesProduto() {
        if (selectedProductId == -1) {
            mostrarErro("Selecione um produto.");
            return;
        }

        try (Connection conn = new DBConnector().conectar()) {
            conn.setAutoCommit(false);

            atualizarProduto(conn);
            atualizarEstoque(conn);

            conn.commit();
            mostrarSucesso("Produto atualizado com sucesso!");
            carregarProdutos();
            limparCampos();

        } catch (Exception e) {
            mostrarErro("Erro ao salvar: " + e.getMessage());
        }
    }

    private void atualizarProduto(Connection conn) throws SQLException, IOException {
        String sql = "UPDATE produtos SET nome=?, tipo_produto=?, valor=?, descricao=?, imagens1_path=?, imagens2_path=?, imagens3_path=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProdutoField.getText());
            stmt.setString(2, (String) tipoProdutoComboBox.getSelectedItem());
            stmt.setDouble(3, Double.parseDouble(valorField.getText().replace(",", ".")));
            stmt.setString(4, descricaoArea.getText());
            stmt.setString(5, obterImagemFinal(0, currentImagePath1));
            stmt.setString(6, obterImagemFinal(1, currentImagePath2));
            stmt.setString(7, obterImagemFinal(2, currentImagePath3));
            stmt.setInt(8, selectedProductId);
            stmt.executeUpdate();
        }
    }

    private void atualizarEstoque(Connection conn) throws SQLException {
        try (PreparedStatement delete = conn.prepareStatement("DELETE FROM estoque_variacoes WHERE produto_id=?")) {
            delete.setInt(1, selectedProductId);
            delete.executeUpdate();
        }

        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO estoque_variacoes(produto_id,tamanho_descricao,quantidade) VALUES (?,?,?)")) {

            if (tipoProdutoComboBox.getSelectedItem().equals(ROUPA)) {
                inserirEstoque(insert, "P", quantidadePField.getText());
                inserirEstoque(insert, "M", quantidadeMField.getText());
                inserirEstoque(insert, "G", quantidadeGField.getText());
            } else {
                inserirEstoque(insert, "38", quantidade38Field.getText());
                inserirEstoque(insert, "39", quantidade39Field.getText());
                inserirEstoque(insert, "40", quantidade40Field.getText());
                inserirEstoque(insert, "41", quantidade41Field.getText());
                inserirEstoque(insert, "42", quantidade42Field.getText());
            }

            insert.executeBatch();
        }
    }

    private void inserirEstoque(PreparedStatement stmt, String tamanho, String qtd) throws SQLException {
        stmt.setInt(1, selectedProductId);
        stmt.setString(2, tamanho);
        stmt.setInt(3, Integer.parseInt(qtd));
        stmt.addBatch();
    }

    private void removerProduto() {
        if (selectedProductId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja remover o produto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = new DBConnector().conectar();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM produtos WHERE id=?")) {

            stmt.setInt(1, selectedProductId);
            stmt.executeUpdate();

            mostrarSucesso("Produto removido.");
            carregarProdutos();
            limparCampos();

        } catch (SQLException e) {
            mostrarErro(e.getMessage());
        }
    }

    // ================= IMAGENS =================
    private void selecionarNovaImagem(int index) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            newImagePaths.set(index, chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private String obterImagemFinal(int index, String atual) throws IOException {
        if (newImagePaths.get(index) == null) return atual;

        File origem = new File(newImagePaths.get(index));
        Files.createDirectories(Paths.get(IMAGES_COPY_DIRECTORY));

        String nome = System.currentTimeMillis() + "_" + origem.getName();
        Path destino = Paths.get(IMAGES_COPY_DIRECTORY, nome);

        Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toString();
    }

    // ================= AUX =================
    private void limparCampos() {
        nomeProdutoField.setText("");
        valorField.setText("");
        descricaoArea.setText("");
        limparCamposQuantidade();
        produtosTable.clearSelection();
        selectedProductId = -1;
    }

    private void limparCamposQuantidade() {
        JTextField[] campos = {
                quantidadePField, quantidadeMField, quantidadeGField,
                quantidade38Field, quantidade39Field,
                quantidade40Field, quantidade41Field, quantidade42Field
        };

        for (JTextField campo : campos) {
            if (campo != null) campo.setText("0");
        }
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarSucesso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaEditarProduto::new);
    }
}
