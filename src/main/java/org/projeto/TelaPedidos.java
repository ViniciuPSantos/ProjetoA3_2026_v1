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
        setTitle("Pedidos");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        pedidosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Adicionar as colunas da tabela
        tableModel.addColumn("ID Pedido");
        tableModel.addColumn("ID Usuário");
        tableModel.addColumn("Data Pedido");
        tableModel.addColumn("Status");
        tableModel.addColumn("Endereço Entrega");
        tableModel.addColumn("Forma Pagamento");
        tableModel.addColumn("Total");

        carregarPedidosDoBanco();

        setVisible(true);
    }

    private void carregarPedidosDoBanco() {
        String sql = "SELECT id, usuario_id, data_pedido, status, endereco_entrega, forma_pagamento, total FROM pedidos";
        DBConnector dbConnector = new DBConnector();

        try (Connection conn = dbConnector.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                int usuarioId = rs.getInt("usuario_id");
                java.sql.Timestamp dataPedido = rs.getTimestamp("data_pedido");
                String status = rs.getString("status");
                String enderecoEntrega = rs.getString("endereco_entrega");
                String formaPagamento = rs.getString("forma_pagamento");
                double total = rs.getDouble("total");
                tableModel.addRow(new Object[]{id, usuarioId, dataPedido, status, enderecoEntrega, formaPagamento, total});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
