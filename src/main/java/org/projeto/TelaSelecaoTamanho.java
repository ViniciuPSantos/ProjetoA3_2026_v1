package org.projeto;

import org.projeto.DBConnector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaSelecaoTamanho extends JFrame {

    private int produtoId;
    private String nomeProduto;
    private double valorProduto;

    private JComboBox<String> tamanhoComboBox;
    private JSpinner quantidadeSpinner;
    private JButton adicionarAoCarrinhoButton;

    public TelaSelecaoTamanho(int produtoId,
                              String nomeProduto,
                              double valorProduto) {

        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.valorProduto = valorProduto;

        configurarJanela();
        montarTela();

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Selecionar Produto");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color fundo = new Color(180,255,180);

        JPanel painelPrincipal =
                new JPanel(new GridBagLayout());

        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        25,25,25,25
                )
        );

        setContentPane(painelPrincipal);
    }

    private void montarTela() {
        Color fundo = new Color(180,255,180);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo =
                new JLabel("Adicionar ao Carrinho");

        titulo.setFont(
                new Font("Arial",
                        Font.BOLD,
                        22)
        );

        titulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        add(titulo, gbc);

        gbc.gridwidth = 1;

        // produto
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Produto:"), gbc);

        gbc.gridx = 1;

        JLabel nomeLabel =
                new JLabel(nomeProduto);

        nomeLabel.setFont(
                new Font("Arial",
                        Font.BOLD,
                        16)
        );

        add(nomeLabel, gbc);

        // tamanho
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tamanho:"), gbc);

        String[] tamanhos =
                obterTamanhosDisponiveis(produtoId);

        if (tamanhos.length == 0) {
            tamanhoComboBox =
                    new JComboBox<>(
                            new String[]{"Sem estoque"}
                    );
            tamanhoComboBox.setEnabled(false);
        } else {
            tamanhoComboBox =
                    new JComboBox<>(tamanhos);
        }

        gbc.gridx = 1;
        add(tamanhoComboBox, gbc);

        // quantidade
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Quantidade:"), gbc);

        SpinnerModel model =
                new SpinnerNumberModel(
                        1,1,100,1
                );

        quantidadeSpinner =
                new JSpinner(model);

        gbc.gridx = 1;
        add(quantidadeSpinner, gbc);

        // botão
        adicionarAoCarrinhoButton =
                new JButton(
                        "Adicionar ao Carrinho"
                );

        if (tamanhos.length == 0) {
            adicionarAoCarrinhoButton
                    .setEnabled(false);
        }

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        add(adicionarAoCarrinhoButton, gbc);

        adicionarAoCarrinhoButton
                .addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(
                                    ActionEvent e) {

                                String tamanho =
                                        (String)
                                                tamanhoComboBox
                                                        .getSelectedItem();

                                if (tamanho == null ||
                                        "Sem estoque".equals(tamanho)) {
                                    JOptionPane.showMessageDialog(
                                            TelaSelecaoTamanho.this,
                                            "Produto sem estoque."
                                    );
                                    return;
                                }

                                int qtd =
                                        (int)
                                                quantidadeSpinner
                                                        .getValue();

                                SessaoUsuario sessao =
                                        SessaoUsuario
                                                .getInstance();

                                if (sessao.isUsuarioLogado()) {

                                    if (verificarEstoque(
                                            produtoId,
                                            tamanho,
                                            qtd)) {

                                        Carrinho.getInstance()
                                                .adicionarItem(
                                                        produtoId,
                                                        nomeProduto,
                                                        valorProduto,
                                                        tamanho,
                                                        qtd
                                                );

                                        JOptionPane.showMessageDialog(
                                                TelaSelecaoTamanho.this,
                                                "Produto adicionado!"
                                        );

                                        dispose();

                                    } else {
                                        JOptionPane.showMessageDialog(
                                                TelaSelecaoTamanho.this,
                                                "Estoque insuficiente."
                                        );
                                    }

                                } else {
                                    JOptionPane.showMessageDialog(
                                            TelaSelecaoTamanho.this,
                                            "Faça login para continuar."
                                    );
                                }
                            }
                        });
    }

    private String[] obterTamanhosDisponiveis(
            int produtoId) {

        List<String> tamanhos =
                new ArrayList<>();

        String sql =
                "SELECT ev.tamanho_descricao " +
                        "FROM estoque_variacoes ev " +
                        "JOIN produtos p ON ev.produto_id=p.id " +
                        "WHERE ev.produto_id=? " +
                        "AND ev.quantidade>0";

        try (Connection conn =
                     new DBConnector().conectar();

             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, produtoId);

            ResultSet rs =
                    pstmt.executeQuery();

            while (rs.next()) {
                tamanhos.add(
                        rs.getString(
                                "tamanho_descricao"
                        )
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );
        }

        return tamanhos.toArray(
                new String[0]
        );
    }

    private boolean verificarEstoque(
            int produtoId,
            String tamanhoDescricao,
            int quantidadeDesejada) {

        String sql =
                "SELECT quantidade " +
                        "FROM estoque_variacoes " +
                        "WHERE produto_id=? " +
                        "AND tamanho_descricao=?";

        try (Connection conn =
                     new DBConnector().conectar();

             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, produtoId);
            pstmt.setString(2,
                    tamanhoDescricao);

            ResultSet rs =
                    pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade")
                        >= quantidadeDesejada;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );
        }

        return false;
    }
}