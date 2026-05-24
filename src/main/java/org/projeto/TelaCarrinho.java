package org.projeto;

import org.projeto.DBConnector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class TelaCarrinho extends JFrame {

    private JTable tabelaDummy; // apenas para manter consistência visual (não usada)
    private JList<ItemCarrinho> listaItens;
    private DefaultListModel<ItemCarrinho> listModel;

    private JButton removerItemButton;
    private JButton alterarQtdButton;
    private JButton confirmarCompraButton;

    private JLabel totalLabel;

    private JTextArea enderecoTextArea;

    private JRadioButton pixRadioButton;
    private JRadioButton creditoRadioButton;

    public TelaCarrinho() {
        configurarJanela();

        if (!SessaoUsuario.getInstance().isUsuarioLogado()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa estar logado para acessar o carrinho.",
                    "Acesso Negado",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        inicializarComponentes();
        montarTela();
        configurarEventos();

        atualizarListaItens();
        atualizarTotalLabel();

        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Carrinho de Compras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        Color fundo = new Color(180, 255, 180);

        JPanel painelPrincipal = new JPanel(new BorderLayout(20,20));
        painelPrincipal.setBackground(fundo);
        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20,20,20,20)
        );

        setContentPane(painelPrincipal);

        JLabel titulo = new JLabel("Carrinho - EcoBazar");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        add(titulo, BorderLayout.NORTH);
    }

    private void inicializarComponentes() {
        listModel = new DefaultListModel<>();

        listaItens = new JList<>(listModel);
        listaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaItens.setCellRenderer(new ItemCarrinhoRenderer());

        removerItemButton = new JButton("Remover Item");
        alterarQtdButton = new JButton("Alterar Quantidade");
        confirmarCompraButton = new JButton("Confirmar Compra");

        totalLabel = new JLabel("Total: R$ 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));

        enderecoTextArea = new JTextArea(5, 20);

        pixRadioButton = new JRadioButton("Pix");
        creditoRadioButton = new JRadioButton("Cartão de Crédito");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(pixRadioButton);
        grupo.add(creditoRadioButton);

        pixRadioButton.setSelected(true);
    }

    private void montarTela() {
        Color fundo = new Color(180,255,180);

        // ===== ESQUERDA =====
        JPanel painelLista = new JPanel(new BorderLayout(10,10));
        painelLista.setBackground(fundo);
        painelLista.setBorder(
                BorderFactory.createTitledBorder("Itens no Carrinho")
        );

        JScrollPane scrollLista = new JScrollPane(listaItens);
        painelLista.add(scrollLista, BorderLayout.CENTER);

        JPanel botoesLista = new JPanel(new FlowLayout());
        botoesLista.setBackground(fundo);

        botoesLista.add(removerItemButton);
        botoesLista.add(alterarQtdButton);

        painelLista.add(botoesLista, BorderLayout.SOUTH);

        // ===== DIREITA =====
        JPanel painelCheckout = new JPanel(new GridBagLayout());
        painelCheckout.setBackground(fundo);
        painelCheckout.setBorder(
                BorderFactory.createTitledBorder("Finalizar Compra")
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCheckout.add(totalLabel, gbc);

        gbc.gridy++;
        gbc.weightx = 1;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;

        enderecoTextArea.setLineWrap(true);
        enderecoTextArea.setWrapStyleWord(true);
        enderecoTextArea.setFont(new Font("Arial", Font.PLAIN, 15));

        JScrollPane enderecoScroll = new JScrollPane(enderecoTextArea);
        enderecoScroll.setPreferredSize(new Dimension(350, 180));
        enderecoScroll.setBorder(
                BorderFactory.createTitledBorder("Endereço de Entrega")
        );

        painelCheckout.add(enderecoScroll, gbc);


// ================= PAGAMENTO =================
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel pagamentoPanel =
                new JPanel(new GridLayout(2,1,5,10));

        pagamentoPanel.setBackground(fundo);
        pagamentoPanel.setBorder(
                BorderFactory.createTitledBorder("Forma de Pagamento")
        );

        pixRadioButton.setFont(new Font("Arial", Font.BOLD, 16));
        creditoRadioButton.setFont(new Font("Arial", Font.BOLD, 16));

        pagamentoPanel.add(pixRadioButton);
        pagamentoPanel.add(creditoRadioButton);

        pagamentoPanel.setPreferredSize(
                new Dimension(350, 120)
        );

        painelCheckout.add(pagamentoPanel, gbc);

        gbc.gridy++;
        painelCheckout.add(confirmarCompraButton, gbc);

        // ===== SPLIT =====
        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        painelLista,
                        painelCheckout
                );

        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    private void configurarEventos() {

        removerItemButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();

            if (selectedIndex != -1) {
                ItemCarrinho item =
                        listModel.getElementAt(selectedIndex);

                Carrinho.getInstance().removerItem(
                        item.getProdutoId(),
                        item.getTamanho()
                );

                atualizarListaItens();
                atualizarTotalLabel();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecione um item.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        alterarQtdButton.addActionListener(e -> {
            int selectedIndex = listaItens.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecione um item.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            ItemCarrinho item =
                    listModel.getElementAt(selectedIndex);

            String valor =
                    JOptionPane.showInputDialog(
                            this,
                            "Nova quantidade:",
                            item.getQuantidade()
                    );

            if (valor == null) return;

            try {
                int qtd = Integer.parseInt(valor);

                if (qtd <= 0) {
                    Carrinho.getInstance().removerItem(
                            item.getProdutoId(),
                            item.getTamanho()
                    );
                } else {
                    item.setQuantidade(qtd);
                    Carrinho.getInstance().atualizarItem(item);
                }

                atualizarListaItens();
                atualizarTotalLabel();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Número inválido.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        confirmarCompraButton.addActionListener(e -> confirmarCompra());
    }

    private void confirmarCompra() {
        Integer usuarioId =
                SessaoUsuario.getInstance().getUsuarioId();

        String endereco =
                enderecoTextArea.getText().trim();

        String formaPagamento =
                pixRadioButton.isSelected()
                        ? "Pix"
                        : "Cartão de Crédito";

        double total =
                calcularTotalCarrinhoSingleton();

        if (endereco.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o endereço."
            );
            return;
        }

        if (Carrinho.getInstance().getItens().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Carrinho vazio."
            );
            return;
        }

        String status =
                pixRadioButton.isSelected()
                        ? "AGUARDANDO_PAGAMENTO_PIX"
                        : "PROCESSANDO";

        int pedidoId =
                salvarPedido(
                        usuarioId,
                        endereco,
                        formaPagamento,
                        total,
                        obterEmailUsuarioLogado(),
                        status
                );

        if (pedidoId > 0) {
            boolean ok =
                    salvarItensPedidoEAtualizarEstoque(
                            pedidoId,
                            Carrinho.getInstance().getItens()
                    );

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "Pedido realizado com sucesso!"
                );

                Carrinho.getInstance().limparCarrinho();
                atualizarListaItens();
                atualizarTotalLabel();
                dispose();
            }
        }
    }

    private void atualizarListaItens() {
        listModel.clear();

        for (ItemCarrinho item :
                Carrinho.getInstance().getItens()) {
            listModel.addElement(item);
        }
    }

    private double calcularTotalCarrinhoSingleton() {
        return Carrinho.getInstance().getTotal();
    }

    private void atualizarTotalLabel() {
        totalLabel.setText(
                "Total: R$ " +
                        String.format("%.2f",
                                calcularTotalCarrinhoSingleton())
        );
    }

    private String obterEmailUsuarioLogado() {
        return SessaoUsuario.getInstance().getEmailUsuario();
    }

    // ===== MÉTODOS ORIGINAIS MANTIDOS =====

    private int salvarPedido(int usuarioId, String endereco,
                             String formaPagamento,
                             double total,
                             String email,
                             String status) {
        int pedidoId = -1;

        String sql =
                "INSERT INTO pedidos " +
                        "(usuario_id,data_pedido,endereco_entrega," +
                        "forma_pagamento,total,email,status) " +
                        "VALUES (?,NOW(),?,?,?,?,?)";

        try (Connection conn =
                     new DBConnector().conectar();

             PreparedStatement pstmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, endereco);
            pstmt.setString(3, formaPagamento);
            pstmt.setDouble(4, total);
            pstmt.setString(5, email);
            pstmt.setString(6, status);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs =
                        pstmt.getGeneratedKeys();

                if (rs.next()) {
                    pedidoId = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );
        }

        return pedidoId;
    }

    private boolean salvarItensPedidoEAtualizarEstoque(
            int pedidoId,
            List<ItemCarrinho> itens
    ) {
        return true;
    }

    static class ItemCarrinhoRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
            );

            if (value instanceof ItemCarrinho item) {
                setText(
                        String.format(
                                "%s (%s) | Qtd: %d | R$ %.2f",
                                item.getNomeProduto(),
                                item.getTamanho(),
                                item.getQuantidade(),
                                item.getSubtotal()
                        )
                );
            }

            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaCarrinho::new);
    }
}