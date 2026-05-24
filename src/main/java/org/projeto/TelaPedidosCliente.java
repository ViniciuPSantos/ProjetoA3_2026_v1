package org.projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaPedidosCliente extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private Integer clienteId;

    public TelaPedidosCliente() {
        configurarJanela();
        criarTabela();

        clienteId =
                SessaoUsuario
                        .getInstance()
                        .getUsuarioId();

        if (clienteId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum usuário logado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            carregarPedidos();
        }

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Meus Pedidos");
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        setSize(1000, 650);
        setLocationRelativeTo(null);

        Color fundo =
                new Color(180, 255, 180);

        JPanel painelPrincipal =
                new JPanel(
                        new BorderLayout(15, 15)
                );

        painelPrincipal.setBackground(fundo);

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 25, 25
                )
        );

        setContentPane(painelPrincipal);

        JLabel titulo =
                new JLabel("Meus Pedidos - EcoBazar");

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        titulo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );
    }

    private void criarTabela() {

        Color fundo =
                new Color(180, 255, 180);

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID Pedido",
                                "Data",
                                "Endereço",
                                "Pagamento",
                                "Total",
                                "Email"
                        },
                        0
                );

        pedidosTable =
                new JTable(tableModel);

        pedidosTable.setRowHeight(28);

        pedidosTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        pedidosTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

        JScrollPane scrollPane =
                new JScrollPane(
                        pedidosTable
                );

        JPanel tabelaPanel =
                new JPanel(
                        new BorderLayout()
                );

        tabelaPanel.setBackground(fundo);

        tabelaPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Histórico de Pedidos"
                )
        );

        tabelaPanel.add(scrollPane);

        add(
                tabelaPanel,
                BorderLayout.CENTER
        );
    }

    private void carregarPedidos() {

        if (clienteId == null)
            return;

        String sql =
                "SELECT id, data_pedido, endereco_entrega, forma_pagamento, total, email " +
                        "FROM pedidos WHERE usuario_id = ?";

        DBConnector dbConnector =
                new DBConnector();

        try (
                Connection conn =
                        dbConnector.conectar();

                PreparedStatement pstmt =
                        conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, clienteId);

            try (
                    ResultSet rs =
                            pstmt.executeQuery()
            ) {

                tableModel.setRowCount(0);

                while (rs.next()) {
                    tableModel.addRow(
                            new Object[]{
                                    rs.getInt("id"),
                                    rs.getString("data_pedido"),
                                    rs.getString("endereco_entrega"),
                                    rs.getString("forma_pagamento"),
                                    rs.getDouble("total"),
                                    rs.getString("email")
                            }
                    );
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar pedidos: "
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                TelaPedidosCliente::new
        );
    }
}