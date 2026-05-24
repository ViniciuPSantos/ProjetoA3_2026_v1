package org.projeto;

import org.projeto.DBConnector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaPedidos extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;

    public TelaPedidos() {
        configurarJanela();
        criarTabela();
        carregarPedidosDoBanco();
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Pedidos");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color fundo = new Color(180, 255, 180);

        JPanel painelPrincipal = new JPanel(
                new BorderLayout(15, 15)
        );

        painelPrincipal.setBackground(fundo);

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 25, 25
                )
        );

        setContentPane(painelPrincipal);

        JLabel titulo = new JLabel("Pedidos - EcoBazar");
        titulo.setFont(
                new Font("Arial", Font.BOLD, 24)
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

        Color fundo = new Color(180, 255, 180);

        tableModel = new DefaultTableModel();

        pedidosTable = new JTable(tableModel);

        pedidosTable.setRowHeight(28);

        pedidosTable.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        pedidosTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane =
                new JScrollPane(pedidosTable);

        JPanel tabelaPanel =
                new JPanel(new BorderLayout());

        tabelaPanel.setBackground(fundo);

        tabelaPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Lista de Pedidos"
                )
        );

        tabelaPanel.add(scrollPane);

        add(tabelaPanel, BorderLayout.CENTER);

        tableModel.addColumn("ID Pedido");
        tableModel.addColumn("ID Usuário");
        tableModel.addColumn("Data Pedido");
        tableModel.addColumn("Status");
        tableModel.addColumn("Endereço Entrega");
        tableModel.addColumn("Forma Pagamento");
        tableModel.addColumn("Total");
    }

    private void carregarPedidosDoBanco() {
        String sql =
                "SELECT id, usuario_id, data_pedido, status, endereco_entrega, forma_pagamento, total FROM pedidos";

        DBConnector dbConnector =
                new DBConnector();

        try (
                Connection conn =
                        dbConnector.conectar();

                PreparedStatement pstmt =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(
                        new Object[]{
                                rs.getInt("id"),
                                rs.getInt("usuario_id"),
                                rs.getTimestamp("data_pedido"),
                                rs.getString("status"),
                                rs.getString("endereco_entrega"),
                                rs.getString("forma_pagamento"),
                                rs.getDouble("total")
                        }
                );
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
                TelaPedidos::new
        );
    }
}