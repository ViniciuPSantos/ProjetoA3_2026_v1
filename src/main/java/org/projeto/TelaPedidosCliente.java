package org.projeto;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
// DriverManager não é mais necessário diretamente aqui para obter a conexão
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaPedidosCliente extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private Integer clienteId; // ID do cliente logado

    // 👇 Removidas as constantes de conexão daqui
    // private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    // private static final String DB_USER = "root";
    // private static final String DB_PASSWORD = "";

    public TelaPedidosCliente() {
        setTitle("Meus Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        clienteId = SessaoUsuario.getInstance().getUsuarioId();
        if (clienteId == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            // Considerar se a tela deve ser fechada ou se o usuário deve ser redirecionado para login
            // SwingUtilities.invokeLater(this::dispose); // Garante que dispose seja chamado no EDT
            // return; // Retornar aqui pode impedir a UI de ser totalmente construída se você não fechar
            // Se o usuário não estiver logado, talvez nem devesse abrir esta tela.
            // Ou desabilitar a funcionalidade de carregar pedidos.
            // Por agora, vamos permitir que a UI seja construída, mas vazia.
        }

        tableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Data", "Endereço", "Pagamento", "Total", "Email"}, 0);
        pedidosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);

        add(scrollPane, BorderLayout.CENTER);

        if (clienteId != null) { // Só carrega pedidos se o clienteId for válido
            carregarPedidos();
        } else {
            // Opcional: Exibir uma mensagem na tabela ou um label indicando que não há usuário logado
            tableModel.setRowCount(0); // Garante que a tabela esteja vazia
        }


        setVisible(true);
    }

    private void carregarPedidos() {
        if (clienteId == null) { // Verificação adicional para segurança
            return;
        }
        String sql = "SELECT id, data_pedido, endereco_entrega, forma_pagamento, total, email FROM pedidos WHERE usuario_id = ?";
        DBConnector dbConnector = new DBConnector(); // 👈 Instanciando seu conector

        // Usando try-with-resources para Connection e PreparedStatement
        try (Connection conn = dbConnector.conectar(); // 👈 Usando o método conectar()
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);

            try (ResultSet rs = pstmt.executeQuery()) { // 👈 ResultSet também no try-with-resources
                tableModel.setRowCount(0); // Limpa a tabela antes de carregar

                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("data_pedido"),
                            rs.getString("endereco_entrega"),
                            rs.getString("forma_pagamento"),
                            rs.getDouble("total"),
                            rs.getString("email")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
