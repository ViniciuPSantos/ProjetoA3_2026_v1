package org.projeto;


import org.projeto.DBConnector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TelaCarrinho extends JFrame {

    private JList<ItemCarrinho> listaItens;
    private DefaultListModel<ItemCarrinho> listModel;
    private JButton removerItemButton;
    private JButton alterarQtdButton;
    private JLabel totalLabel;
    private JTextArea enderecoTextArea;
    private JRadioButton pixRadioButton;
    private JRadioButton creditoRadioButton;
    private JButton confirmarCompraButton;

    public TelaCarrinho() {
        setTitle("Carrinho de Compras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600); // Mantido, ajuste se necessário
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (!SessaoUsuario.getInstance().isUsuarioLogado()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa estar logado para acessar o carrinho.",
                    "Acesso Negado",
                    JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::dispose);
            return;
        }

        listModel = new DefaultListModel<>();
        listaItens = new JList<>(listModel);
        listaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaItens.setCellRenderer(new ItemCarrinhoRenderer());

        JPanel listaPanel = new JPanel(new BorderLayout());
        listaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        listaPanel.add(new JScrollPane(listaItens), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removerItemButton = new JButton("Remover Item");
        alterarQtdButton = new JButton("Alterar Quantidade");
        botoesPanel.add(removerItemButton);
        botoesPanel.add(alterarQtdButton);
        listaPanel.add(botoesPanel, BorderLayout.SOUTH);

        JPanel checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        totalLabel = new JLabel("Total: R$ 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        enderecoTextArea = new JTextArea(5, 20);
        JScrollPane enderecoScrollPane = new JScrollPane(enderecoTextArea);
        enderecoScrollPane.setBorder(BorderFactory.createTitledBorder("Endereço de Entrega:"));
        enderecoScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pagamentoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagamentoPanel.setBorder(BorderFactory.createTitledBorder("Forma de Pagamento"));
        pixRadioButton = new JRadioButton("Pix");
        creditoRadioButton = new JRadioButton("Cartão de Crédito");
        ButtonGroup pagamentoGroup = new ButtonGroup();
        pagamentoGroup.add(pixRadioButton);
        pagamentoGroup.add(creditoRadioButton);
        pixRadioButton.setSelected(true);
        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(creditoRadioButton);
        pagamentoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmarCompraButton = new JButton("Confirmar Compra");
        confirmarCompraButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmarCompraButton.addActionListener(e -> {
            Integer usuarioIdAtual = SessaoUsuario.getInstance().getUsuarioId();
            if (usuarioIdAtual == null) {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Sessão expirada ou usuário não logado. Por favor, faça login novamente.", "Erro de Sessão", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String endereco = enderecoTextArea.getText().trim();
            String formaPagamento = pixRadioButton.isSelected() ? "Pix" : "Cartão de Crédito";
            double totalCompra = calcularTotalCarrinhoSingleton();

            if (endereco.isEmpty()) {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Por favor, preencha o endereço de entrega.", "Endereço Obrigatório", JOptionPane.WARNING_MESSAGE);
                enderecoTextArea.requestFocusInWindow();
                return;
            }
            if (Carrinho.getInstance().getItens().isEmpty()) {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "O carrinho está vazio.", "Carrinho Vazio", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ajustar o status do pedido com base na forma de pagamento
            String statusPedido = pixRadioButton.isSelected() ? "AGUARDANDO_PAGAMENTO_PIX" : "PROCESSANDO";
            int pedidoId = salvarPedido(usuarioIdAtual, endereco, formaPagamento, totalCompra, obterEmailUsuarioLogado(), statusPedido);

            if (pedidoId > 0) {
                boolean itensSalvosComEstoqueAtualizado = salvarItensPedidoEAtualizarEstoque(pedidoId, Carrinho.getInstance().getItens());
                if (itensSalvosComEstoqueAtualizado) {
                    // Lógica de exibir QR Code removida conforme sua desistência, mas a estrutura do IF permanece
                    if (pixRadioButton.isSelected()) {
                        JOptionPane.showMessageDialog(TelaCarrinho.this,
                                "Pedido Nº " + pedidoId + " realizado!\nStatus: Aguardando Pagamento via Pix." +
                                        "\nTotal: R$" + String.format("%.2f", totalCompra),
                                "Pedido Registrado", JOptionPane.INFORMATION_MESSAGE);
                    } else { // Para outras formas de pagamento
                        JOptionPane.showMessageDialog(TelaCarrinho.this,
                                "Compra confirmada com sucesso!\nNúmero do Pedido: " + pedidoId +
                                        "\nTotal: R$" + String.format("%.2f", totalCompra) +
                                        "\nPagamento: " + formaPagamento,
                                "Confirmação de Compra", JOptionPane.INFORMATION_MESSAGE);
                    }

                    Carrinho.getInstance().limparCarrinho();
                    atualizarListaItens();
                    atualizarTotalLabel();
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Obrigado pela sua compra!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao salvar os itens do pedido ou atualizar estoque. O pedido (ID: " + pedidoId + ") pode estar inconsistente. Contate o suporte.", "Erro Crítico na Compra", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Erro ao registrar o pedido. A compra não foi finalizada.", "Erro na Compra", JOptionPane.ERROR_MESSAGE);
            }
        });

        checkoutPanel.add(totalLabel);
        checkoutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        checkoutPanel.add(enderecoScrollPane);
        checkoutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        checkoutPanel.add(pagamentoPanel);
        checkoutPanel.add(Box.createVerticalStrut(20));
        checkoutPanel.add(confirmarCompraButton);
        checkoutPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(checkoutPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listaPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);

        removerItemButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();
            if (selectedIndex != -1) {
                ItemCarrinho removedItem = listModel.getElementAt(selectedIndex);
                Carrinho.getInstance().removerItem(removedItem.getProdutoId(), removedItem.getTamanho());
                atualizarListaItens();
                atualizarTotalLabel();
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Selecione um item para remover.", "Nenhum Item Selecionado", JOptionPane.WARNING_MESSAGE);
            }
        });

        alterarQtdButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();
            if (selectedIndex != -1) {
                ItemCarrinho itemParaAlterar = listModel.getElementAt(selectedIndex);
                String novoValorStr = JOptionPane.showInputDialog(TelaCarrinho.this,
                        "Digite a nova quantidade para " + itemParaAlterar.getNomeProduto() + " (" + itemParaAlterar.getTamanho() + "):",
                        itemParaAlterar.getQuantidade());
                if (novoValorStr != null) {
                    try {
                        int novaQuantidade = Integer.parseInt(novoValorStr);
                        if (novaQuantidade > 0) {
                            if (verificarEstoqueDisponivel(itemParaAlterar.getProdutoId(), itemParaAlterar.getTamanho(), novaQuantidade)) {
                                itemParaAlterar.setQuantidade(novaQuantidade);
                                Carrinho.getInstance().atualizarItem(itemParaAlterar);
                                atualizarListaItens();
                                atualizarTotalLabel();
                            } else {
                                JOptionPane.showMessageDialog(TelaCarrinho.this, "Estoque insuficiente para a quantidade desejada.", "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
                            }
                        } else if (novaQuantidade == 0) {
                            Carrinho.getInstance().removerItem(itemParaAlterar.getProdutoId(), itemParaAlterar.getTamanho());
                            atualizarListaItens();
                            atualizarTotalLabel();
                        } else {
                            JOptionPane.showMessageDialog(TelaCarrinho.this, "A quantidade deve ser um número positivo.", "Quantidade Inválida", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(TelaCarrinho.this, "Por favor, digite um número válido para a quantidade.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(TelaCarrinho.this, "Selecione um item para alterar a quantidade.", "Nenhum Item Selecionado", JOptionPane.WARNING_MESSAGE);
            }
        });

        atualizarListaItens();
        atualizarTotalLabel();
        setVisible(true);
    }

    // 👇 MÉTODO VERIFICAR ESTOQUE CORRIGIDO PARA OPÇÃO B
    private boolean verificarEstoqueDisponivel(int produtoId, String tamanhoDescricao, int quantidadeDesejada) {
        if (tamanhoDescricao == null || tamanhoDescricao.trim().isEmpty()) return false;

        DBConnector dbConnector = new DBConnector();
        String sql = "SELECT quantidade FROM estoque_variacoes WHERE produto_id = ? AND tamanho_descricao = ?";
        try (Connection conn = dbConnector.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            pstmt.setString(2, tamanhoDescricao.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade") >= quantidadeDesejada;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao verificar estoque: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Retorna false se não encontrar o item ou se ocorrer erro
    }

    private void atualizarListaItens() {
        listModel.clear();
        for (ItemCarrinho item : Carrinho.getInstance().getItens()) {
            listModel.addElement(item);
        }
    }

    private double calcularTotalCarrinhoSingleton() {
        return Carrinho.getInstance().getTotal();
    }

    private void atualizarTotalLabel() {
        totalLabel.setText("Total: R$ " + String.format("%.2f", calcularTotalCarrinhoSingleton()));
    }

    private String obterEmailUsuarioLogado() {
        return SessaoUsuario.getInstance().getEmailUsuario();
    }

    // Método salvarPedido MODIFICADO para aceitar 'status'
    private int salvarPedido(int usuarioId, String endereco, String formaPagamento, double total, String email, String status) {
        int pedidoId = -1;
        String sql = "INSERT INTO pedidos (usuario_id, data_pedido, endereco_entrega, forma_pagamento, total, email, status) VALUES (?, NOW(), ?, ?, ?, ?, ?)";
        DBConnector dbConnector = new DBConnector();

        try (Connection conn = dbConnector.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, endereco);
            pstmt.setString(3, formaPagamento);
            pstmt.setDouble(4, total);
            pstmt.setString(5, email);
            pstmt.setString(6, status); // Salva o status do pedido

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedidoId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar pedido: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return pedidoId;
    }

    // Renomeado para maior clareza e agora retorna boolean
    private boolean salvarItensPedidoEAtualizarEstoque(int pedidoId, List<ItemCarrinho> itens) {
        boolean sucessoGeral = true;
        String sqlInsertItem = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal, tamanho) VALUES (?, ?, ?, ?, ?, ?)";
        DBConnector dbConnector = new DBConnector();
        Connection conn = null; // Declarar fora do try para poder usar no finally e no catch para rollback

        try {
            conn = dbConnector.conectar();
            conn.setAutoCommit(false); // Inicia transação

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsertItem)) {
                for (ItemCarrinho item : itens) {
                    pstmtInsert.setInt(1, pedidoId);
                    pstmtInsert.setInt(2, item.getProdutoId());
                    pstmtInsert.setInt(3, item.getQuantidade());
                    pstmtInsert.setDouble(4, item.getValorUnitario());
                    pstmtInsert.setDouble(5, item.getSubtotal());
                    pstmtInsert.setString(6, item.getTamanho());
                    pstmtInsert.addBatch();
                }
                int[] results = pstmtInsert.executeBatch();
                for (int result : results) {
                    // Para batch, result pode ser Statement.SUCCESS_NO_INFO que é >= 0
                    // Um valor < 0 (geralmente Statement.EXECUTE_FAILED) indica falha.
                    if (result == Statement.EXECUTE_FAILED) {
                        sucessoGeral = false;
                        break;
                    }
                }
            } // pstmtInsert é fechado aqui

            if (sucessoGeral) {
                for (ItemCarrinho item : itens) {
                    atualizarEstoque(conn, item.getProdutoId(), item.getTamanho(), -item.getQuantidade());
                }
            }

            if (sucessoGeral) {
                conn.commit();
            } else {
                conn.rollback(); // Se o batch de insert falhou, ou se atualizarEstoque lançar exceção (que será pega no catch externo)
                // JOptionPane.showMessageDialog(this, "Falha ao salvar um ou mais itens do pedido. A transação foi desfeita.", "Erro na Transação", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Tenta rollback se uma SQLException ocorreu
                } catch (SQLException exRollback) {
                    System.err.println("Erro ao tentar rollback da transação: " + exRollback.getMessage());
                }
            }
            // A mensagem de erro da imagem é lançada por este bloco no código original,
            // se atualizarEstoque lançar "Tamanho inválido..."
            JOptionPane.showMessageDialog(this, "Erro crítico durante a transação de itens/estoque: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            sucessoGeral = false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaura autoCommit
                    conn.close(); // Fecha conexão
                } catch (SQLException exClose) {
                    System.err.println("Erro ao fechar conexão ou resetar autoCommit: " + exClose.getMessage());
                }
            }
        }
        return sucessoGeral;
    }

    // 👇 MÉTODO ATUALIZAR ESTOQUE TOTALMENTE CORRIGIDO PARA OPÇÃO B
    private void atualizarEstoque(Connection conn, int produtoId, String tamanhoDescricao, int quantidadeAlteracao) throws SQLException {
        // O parâmetro 'tamanhoDescricao' já é o valor correto (ex: "P", "M", "38", "40")
        if (tamanhoDescricao == null || tamanhoDescricao.trim().isEmpty()) {
            // Lançar SQLException aqui fará o rollback da transação em salvarItensPedidoEAtualizarEstoque
            throw new SQLException("Descrição do tamanho não pode ser vazia para atualização de estoque, Produto ID: " + produtoId);
        }

        String sql = "UPDATE estoque_variacoes SET quantidade = GREATEST(0, quantidade + ?) WHERE produto_id = ? AND tamanho_descricao = ?";
        // Usar GREATEST(0, ...) impede que o estoque fique negativo no banco.

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantidadeAlteracao); // quantidadeAlteracao será negativa para diminuir estoque
            pstmt.setInt(2, produtoId);
            pstmt.setString(3, tamanhoDescricao.trim());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // Se nenhuma linha for afetada, significa que a combinação produtoId + tamanhoDescricao não existe
                // na tabela estoque_variacoes, ou que o estoque já era 0 e tentamos diminuir (devido ao GREATEST(0,...)).
                // Para uma operação de VENDA (quantidadeAlteracao < 0), isso é um erro crítico se não for esperado.
                System.err.println("Aviso: Nenhuma linha afetada ao tentar atualizar estoque para produto ID " +
                        produtoId + ", tamanho '" + tamanhoDescricao + "'. " +
                        "Variação pode não existir ou estoque já em 0 (se diminuindo).");

                // DECISÃO IMPORTANTE: Lançar exceção aqui para forçar rollback se for uma diminuição e não encontrar?
                // Se for uma diminuição de estoque (venda) e a variação não for encontrada, é um problema de dados
                // ou lógica (tentando vender algo que não tem registro de estoque).
                if (quantidadeAlteracao < 0) { // Se estamos tentando diminuir o estoque
                    throw new SQLException("Falha ao atualizar estoque: Variação do produto (ID: " + produtoId +
                            ", Tamanho: " + tamanhoDescricao + ") não encontrada em estoque_variacoes ou estoque insuficiente para baixa.");
                }
            }
        }
        // Não há commit ou rollback aqui; isso é gerenciado pelo chamador (salvarItensPedidoEAtualizarEstoque)
    }

    static class ItemCarrinhoRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ItemCarrinho) {
                ItemCarrinho item = (ItemCarrinho) value;
                setText(String.format("ID %d: %s (%s) - Qtd: %d - Preço: R$ %.2f - Subtotal: R$ %.2f",
                        item.getProdutoId(), item.getNomeProduto(), item.getTamanho(),
                        item.getQuantidade(), item.getValorUnitario(), item.getSubtotal()));
            }
            return this;
        }
    }

    public static void main(String[] args) {
        // SessaoUsuario.getInstance().iniciarSessao(1, "Cliente Teste", "cliente", "teste@exemplo.com");
        SwingUtilities.invokeLater(() -> new TelaCarrinho());
    }
}


